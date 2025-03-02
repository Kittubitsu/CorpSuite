package toshu.org.corpsuite.web.mapper;

import lombok.experimental.UtilityClass;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.web.dto.ComputerAdd;
import toshu.org.corpsuite.web.dto.TicketAdd;

@UtilityClass
public class dtoMapper {

    public static ComputerAdd toComputerDto(Computer computer) {
        return ComputerAdd.builder()
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
                .isActive(computer.isActive())
                .macAddress(computer.getMacAddress())
                .build();
    }

    public static TicketAdd toTicketDto(Ticket ticket) {
        return TicketAdd.builder()
                .requester(ticket.getRequester().getCorporateEmail())
                .department(ticket.getResponsible().getDepartment())
                .comment(ticket.getComment())
                .status(ticket.getStatus())
                .type(ticket.getType())
                .build();
    }
}
