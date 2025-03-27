package toshu.org.corpsuite.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import toshu.org.corpsuite.log.client.LogClient;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AbsenceRequest;
import toshu.org.corpsuite.web.dto.AddUserRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class AddRequestITest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @MockitoBean
    private LogClient logClient;

    @MockitoBean
    private LogService logService;

    @Test
    void whenNewRequestIsAdded_thenPaidLeaveIsSubtractedFromUser() {

        when(logClient.saveLog(any())).thenReturn(ResponseEntity.ok().body(null));

        User authUser = User.builder().corporateEmail("SYSTEM@TEST.TEST").build();

        AddUserRequest addUserRequest = AddUserRequest.builder()
                .firstName("test")
                .lastName("test")
                .department(UserDepartment.PROGRAMMING)
                .country("Bulgaria")
                .corporateEmail("test@test.test")
                .active(true)
                .password("testtest123123")
                .position(UserPosition.JUNIOR)
                .build();

        userService.addUser(addUserRequest, authUser);

        User user = userService.getByEmail("test@test.test");

        assertNotNull(user);

        AbsenceRequest absenceRequest = AbsenceRequest.builder()
                .type(RequestType.PAID_LEAVE)
                .totalDays(1)
                .comment("test")
                .status(RequestStatus.PENDING)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .requesterEmail(user.getCorporateEmail())
                .responsibleEmail(user.getCorporateEmail())
                .build();

        userService.subtractUserPaidLeave(user.getId(), absenceRequest.getTotalDays());

        User userActual = userService.getByEmail("test@test.test");

        assertEquals(19, userActual.getPaidLeaveCount());

        requestService.addRequest(absenceRequest, user, user);

        List<Request> requestList = requestService.getAllByUser(user, true);

        assertEquals(user.getCorporateEmail(), requestList.get(0).getRequester().getCorporateEmail());


    }
}
