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
import toshu.org.corpsuite.web.dto.RequestAdd;

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
    public Request addRequest(RequestAdd requestAdd, User requester, User responsible) {

        Request request = Request.builder()
                .requester(requester)
                .responsible(responsible)
                .status(requestAdd.getStatus())
                .type(requestAdd.getType())
                .comment(requestAdd.getComment())
                .fromDate(requestAdd.getFromDate())
                .toDate(requestAdd.getToDate())
                .totalDaysOff(requestAdd.getTotalDays())
                .build();

        userService.subtractUserPaidLeave(requester.getId(), requestAdd.getTotalDays());

        return requestRepository.save(request);
    }

    @Transactional
    public void editRequest(RequestAdd requestAdd, UUID requestId, User user) {

        Request request = getById(requestId);

        request.setComment(requestAdd.getComment());
        request.setType(requestAdd.getType());
        request.setStatus(requestAdd.getStatus());

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            userService.addUserPaidLeave(user.getId(), requestAdd.getTotalDays());
        }

        requestRepository.save(request);
    }

    public List<Request> getAllRequests() {

        return requestRepository.findAll();
    }

    public List<Request> getAllByUser(User user) {
        return requestRepository.findAllByRequesterOrResponsible(user, user);
    }

    public Request getById(UUID id) {

        return requestRepository.findById(id).orElseThrow(() -> new DomainException("No such request could be found!"));
    }
}
