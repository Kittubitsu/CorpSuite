package toshu.org.corpsuite.web.mapper;

import lombok.experimental.UtilityClass;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.*;

@UtilityClass
public class DtoMapper {

    public static AddComputerRequest toComputerDto(Computer computer) {
        return AddComputerRequest.builder()
                .computerName(computer.getComputerName())
                .age(computer.getAge())
                .cpu(computer.getCpu())
                .gpu(computer.getGpu())
                .ram(computer.getRam())
                .owner(computer.getOwner())
                .barcode(computer.getBarcode())
                .storage(computer.getStorage())
                .comment(computer.getComment())
                .motherboard(computer.getMotherboard())
                .operatingSystem(computer.getOperatingSystem())
                .active(computer.isActive())
                .macAddress(computer.getMacAddress())
                .build();
    }

    public static AddTicketRequest toTicketDto(Ticket ticket) {
        return AddTicketRequest.builder()
                .requester(ticket.getRequester().getCorporateEmail())
                .department(ticket.getResponsible().getDepartment())
                .comment(ticket.getComment())
                .status(ticket.getStatus())
                .type(ticket.getType())
                .build();
    }

    public static AddAbsenceRequest toRequestDto(Request request) {
        return AddAbsenceRequest.builder()
                .requesterEmail(request.getRequester().getCorporateEmail())
                .responsibleEmail(request.getResponsible().getCorporateEmail())
                .type(request.getType())
                .status(request.getStatus())
                .comment(request.getComment())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .totalDays(request.getTotalDaysOff())
                .build();
    }

    public static AddCardRequest toCardDto(Card card) {
        return AddCardRequest.builder()
                .code(card.getCode())
                .type(card.getType())
                .active(card.isActive())
                .build();
    }
    
    public static EditUserRequest toEditUserDto(User user){
        return EditUserRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .corporateEmail(user.getCorporateEmail())
                .card(user.getCard())
                .country(user.getCountry())
                .department(user.getDepartment())
                .active(user.isActive())
                .password(user.getPassword())
                .position(user.getPosition())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
