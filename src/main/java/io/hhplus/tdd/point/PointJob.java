package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.hhplus.tdd.point.TransactionType.*;

@Service
@RequiredArgsConstructor
public class PointJob {

    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    private final Lock lock = new ReentrantLock();


    public UserPoint getPoint(long id) {
        return pointService.getPoint(id);
    }

    public List<PointHistory> getHistories(long id) {
        return pointHistoryService.getPointHistories(id);
    }

    public UserPoint chargePoint(long id, long amount) {
        lock.lock();
        try {
            pointService.chargePoint(id, amount);
            pointHistoryService.insertHistory(id, amount, CHARGE);
        } finally {
            lock.unlock();
        }

        return pointService.getPoint(id);
    }

    public UserPoint usePoint(long id, long amount) {
        lock.lock();
        try {
            pointService.usePoint(id, amount);
            pointHistoryService.insertHistory(id, amount, USE);
        } finally {
            lock.unlock();
        }
        return pointService.getPoint(id);
    }

}
