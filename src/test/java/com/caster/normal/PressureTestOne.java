package com.caster.normal;

import com.caster.normal.util.ApacheHttpClient4Wrap;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author caster.hsu
 * @Since 2023/11/24
 */
public class PressureTestOne {
    private CyclicBarrier cyclicBarrier;
    private CyclicBarrier cyclicBarrierSecond;

    private static long startTime;

    public static void main(String[] args) throws InterruptedException {
        PressureTestOne one = new PressureTestOne();
        one.mainProcess(200);
    }

    class WorkerThread implements Runnable {
        @Override
        public void run() {
            try {
                String thisThreadName = Thread.currentThread().getName();
                ApacheHttpClient4Wrap client = new ApacheHttpClient4Wrap();
                cyclicBarrier.await();
                client.executeHttpGet("http://172.20.160.120:9000/list?threadName=" + thisThreadName, new HashMap<>());
                // 100 條
                cyclicBarrierSecond.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mainProcess(int totalWorkers) {
        cyclicBarrier = new CyclicBarrier(totalWorkers, () -> {
            startTime = System.currentTimeMillis();
        });

        cyclicBarrierSecond = new CyclicBarrier(totalWorkers, () -> {
            System.out.println("花費時間(ms) >>> " + (System.currentTimeMillis() - startTime));
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        for (int i = 0; i < totalWorkers; i++) {
            Thread worker = new Thread(new WorkerThread());
            worker.setName("Thread_" + i);
            worker.start();
        }
    }


}
