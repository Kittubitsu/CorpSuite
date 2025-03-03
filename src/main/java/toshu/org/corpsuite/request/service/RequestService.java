package toshu.org.corpsuite.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.repository.RequestRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.RequestAdd;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

//    public Request addRequest(RequestAdd requestAdd) {
//
//        Request request = Request.builder()
//                .requester(requestAdd.getRequester())
//                .responsible(requestAdd.getResponsible())
//                .status(requestAdd.getStatus())
//                .type(requestAdd.getType())
//                .comment(requestAdd.getComment())
//                .fromDate(requestAdd.getFromDate())
//                .toDate(requestAdd.getToDate())
//                .totalDaysOff(requestAdd.getTotalDays())
//                .build();
//
//        return requestRepository.save(request);
//    }

    public List<Request> getAllRequests() {

        return requestRepository.findAll();
    }

    public List<Request> getAllByUser(User user) {
        return requestRepository.findAllByRequesterOrResponsible(user, user);
    }

}
