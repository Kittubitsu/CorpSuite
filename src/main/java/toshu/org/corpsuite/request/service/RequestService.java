package toshu.org.corpsuite.request.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.repository.RequestRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AbsenceRequest;

import java.util.List;
import java.util.UUID;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public RequestService(RequestRepository requestRepository, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void addRequest(AbsenceRequest absenceRequest, User requester, User responsible) {

        Request request = Request.builder()
                .requester(requester)
                .responsible(responsible)
                .status(absenceRequest.getStatus())
                .type(absenceRequest.getType())
                .comment(absenceRequest.getComment())
                .fromDate(absenceRequest.getFromDate())
                .toDate(absenceRequest.getToDate())
                .totalDaysOff(absenceRequest.getTotalDays())
                .build();

        userService.subtractUserPaidLeave(requester.getId(), absenceRequest.getTotalDays());

        Request saved = requestRepository.save(request);

        LogRequest logRequest = LogRequest.builder()
                .email(requester.getCorporateEmail())
                .action("CREATE")
                .module("Request")
                .comment("Request created with id [%s]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }

    @Transactional
    public void editRequest(AbsenceRequest absenceRequest, UUID requestId, User user) {

        Request request = getById(requestId);

        request.setComment(absenceRequest.getComment());
        request.setType(absenceRequest.getType());
        request.setStatus(absenceRequest.getStatus());

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            userService.addUserPaidLeave(user.getId(), absenceRequest.getTotalDays());
        }

        requestRepository.save(request);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Request")
                .comment("Request edited with id [%s]".formatted(requestId))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }

    public List<Request> getAllByUser(User user, Boolean show) {
        List<Request> requestList = requestRepository.findAllByRequesterOrResponsible(user, user);
        if (!show) {
            return requestList.stream().filter(request -> request.getStatus().equals(RequestStatus.PENDING)).toList();
        }
        return requestList;
    }

    public Request getById(UUID id) {
        return requestRepository.findById(id).orElseThrow(() -> new DomainException("No such request could be found!"));
    }

    public List<Request> getFirstThreePendingRequestsByUser(User user) {
        return requestRepository.findAllByRequesterOrResponsible(user, user).stream().filter(request -> request.getStatus().equals(RequestStatus.PENDING)).limit(3).toList();
    }
}
