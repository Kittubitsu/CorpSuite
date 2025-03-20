package toshu.org.corpsuite.web.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.model.CardType;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.model.TicketType;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.web.dto.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMapToComputerRequest_thenReturnCorrectObject() {

        User user = new User();

        Computer computer = Computer.builder()
                .computerName("WKS1")
                .age(1)
                .cpu("intel")
                .gpu("nvidia")
                .ram("16")
                .owner(user)
                .barcode("2020")
                .storage("2TB")
                .comment("test")
                .motherboard("asrock")
                .operatingSystem("windows")
                .active(true)
                .macAddress("175")
                .build();

        AddComputerRequest computerDto = DtoMapper.toComputerDto(computer);

        assertEquals(computer.getComputerName(), computerDto.getComputerName());
        assertEquals(computer.getAge(), computerDto.getAge());
        assertEquals(computer.getCpu(), computerDto.getCpu());
        assertEquals(computer.getGpu(), computerDto.getGpu());
        assertEquals(computer.getRam(), computerDto.getRam());
        assertEquals(computer.getOwner(), computerDto.getOwner());
        assertEquals(computer.getBarcode(), computerDto.getBarcode());
        assertEquals(computer.getStorage(), computerDto.getStorage());
        assertEquals(computer.getComment(), computerDto.getComment());
        assertEquals(computer.getOperatingSystem(), computerDto.getOperatingSystem());
        assertEquals(computer.isActive(), computerDto.getActive());
        assertEquals(computer.getMacAddress(), computerDto.getMacAddress());
        assertEquals(computer.getMotherboard(), computerDto.getMotherboard());
    }

    @Test
    void givenHappyPath_whenMapToTicketRequest_thenReturnCorrectObject() {
        User user = User.builder()
                .corporateEmail("toshu@abv.bg")
                .department(UserDepartment.IT)
                .build();

        Ticket ticket = Ticket.builder()
                .requester(user)
                .responsible(user)
                .comment("test")
                .status(TicketStatus.PENDING)
                .type(TicketType.OTHER)
                .build();

        AddTicketRequest ticketDto = DtoMapper.toTicketDto(ticket);

        assertEquals(ticket.getRequester().getCorporateEmail(), ticketDto.getRequester());
        assertEquals(ticket.getResponsible().getDepartment(), ticketDto.getDepartment());
        assertEquals(ticket.getComment(), ticketDto.getComment());
        assertEquals(ticket.getStatus(), ticketDto.getStatus());
        assertEquals(ticket.getType(), ticketDto.getType());
    }

    @Test
    void givenHappyPath_whenMapToAbsenceRequest_thenReturnCorrectObject() {
        User user = User.builder()
                .corporateEmail("toshu@abv.bg")
                .build();

        Request request = Request.builder()
                .requester(user)
                .responsible(user)
                .type(RequestType.PAID_LEAVE)
                .status(RequestStatus.PENDING)
                .comment("test")
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now())
                .totalDaysOff(1)
                .build();


        AddAbsenceRequest requestDto = DtoMapper.toRequestDto(request);

        assertEquals(request.getRequester().getCorporateEmail(), requestDto.getRequesterEmail());
        assertEquals(request.getResponsible().getCorporateEmail(), requestDto.getResponsibleEmail());
        assertEquals(request.getType(), requestDto.getType());
        assertEquals(request.getStatus(), requestDto.getStatus());
        assertEquals(request.getComment(), requestDto.getComment());
        assertEquals(request.getFromDate(), requestDto.getFromDate());
        assertEquals(request.getToDate(), requestDto.getToDate());
        assertEquals(request.getTotalDaysOff(), requestDto.getTotalDays());
    }

    @Test
    void givenHappyPath_whenMapToCardRequest_thenReturnCorrectObject() {
        Card card = Card.builder()
                .code("15")
                .type(CardType.IT)
                .active(true)
                .build();


        AddCardRequest cardDto = DtoMapper.toCardDto(card);

        assertEquals(card.getCode(), cardDto.getCode());
        assertEquals(card.getType(), cardDto.getType());
        assertEquals(card.isActive(), cardDto.getActive());
    }

    @Test
    void givenHappyPath_whenMapToUserRequest_thenReturnCorrectObject() {
        User user = User.builder()
                .firstName("Toshu")
                .lastName("Dimov")
                .corporateEmail("toshu@abv.bg")
                .card(new Card())
                .country("BG")
                .department(UserDepartment.IT)
                .active(true)
                .password("123")
                .position(UserPosition.MANAGER)
                .profilePicture("https://test.com")
                .build();

        EditUserRequest editUserDto = DtoMapper.toEditUserDto(user);

        assertEquals(user.getFirstName(), editUserDto.getFirstName());
        assertEquals(user.getLastName(), editUserDto.getLastName());
        assertEquals(user.getCorporateEmail(), editUserDto.getCorporateEmail());
        assertEquals(user.getCard(), editUserDto.getCard());
        assertEquals(user.getCountry(), editUserDto.getCountry());
        assertEquals(user.getDepartment(), editUserDto.getDepartment());
        assertEquals(user.isActive(), editUserDto.getActive());
        assertEquals(user.getPassword(), editUserDto.getPassword());
        assertEquals(user.getPosition(), editUserDto.getPosition());
        assertEquals(user.getProfilePicture(), editUserDto.getProfilePicture());
    }

}
