package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserPointTable userPointTable;

    private String prefixUrl;


    @BeforeEach
    public void init() {
        prefixUrl = "/point";
    }

    @DisplayName("포인트 조회")
    @Test
    void getPointTest() throws Exception {

        //given
        long userId = 1L;

        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(prefixUrl + "/{id}", userId))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("포인트 이력 조회")
    @Test
    void getPointHistoryTest() throws Exception {

        //given
        long userId = 1L;

        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(prefixUrl + "/{id}/histories", userId))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("포인트 충전 테스트 - 01)충전 성공")
    @Test
    void chargePointTest() throws Exception {

        //given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 800L, System.currentTimeMillis());

        //when
        String request = objectMapper.writeValueAsString(100L);
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(prefixUrl + "/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("포인트 충전 테스트 - 02)최대 충전 가능 금액을 초과할 경우")
    @Test
    void chargePointTestWhenOverMaximumPoint() throws Exception {

        //given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 800L, System.currentTimeMillis());

        //when
        String request = objectMapper.writeValueAsString(500L);
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(prefixUrl + "/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @DisplayName("포인트 사용 테스트 - 1)사용 성공")
    @Test
    void usePointTestSuccess() throws Exception {

        //given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 800L, System.currentTimeMillis());

        //when
        String request = objectMapper.writeValueAsString(100L);
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(prefixUrl + "/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("포인트 사용 테스트 - 02)보유 포인트를 초과한 금액 사용시")
    @Test
    void chargePointTestWhenNotEnoughPoint() throws Exception {

        //given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 800L, System.currentTimeMillis());

        //when
        String request = objectMapper.writeValueAsString(1200L);
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(prefixUrl + "/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


}