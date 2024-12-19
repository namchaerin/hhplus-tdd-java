package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointConcurrencyControlTest {

    private static PointJob pointJob;

    private static PointService pointService;

    private static PointHistoryService pointHistoryService;


    @DisplayName("포인트 충전 동시성 제어 테스트")
    @Test
    void concurrencyControlTestChargePointCase() throws InterruptedException {

        //given
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(1L, 0L);
        pointService = new PointService(userPointTable);

        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointHistoryService = new PointHistoryService(pointHistoryTable);

        pointJob = new PointJob(pointService, pointHistoryService);

        int threadCnt = 50;
        long userId = 1L;
        long initialBalance = pointJob.getPoint(userId).point(); // 초기 잔고

        //when
        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executor.submit(() -> {
                pointJob.chargePoint(userId, 10L);
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();

        //then
        long finalBalance = pointJob.getPoint(userId).point();
        long expectedBalance = initialBalance + (50 * 10);
        assertThat(finalBalance).isEqualTo(expectedBalance);

    }

    @DisplayName("포인트 사용 동시성 제어 테스트")
    @Test
    void concurrencyControlTestUsePointCase() throws InterruptedException {

        //given
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(1L, 1000L);
        pointService = new PointService(userPointTable);

        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointHistoryService = new PointHistoryService(pointHistoryTable);

        pointJob = new PointJob(pointService, pointHistoryService);

        int threadCnt = 50;
        long userId = 1L;
        long initialBalance = pointJob.getPoint(userId).point(); // 초기 잔고

        //when
        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executor.submit(() -> {
                pointJob.usePoint(userId, 20L);
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();

        //then
        long finalBalance = pointJob.getPoint(userId).point();
        long expectedBalance = initialBalance - (50 * 20);
        assertThat(finalBalance).isEqualTo(expectedBalance);

    }

    @DisplayName("포인트 충전/사용 동시성 제어 테스트")
    @Test
    void concurrencyControlTest() throws InterruptedException {

        //given
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(1L, 200L);
        pointService = new PointService(userPointTable);

        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointHistoryService = new PointHistoryService(pointHistoryTable);

        pointJob = new PointJob(pointService, pointHistoryService);

        int threadCnt = 100;
        long userId = 1L;
        long initialBalance = pointJob.getPoint(userId).point(); // 초기 잔고

        //when
        ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            if (i % 2 == 0) {
                executor.submit(() -> {
                    pointJob.chargePoint(userId, 20L);
                    latch.countDown();
                });  // 충전 작업
            } else {
                executor.submit(() -> {
                    pointJob.usePoint(userId, 10L);
                    latch.countDown();
                }); // 사용 작업
            }
        }
        latch.await();
        executor.shutdown();

        //then
        long finalBalance = pointJob.getPoint(userId).point();
        long expectedBalance = initialBalance + (50 * 20) - (50 * 10);
        assertThat(finalBalance).isEqualTo(expectedBalance);

    }


}
