package com.melexis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ProfilerTest {

    public List<Integer> generateRandomNumbers(int n) {
        final List<Integer> l = new ArrayList<Integer>(n);

        for (; n > 0; n--) {
            int el = (int) (Math.random() * 10000);
            l.add(el);
        }

        return l;
    }

    public int sum(List<Integer> numbers) {
        Integer sum = 0;
        for (Integer n : numbers) {
            sum += n;
        }
        return sum;
    }

    public int sumOfPows(List<Integer> numbers) {
        Integer sum = 0;
        for (Integer n : numbers) {
            sum += Double.valueOf(Math.pow(n, 2)).intValue();
        }
        return sum;
    }

    public int squareOfSumOfPows(List<Integer> numbers) {
        Integer sum = sumOfPows(numbers);
        return Double.valueOf(Math.sqrt(sum)).intValue();
    }


    public static void main1(String[] args) throws InterruptedException {
        final ProfilerTest pt = new ProfilerTest();

        final ForkJoinPool pool = new ForkJoinPool(10);

        for (int i = 0; i < 10; i++) {
            pool.submit(
                    new Thread() {
                        @Override
                        public void run() {
                            while (true) {
                                final List<Integer> ints = pt.generateRandomNumbers(100000);
                                System.out.println(Thread.currentThread().getId() + " Sum:\t\t" + pt.sum(ints));
                                System.out.println(Thread.currentThread().getId() + " Sum of pows:\t" + pt.sumOfPows(ints));
                                System.out.println(Thread.currentThread().getId() + " Sqrt of pows:\t" + pt.squareOfSumOfPows(ints));
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
        }

        pool.awaitTermination(100, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        final ProfilerTest pt = new ProfilerTest();
        final ForkJoinPool fjp = new ForkJoinPool(10);
        final Integer mutex = 1;

        for (int i=0; i<10; i++) {
            fjp.submit(new Thread() {
                @Override
                public void run() {
                    synchronized (mutex) {
                        for (int i=0; i<1000; i++) {
                            final List<Integer> ints = pt.generateRandomNumbers(100000);
                            System.out.println(Thread.currentThread().getId() + " Sum:\t\t" + pt.sum(ints));
                            System.out.println(Thread.currentThread().getId() + " Sum of pows:\t" + pt.sumOfPows(ints));
                            System.out.println(Thread.currentThread().getId() + " Sqrt of pows:\t" + pt.squareOfSumOfPows(ints));
                        }
                    }

                    final List<Integer> ints = pt.generateRandomNumbers(100);
                    System.out.println(Thread.currentThread().getId() + " Sum of pows:\t" + pt.sumOfPows(ints));
                }
            });

        }

        fjp.awaitTermination(100, TimeUnit.SECONDS);
    }
}
