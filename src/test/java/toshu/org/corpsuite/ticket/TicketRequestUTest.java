package toshu.org.corpsuite.ticket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.model.TicketType;
import toshu.org.corpsuite.ticket.repository.TicketRepository;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.AddTicketRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketRequestUTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private TicketService ticketService;


    @Test
    void givenFiveTickets_whenGettingFirstThreePendingByUser_thenReturnOnlyFirstThree() {
        List<Ticket> ticketList = new ArrayList<>(List.of(
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.PENDING).build()));

        when(ticketRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(ticketList);

        List<Ticket> actualTickets = ticketService.getFirstThreePendingTicketsByUser(new User());

        assertEquals(ticketList.size() - 1, actualTickets.size());
        verify(ticketRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenMissingTicket_whenGetById_thenThrows() {

        UUID id = UUID.randomUUID();

        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> ticketService.getById(id));
        verify(ticketRepository, times(1)).findById(any());

    }

    @Test
    void givenExistingTicket_whenGetById_thenReturnSuccessfully() {

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .build();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        Ticket actualTicket = ticketService.getById(ticket.getId());

        assertEquals(actualTicket.getId(), ticket.getId());
        verify(ticketRepository, times(1)).findById(any());

    }

    @Test
    void givenBoolTrue_whenGettingAllTicketsByUser_thenReturnAllUnfiltered() {

        List<Ticket> ticketList = new ArrayList<>(List.of(
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.COMPLETED).build()));

        when(ticketRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(ticketList);

        List<Ticket> expectedTicketList = ticketService.getAllByUser(new User(), true);

        assertEquals(ticketList.size(), expectedTicketList.size());
        verify(ticketRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenBoolFalse_whenGettingAllTicketsByUser_thenReturnFilteredByPendingOnlyStatus() {

        List<Ticket> ticketList = new ArrayList<>(List.of(
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.PENDING).build(),
                Ticket.builder().status(TicketStatus.COMPLETED).build()));

        when(ticketRepository.findAllByRequesterOrResponsible(any(), any())).thenReturn(ticketList);

        List<Ticket> actualTicketList = ticketService.getAllByUser(new User(), false);

        assertEquals(ticketList.size() - 1, actualTicketList.size());
        verify(ticketRepository, times(1)).findAllByRequesterOrResponsible(any(), any());

    }

    @Test
    void givenNewTicket_whenAddingTicket_thenCompletesSuccessfully() {

        AddTicketRequest addTicketRequest = AddTicketRequest.builder().build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();
        Ticket ticket = Ticket.builder().id(UUID.randomUUID()).build();

        when(ticketRepository.save(any())).thenReturn(ticket);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        ticketService.addTicket(addTicketRequest, user, user);

        verify(ticketRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
    }

    @Test
    void givenExistingTicket_whenEditingTicket_thenCompletesSuccessfully() {

        AddTicketRequest ticketRequest = AddTicketRequest.builder().type(TicketType.OTHER).status(TicketStatus.PENDING).build();
        Ticket ticket = Ticket.builder().id(UUID.randomUUID()).type(TicketType.DEVICE).status(TicketStatus.COMPLETED).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        ticketService.editTicket(UUID.randomUUID(), ticketRequest, user);

        assertEquals(ticketRequest.getType(), ticket.getType());
        assertEquals(ticketRequest.getStatus(), ticket.getStatus());
        verify(ticketRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }

    @Test
    void givenExistingTicket_whenEditingTicketToStatusCompleted_thenPropertyChangedAndCompletesSuccessfully() {

        AddTicketRequest ticketRequest = AddTicketRequest.builder().type(TicketType.OTHER).status(TicketStatus.COMPLETED).build();
        Ticket ticket = Ticket.builder().id(UUID.randomUUID()).type(TicketType.DEVICE).status(TicketStatus.PENDING).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        ticketService.editTicket(UUID.randomUUID(), ticketRequest, user);

        assertEquals(ticketRequest.getType(), ticket.getType());
        assertEquals(ticketRequest.getStatus(), ticket.getStatus());
        assertNotNull(ticket.getClosed());
        verify(ticketRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }

    @Test
    void givenExistingTicket_whenEditingTicketToStatusRejected_thenPropertyChangedAndCompletesSuccessfully() {

        AddTicketRequest ticketRequest = AddTicketRequest.builder().type(TicketType.OTHER).status(TicketStatus.REJECTED).build();
        Ticket ticket = Ticket.builder().id(UUID.randomUUID()).type(TicketType.DEVICE).status(TicketStatus.PENDING).build();
        User user = User.builder().corporateEmail("toshu@abv.bg").id(UUID.randomUUID()).build();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        ticketService.editTicket(UUID.randomUUID(), ticketRequest, user);

        assertEquals(ticketRequest.getType(), ticket.getType());
        assertEquals(ticketRequest.getStatus(), ticket.getStatus());
        assertNotNull(ticket.getClosed());
        verify(ticketRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }
}
