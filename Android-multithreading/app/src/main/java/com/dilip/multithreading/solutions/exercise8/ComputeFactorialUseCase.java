package com.dilip.multithreading.solutions.exercise8;

import com.dilip.multithreading.common.BaseObservable;
import com.techyourchance.threadposter.BackgroundThreadPoster;
import com.techyourchance.threadposter.UiThreadPoster;

import java.math.BigInteger;

import androidx.annotation.WorkerThread;

public class ComputeFactorialUseCase extends BaseObservable<ComputeFactorialUseCase.Listener> {

    public interface Listener {
        void onFactorialComputed(BigInteger result);
        void onFactorialComputationTimedOut();
        void onFactorialComputationAborted();
    }

    private final Object LOCK = new Object();

    private final UiThreadPoster mUiThreadPoster;
    private final BackgroundThreadPoster mBackgroundThreadPoster;

    private int mNumberOfThreads;
    private ComputationRange[] mThreadsComputationRanges;
    private volatile BigInteger[] mThreadsComputationResults;
    private int mNumOfFinishedThreads = 0;

    private long mComputationTimeoutTime;

    private boolean mAbortComputation;

    public ComputeFactorialUseCase(UiThreadPoster uiThreadPoster, BackgroundThreadPoster backgroundThreadPoster) {
        mUiThreadPoster = uiThreadPoster;
        mBackgroundThreadPoster = backgroundThreadPoster;
    }

    @Override
    protected void onLastListenerUnregistered() {
        super.onLastListenerUnregistered();
        synchronized (LOCK) {
            mAbortComputation = true;
            LOCK.notifyAll();
        }
    }

    public void computeFactorialAndNotify(final int argument, final int timeout) {
        mBackgroundThreadPoster.post(() -> {
            initComputationParams(argument, timeout);
            startComputation();
            waitForThreadsResultsOrTimeoutOrAbort();
            processComputationResults();
        });
    }

    private void initComputationParams(int factorialArgument, int timeout) {
        mNumberOfThreads = factorialArgument < 20
                ? 1 : Runtime.getRuntime().availableProcessors();

        synchronized (LOCK) {
            mNumOfFinishedThreads = 0;
            mAbortComputation = false;
        }

        mThreadsComputationResults = new BigInteger[mNumberOfThreads];

        mThreadsComputationRanges = new ComputationRange[mNumberOfThreads];

        initThreadsComputationRanges(factorialArgument);

        mComputationTimeoutTime = System.currentTimeMillis() + timeout;
    }

    private void initThreadsComputationRanges(int factorialArgument) {
        int computationRangeSize = factorialArgument / mNumberOfThreads;

        long nextComputationRangeEnd = factorialArgument;
        for (int i = mNumberOfThreads - 1; i >= 0; i--) {
            mThreadsComputationRanges[i] = new ComputationRange(
                    nextComputationRangeEnd - computationRangeSize + 1,
                    nextComputationRangeEnd
            );
            nextComputationRangeEnd = mThreadsComputationRanges[i].start - 1;
        }

        // add potentially "remaining" values to first thread's range
        mThreadsComputationRanges[0].start = 1;
    }

    @WorkerThread
    private void startComputation() {
        for (int i = 0; i < mNumberOfThreads; i++) {

            final int threadIndex = i;

            mBackgroundThreadPoster.post(() -> {
                long rangeStart = mThreadsComputationRanges[threadIndex].start;
                long rangeEnd = mThreadsComputationRanges[threadIndex].end;
                BigInteger product = new BigInteger("1");
                for (long num = rangeStart; num <= rangeEnd; num++) {
                    if (isTimedOut()) {
                        break;
                    }
                    product = product.multiply(new BigInteger(String.valueOf(num)));
                }
                mThreadsComputationResults[threadIndex] = product;

                synchronized (LOCK) {
                    mNumOfFinishedThreads++;
                    LOCK.notifyAll();
                }

            });
        }
    }

    @WorkerThread
    private void waitForThreadsResultsOrTimeoutOrAbort() {
        synchronized (LOCK) {
            while (mNumOfFinishedThreads != mNumberOfThreads && !mAbortComputation && !isTimedOut()) {
                try {
                    LOCK.wait(getRemainingMillisToTimeout());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @WorkerThread
    private void processComputationResults() {
        if (mAbortComputation) {
            notifyAborted();
            return;
        }

        BigInteger result = computeFinalResult();

        // need to check for timeout after computation of the final result
        if (isTimedOut()) {
            notifyTimeout();
            return;
        }

        notifySuccess(result);
    }

    @WorkerThread
    private BigInteger computeFinalResult() {
        BigInteger result = new BigInteger("1");
        for (int i = 0; i < mNumberOfThreads; i++) {
            if (isTimedOut()) {
                break;
            }
            result = result.multiply(mThreadsComputationResults[i]);
        }
        return result;
    }

    private long getRemainingMillisToTimeout() {
        return mComputationTimeoutTime - System.currentTimeMillis();
    }

    private boolean isTimedOut() {
        return System.currentTimeMillis() >= mComputationTimeoutTime;
    }

    private void notifySuccess(final BigInteger result) {
        mUiThreadPoster.post(() -> {
            for (Listener listener : getListeners()) {
                listener.onFactorialComputed(result);
            }
        });
    }

    private void notifyAborted() {
        mUiThreadPoster.post(() -> {
            for (Listener listener : getListeners()) {
                listener.onFactorialComputationAborted();
            }
        });
    }

    private void notifyTimeout() {
        mUiThreadPoster.post(() -> {
            for (Listener listener : getListeners()) {
                listener.onFactorialComputationTimedOut();
            }
        });
    }


    private static class ComputationRange {
        private long start;
        private long end;

        public ComputationRange(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
}
