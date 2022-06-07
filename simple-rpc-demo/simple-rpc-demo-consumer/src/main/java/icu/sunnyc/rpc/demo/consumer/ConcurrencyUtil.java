package icu.sunnyc.rpc.demo.consumer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 并发测试工具类
 * @author sunnyc
 * @version V1.0
 * @date 2022/6/7 10:31:18
 */
public class ConcurrencyUtil {

    public static <T> void concurrencyExecute(int threadNumber, Consumer<T> consumer, T t) {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(threadNumber);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        for (int i = 0; i < threadNumber; i++) {
            executorService.submit(() -> {
                try {
                    // 先阻塞这别让这个线程跑起来
                    start.await();
                    // 具体的业务方法（本地方法 or 远程调用）
                    consumer.accept(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
                    // 一个线程跑完 end计数器-1
                    end.countDown();
                }
            });
        }
        // start-1 所有线程启动，模拟并发
        start.countDown();
        // 阻塞直到所有线程执行完毕
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        executorService.shutdown();
    }
}
