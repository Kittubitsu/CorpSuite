package toshu.org.corpsuite.computer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.repository.ComputerRepository;
import toshu.org.corpsuite.computer.service.ComputerService;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.ComputerAlreadyExistsException;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.ComputerRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComputerServiceUTest {

    @Mock
    private ComputerRepository computerRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ComputerService computerService;

    @Test
    void givenBoolTrue_whenGettingAllComputers_thenReturnAll() {
        //Given
        List<Computer> expectedList = List.of(Computer.builder().active(true).build(), Computer.builder().active(false).build());

        //When
        when(computerRepository.findAll()).thenReturn(expectedList);

        List<Computer> actualList = computerService.getAllComputers(true);

        //Then
        assertEquals(expectedList.size(), actualList.size());
        verify(computerRepository, times(1)).findAll();

    }

    @Test
    void givenBoolFalse_whenGettingAllComputers_thenReturnAllWithActiveStatus() {
        //Given
        List<Computer> expectedList = List.of(Computer.builder().active(true).build(), Computer.builder().active(false).build());

        //When
        when(computerRepository.findAll()).thenReturn(expectedList);

        List<Computer> actualList = computerService.getAllComputers(false);

        //Then
        assertNotEquals(expectedList.size(), actualList.size());
        verify(computerRepository, times(1)).findAll();

    }

    @Test
    void givenExistingComputers_whenGettingAllActiveComputers_ReturnAll() {
        //Given
        List<Computer> computerList = new ArrayList<>(List.of(Computer.builder().computerName("WKS1").build(), Computer.builder().computerName("WKS2").build()));

        //When
        when(computerRepository.findAllComputersByActiveTrue()).thenReturn(computerList);

        List<Computer> actualComputers = computerService.getAllActiveComputers();

        //Then
        assertEquals(computerList.size(), actualComputers.size());
        assertEquals(computerList.get(0).getComputerName(), actualComputers.get(0).getComputerName());
        assertEquals(computerList.get(1).getComputerName(), actualComputers.get(1).getComputerName());
        verify(computerRepository, times(1)).findAllComputersByActiveTrue();

    }

    @Test
    void givenNoActiveComputers_WhenGettingAllActiveComputers_thenReturnEmptyList() {
        when(computerRepository.findAllComputersByActiveTrue()).thenReturn(Collections.emptyList());

        List<Computer> result = computerService.getAllActiveComputers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(computerRepository, times(1)).findAllComputersByActiveTrue();
    }

    @Test
    void givenMissingComputer_whenGettingById_thenThrows() {

        long id = 1;

        when(computerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> computerService.getById(id));
        verify(computerRepository, times(1)).findById(any());
    }

    @Test
    void givenExistingComputer_whenGettingById_returnSuccessfully() {

        Computer computer = Computer.builder().id(1).build();

        when(computerRepository.findById(any())).thenReturn(Optional.of(computer));

        Computer actualComputer = computerService.getById(1);

        assertEquals(computer.getId(), actualComputer.getId());
        verify(computerRepository, times(1)).findById(any());

    }

    @Test
    void givenExistingComputers_whenEditComputerAge_thenAgeIncrementWithPlusOne() {

        LocalDateTime dateTime = LocalDateTime.now().minusYears(1);
        List<Computer> computers = new ArrayList<>(List.of(Computer.builder().age(0).createdOn(dateTime).build(), Computer.builder().age(0).createdOn(dateTime).build()));

        when(computerRepository.findAllComputersByActiveTrue()).thenReturn(computers);

        computerService.editComputerAge();

        assertEquals(1, computers.get(0).getAge());
        assertEquals(1, computers.get(1).getAge());
        verify(computerRepository, times(1)).saveAll(any());

    }

    @Test
    void givenExistingComputer_whenAddingDuplicate_thenThrows() {
        Computer computer = Computer.builder().computerName("WKS1").build();

        ComputerRequest computerRequest = ComputerRequest.builder().computerName(computer.getComputerName()).build();
        when(computerRepository.findComputerByComputerName(computer.getComputerName())).thenReturn(Optional.of(computer));

        assertThrows(ComputerAlreadyExistsException.class, () -> computerService.addComputer(computerRequest, User.builder().build()));
        verify(computerRepository, times(1)).findComputerByComputerName(any());
    }

    @Test
    void givenMissingComputer_whenAdding_thenCompletesSuccessfully() {

        ComputerRequest computerRequest = ComputerRequest.builder().computerName("WKS1").active(true).build();
        Computer computer = Computer.builder().id(1).build();

        when(computerRepository.findComputerByComputerName(computerRequest.getComputerName())).thenReturn(Optional.empty());
        when(computerRepository.save(any())).thenReturn(computer);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        computerService.addComputer(computerRequest, User.builder().corporateEmail("toshu@abv.bg").build());

        verify(computerRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }

    @Test
    void givenExistingComputer_whenEditComputer_thenCompletesSuccessfully() {

        ComputerRequest computerRequest = ComputerRequest.builder().computerName("WKS1").active(true).build();
        Computer computer = Computer.builder().id(1).build();

        when(computerRepository.findById(any())).thenReturn(Optional.of(computer));
        when(computerRepository.save(any())).thenReturn(computer);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        computerService.editComputer(1, computerRequest, new User());

        verify(computerRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }

    @Test
    void givenExistingComputer_whenEditComputerSetToInactive_thenDecommissionedIsAppended() {

        ComputerRequest computerRequest = ComputerRequest.builder().computerName("WKS1").active(false).build();
        Computer computer = Computer.builder().id(1).build();

        when(computerRepository.findById(any())).thenReturn(Optional.of(computer));
        when(computerRepository.save(any())).thenReturn(computer);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        computerService.editComputer(1, computerRequest, new User());

        verify(computerRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
        assertNotNull(computer.getDecommissionedOn());

    }
}
