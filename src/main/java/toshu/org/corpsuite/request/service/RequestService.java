package toshu.org.corpsuite.request.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.repository.RequestRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddAbsenceRequest;

import java.util.List;
import java.util.UUID;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    @Transactional
    public Request addRequest(AddAbsenceRequest addAbsenceRequest, User requester, User responsible) {

        Request request = Request.builder()
                .requester(requester)
                .responsible(responsible)
                .status(addAbsenceRequest.getStatus())
                .type(addAbsenceRequest.getType())
                .comment(addAbsenceRequest.getComment())
                .fromDate(addAbsenceRequest.getFromDate())
                .toDate(addAbsenceRequest.getToDate())
                .totalDaysOff(addAbsenceRequest.getTotalDays())
                .build();

        userService.subtractUserPaidLeave(requester.getId(), addAbsenceRequest.getTotalDays());

        return requestRepository.save(request);
    }

    @Transactional
    public void editRequest(AddAbsenceRequest addAbsenceRequest, UUID requestId, User user) {

        Request request = getById(requestId);

        request.setComment(addAbsenceRequest.getComment());
        request.setType(addAbsenceRequest.getType());
        request.setStatus(addAbsenceRequest.getStatus());

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            userService.addUserPaidLeave(user.getId(), addAbsenceRequest.getTotalDays());
        }

        requestRepository.save(request);
    }

    public List<Request> getAllRequests() {

        return requestRepository.findAll();
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
}
