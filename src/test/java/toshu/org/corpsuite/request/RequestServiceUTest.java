package toshu.org.corpsuite.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import toshu.org.corpsuite.request.repository.RequestRepository;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AbsenceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private RequestService requestService;

    @Test
    void givenMissingRequest_whenGetById_thenThrows() {

        UUID id = UUID.randomUUID();

        when(requestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> requestService.getById(id));
        verify(requestRepository, times(1)).findById(any());

    }

    @Test
    void givenExistingRequest_whenGetById_thenReturnSuccessfully() {

        Request request = Request.builder()
                .id(UUID.randomUUID())
                .build();

        when(requestRepository.findById(any())).thenReturn(Optional.of(request));

        Request request1 = requestService.getById(request.getId());

        assertEquals(request1.getId(), request.getId());
        verify(requestRepository, times(1)).findById(any());

    }

    @Test
    void givenBoolTrue_whenGettingAllRequestsByUser_thenReturnAllUnfiltered() {

        List<Request> requestList = new ArrayList<>(List.of(Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.APPROVED).build()));

        when(requestRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(requestList);

        List<Request> expectedRequestList = requestService.getAllByUser(new User(), true);

        assertEquals(requestList.size(), expectedRequestList.size());
        verify(requestRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenBoolFalse_whenGettingAllRequestsByUser_thenReturnFilteredByPendingOnlyStatus() {

        List<Request> requestList = new ArrayList<>(List.of(Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.APPROVED).build()));

        when(requestRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(requestList);

        List<Request> actualRequestList = requestService.getAllByUser(new User(), false);

        assertEquals(requestList.size() - 1, actualRequestList.size());
        verify(requestRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenFiveRequests_whenGettingFirstThreePendingByUser_thenReturnOnlyFirstThree() {
        List<Request> requestList = new ArrayList<>(List.of(Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.PENDING).build(), Request.builder().status(RequestStatus.APPROVED).build(), Request.builder().status(RequestStatus.PENDING).build()));

        when(requestRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(requestList);

        List<Request> actualRequests = requestService.getFirstThreePendingRequestsByUser(new User());

        assertEquals(requestList.size() - 1, actualRequests.size());
        verify(requestRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenNewRequest_whenAddingRequest_thenCompletesSuccessfully() {

        AbsenceRequest absenceRequest = AbsenceRequest.builder().totalDays(1).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();
        Request request = Request.builder().id(UUID.randomUUID()).build();

        when(requestRepository.save(any())).thenReturn(request);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));
        doNothing().when(userService).subtractUserPaidLeave(user.getId(), absenceRequest.getTotalDays());

        requestService.addRequest(absenceRequest, user, user);

        verify(requestRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
        verify(userService, times(1)).subtractUserPaidLeave(any(UUID.class), any(Integer.class));
    }

    @Test
    void givenExistingRequest_whenEditingRequest_thenCompletedSuccessfully() {

        AbsenceRequest absenceRequest = AbsenceRequest.builder().totalDays(1).comment("Egg").status(RequestStatus.PENDING).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();
        Request request = Request.builder().id(UUID.randomUUID()).build();

        when(requestRepository.save(any())).thenReturn(request);
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        requestService.editRequest(absenceRequest, UUID.randomUUID(), user);

        assertEquals(absenceRequest.getComment(), request.getComment());
        assertEquals(absenceRequest.getStatus(), request.getStatus());
        verify(requestRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
    }

    @Test
    void givenExistingRequest_whenEditingRequestAndSettingStatusRejected_thenCompletedSuccessfully() {

        AbsenceRequest absenceRequest = AbsenceRequest.builder().totalDays(1).comment("Egg").status(RequestStatus.REJECTED).type(RequestType.PAID_LEAVE).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();
        Request request = Request.builder().id(UUID.randomUUID()).build();

        when(requestRepository.save(any())).thenReturn(request);
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));
        doNothing().when(userService).addUserPaidLeave(any(UUID.class), any(Integer.class));

        requestService.editRequest(absenceRequest, UUID.randomUUID(), user);

        assertEquals(absenceRequest.getComment(), request.getComment());
        assertEquals(absenceRequest.getStatus(), request.getStatus());
        assertEquals(absenceRequest.getType(), request.getType());
        verify(requestRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
        verify(userService, times(1)).addUserPaidLeave(any(UUID.class), any(Integer.class));
    }


}
