package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointHistoryService pointHistoryService;

    private List<PointHistory> pointHistories;

    @BeforeEach
    void setup() {
        pointHistories = Arrays.asList(
                new PointHistory(1L, 1L, 100L, CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, 1L, 500L, CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, 1L, 200L, USE, System.currentTimeMillis()),
                new PointHistory(4L, 1L, 400L, CHARGE, System.currentTimeMillis()));

    }

    @DisplayName("포인트 충전/이용 내역 조회 성공 테스트")
    @Test
    void getPointHistoriesTest() {

        //given
        long testUserId = 1L;
        when(pointHistoryTable.selectAllByUserId(testUserId)).thenReturn(pointHistories);

        //when
        List<PointHistory> result = pointHistoryService.getPointHistories(testUserId);

        //then
        assertThat(result.size()).isEqualTo(4);

    }

    @DisplayName("포인트 충전/이용 내역 조회 실패 테스트")
    @Test
    void getPointHistoriesTestWhenNonExistUser() {

        //given
        long testUserId = 2L;
        when(pointHistoryTable.selectAllByUserId(testUserId)).thenThrow(new NullPointerException());

        //when

        //then
        Assertions.assertThatThrownBy(() -> pointHistoryService.getPointHistories(testUserId)).isInstanceOf(NullPointerException.class);

    }


}