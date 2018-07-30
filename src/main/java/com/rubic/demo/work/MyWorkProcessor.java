package com.rubic.demo.work;

import com.lmax.disruptor.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MyWorkProcessor：工作流水线处理器
 *
 * @author rubic
 * @since 2018-07-18
 */
public final class MyWorkProcessor<T> implements EventProcessor {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Sequence sequence = new Sequence(-1L);
    private final RingBuffer<T> ringBuffer;
    private final SequenceBarrier sequenceBarrier;
    private final WorkHandler<? super T> workHandler;
    private final ExceptionHandler<? super T> exceptionHandler;
    private final Sequence workSequence;
    private final EventReleaser eventReleaser = new EventReleaser() {
        public void release() {
            MyWorkProcessor.this.sequence.set(9223372036854775807L);
        }
    };

    private final TimeoutHandler timeoutHandler;

    public MyWorkProcessor(RingBuffer<T> ringBuffer, SequenceBarrier sequenceBarrier, WorkHandler<? super T> workHandler, ExceptionHandler<? super T> exceptionHandler, Sequence workSequence) {
        this.ringBuffer = ringBuffer;
        this.sequenceBarrier = sequenceBarrier;
        this.workHandler = workHandler;
        this.exceptionHandler = exceptionHandler;
        this.workSequence = workSequence;
        if (this.workHandler instanceof EventReleaseAware) {
            ((EventReleaseAware)this.workHandler).setEventReleaser(this.eventReleaser);
        }

        this.timeoutHandler = workHandler instanceof TimeoutHandler ? (TimeoutHandler)workHandler : null;
    }

    public Sequence getSequence() {
        return this.sequence;
    }

    public void halt() {
        this.running.set(false);
        this.sequenceBarrier.alert();
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public void run() {
        if (!this.running.compareAndSet(false, true)) {
            throw new IllegalStateException("Thread is already running");
        } else {
            this.sequenceBarrier.clearAlert();
            this.notifyStart();
            boolean processedSequence = true;
            long cachedAvailableSequence = -9223372036854775808L;
            long nextSequence = this.sequence.get();
            T event = null;
            int i = 0;

            while (true) {
                while(i < 256) {
                    i++;
                    try {
                        if (processedSequence) {
                            processedSequence = false;

                            do {
                                nextSequence = this.workSequence.get() + 1L;
                                this.sequence.set(nextSequence - 1L);
                            } while(!this.workSequence.compareAndSet(nextSequence - 1L, nextSequence));
                        }

                        if (cachedAvailableSequence >= nextSequence) {
                            event = this.ringBuffer.get(nextSequence);
                            this.workHandler.onEvent(event);
                            processedSequence = true;
                        } else {
                            cachedAvailableSequence = this.sequenceBarrier.waitFor(nextSequence);
                        }
                    } catch (TimeoutException var8) {
                        this.notifyTimeout(this.sequence.get());
                    } catch (AlertException var9) {
                        if (!this.running.get()) {
                            this.notifyShutdown();
                            this.running.set(false);
                            return;
                        }
                    } catch (Throwable var10) {
                        this.exceptionHandler.handleEventException(var10, nextSequence, event);
                        processedSequence = true;
                    }
                }
            }

        }
    }

    private void notifyTimeout(long availableSequence) {
        try {
            if (this.timeoutHandler != null) {
                this.timeoutHandler.onTimeout(availableSequence);
            }
        } catch (Throwable var4) {
            this.exceptionHandler.handleEventException(var4, availableSequence, null);
        }

    }

    private void notifyStart() {
        if (this.workHandler instanceof LifecycleAware) {
            try {
                ((LifecycleAware)this.workHandler).onStart();
            } catch (Throwable var2) {
                this.exceptionHandler.handleOnStartException(var2);
            }
        }

    }

    private void notifyShutdown() {
        if (this.workHandler instanceof LifecycleAware) {
            try {
                ((LifecycleAware)this.workHandler).onShutdown();
            } catch (Throwable var2) {
                this.exceptionHandler.handleOnShutdownException(var2);
            }
        }

    }
}
