package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;


    public List<PointHistory> getPointHistories(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        if (Objects.isNull(pointHistories)) throw new NullPointerException("해당 유저에 대한 포인트 내역이 없습니다.");

        return pointHistories;
    }

    public void insertHistory(long id, long amount, TransactionType type) {
        pointHistoryTable.insert(id, amount, type, System.currentTimeMillis());
    }


}
