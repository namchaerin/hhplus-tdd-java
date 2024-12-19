package io.hhplus.tdd.point;

import io.hhplus.exceptions.MaximumPointsExceededException;
import io.hhplus.exceptions.NotEnoughPointsException;
import io.hhplus.tdd.database.UserPointTable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    private UserPoint userPoint;


    @BeforeEach
    void setUp() {
        userPoint = new UserPoint(1L, 800, 200);
    }

    @Test
    @DisplayName("포인트 조회 성공 테스트 - 유저가 존재하는 경우")
    void getPointsTestWhenExistUser() {

        //given
        long testUserId = 1L;
        when(userPointTable.selectById(testUserId)).thenReturn(userPoint);

        //when
        UserPoint result = pointService.getPoint(testUserId);

        //then
        assertThat(result.point()).isEqualTo(800L);

    }

    @Test
    @DisplayName("포인트 조회 성공 테스트 - 유저가 존재하지 않는 경우")
    void getPointsTestWhenNotExistUser() {

        //given
        long testUserId = 2L;
        when(userPointTable.selectById(testUserId)).thenReturn(UserPoint.empty(testUserId));

        //when
        UserPoint result = pointService.getPoint(testUserId);

        //then
        assertThat(result.point()).isEqualTo(0L);

    }

    @Test
    @DisplayName("포인트 충전 테스트 - 최대 잔고를 넘지 않는 경우")
    void chargeTestWhenSumAmountUnderMaximumBalance() {

        //given
        long testUserId = 1L;
        long testAmount = 200L;
        long expectAmount = Math.addExact(userPoint.point(), testAmount);

        //when
        when(userPointTable.selectById(testUserId)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(testUserId, expectAmount)).thenReturn(new UserPoint(testUserId, expectAmount, System.currentTimeMillis()));
        UserPoint result = pointService.chargePoint(testUserId, testAmount);

        //then
        assertThat(result.point()).isEqualTo(expectAmount);

    }

    @Test
    @DisplayName("포인트 충전 테스트 - 최대 잔고를 넘는 경우")
    void chargeTestWhenSumAmountOverMaximumBalance() {

        //given
        long testUserId = 1L;
        long testAmount = 300L;

        //when
        when(userPointTable.selectById(testUserId)).thenReturn(userPoint);

        //then
        Assertions.assertThatThrownBy(() -> pointService.chargePoint(testUserId, testAmount)).isInstanceOf(MaximumPointsExceededException.class);

    }

    @Test
    @DisplayName("포인트 사용 테스트")
    void useTest() {

        //given
        long testUserId = 1L;
        long testAmount = 300L;
        long expectAmount = Math.subtractExact(userPoint.point(), testAmount);

        //when
        when(userPointTable.selectById(testUserId)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(testUserId, expectAmount)).thenReturn(new UserPoint(testUserId, expectAmount, System.currentTimeMillis()));
        UserPoint result = pointService.usePoint(testUserId, testAmount);

        //then
        assertThat(result.point()).isEqualTo(expectAmount);

    }

    @Test
    @DisplayName("포인트 사용 테스트 - 포인트 부족한 경우")
    void useTestWhenHasNotEnoughPoint() {

        //given
        long testUserId = 1L;
        long testAmount = 1000L;

        //when
        when(userPointTable.selectById(testUserId)).thenReturn(userPoint);

        //then
        Assertions.assertThatThrownBy(() -> pointService.usePoint(testUserId, testAmount)).isInstanceOf(NotEnoughPointsException.class);

    }


}