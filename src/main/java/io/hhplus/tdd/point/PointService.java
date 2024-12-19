package io.hhplus.tdd.point;

import io.hhplus.exceptions.MaximumPointsExceededException;
import io.hhplus.exceptions.NotEnoughPointsException;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;

    private static final long MAX_BALANCE = 1000L;


    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint chargePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        long sumAmount = Math.addExact(userPoint.point(), amount);
        if (sumAmount > MAX_BALANCE) {
            long availableAmount = Math.subtractExact(MAX_BALANCE, userPoint.point());
            throw new MaximumPointsExceededException(String.format("충전 가능 최대 포인트를 초과합니다. 현재 충전 가능한 포인트는 %d 포인트 입니다.", availableAmount));
        }
        return userPointTable.insertOrUpdate(id, sumAmount);
    }

    public UserPoint usePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint.point() < amount)
            throw new NotEnoughPointsException("보유하신 포인트가 부족합니다.");

        return userPointTable.insertOrUpdate(id, Math.subtractExact(userPoint.point(), amount));
    }

}
