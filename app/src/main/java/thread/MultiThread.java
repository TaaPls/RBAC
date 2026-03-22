package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThread {
    private static final int THREAD_COUNT = 4;
    private static final int CALCULATION_LENGTH = 30;
    private static final long STEP_DELAY_MS = 150;

    private static final CountDownLatch startLatch = new CountDownLatch(1);

    private static final AtomicInteger threadCounter = new AtomicInteger(1);

    private static final List<ThreadInfo> threadInfos = new ArrayList<>();

    private static final Object outputLock = new Object();

    public static void main(String[] args) {
        System.out.println("Thread count: " + THREAD_COUNT + ", computation length: " + CALCULATION_LENGTH + " steps");
        System.out.println();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            ThreadInfo threadInfo = new ThreadInfo(threadCounter.getAndIncrement());
            threadInfos.add(threadInfo);

            Thread thread = new Thread(new CalculationTask(threadInfo));
            threads.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(500);
            startLatch.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nAll threads joined");
    }

    static class ThreadInfo {
        final int threadNumber;
        long threadId;
        final StringBuilder progressBar;
        long startTime;
        long endTime;

        ThreadInfo(int threadNumber) {
            this.threadNumber = threadNumber;
            this.threadId = Thread.currentThread().threadId();
            this.progressBar = new StringBuilder();
        }

        void updateThreadId() {
            try {
                threadId = Thread.currentThread().threadId();
            } catch (Exception ignored) {
            }
        }

        String getProgressBar() {
            synchronized (progressBar) {
                return progressBar.toString();
            }
        }

        void appendToProgressBar() {
            synchronized (progressBar) {
                progressBar.append('#');
            }
        }
    }

    static class CalculationTask implements Runnable {
        private final ThreadInfo threadInfo;

        CalculationTask(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            try {
                startLatch.await();

                threadInfo.updateThreadId();

                threadInfo.startTime = System.currentTimeMillis();

                printProgress();

                // Имитируем расчёт
                for (int i = 0; i < CALCULATION_LENGTH; i++) {
                    Thread.sleep(STEP_DELAY_MS);

                    threadInfo.appendToProgressBar();

                    printProgress();
                }

                threadInfo.endTime = System.currentTimeMillis();

                printProgress();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void printProgress() {
            synchronized (outputLock) {
                System.out.print("\033[H\033[2J");
                System.out.flush();

                System.out.println("Multi thread calculation: (steps: " + CALCULATION_LENGTH + ")");
                System.out.println("========================================");

                for (ThreadInfo info : threadInfos) {
                    System.out.printf("Thread: %2d | ", info.threadNumber);

                    System.out.printf("ID: %3d | ", info.threadId);

                    String progressBar = info.getProgressBar();
                    int currentLength = progressBar.length();

                    int percent = (currentLength * 100) / CALCULATION_LENGTH;
                    System.out.printf("[%s] %3d%%", progressBar + " ".repeat(Math.max(0, CALCULATION_LENGTH - currentLength))
                            , percent);

                    if (info.endTime > 0) {
                        long duration = info.endTime - info.startTime;
                        System.out.printf(" | Time: %d ms", duration);
                    }

                    System.out.println();
                }

                System.out.println("========================================");
            }
        }
    }
}
