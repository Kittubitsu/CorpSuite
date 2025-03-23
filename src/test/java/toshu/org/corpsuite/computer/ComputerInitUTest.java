package toshu.org.corpsuite.computer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.service.ComputerInit;
import toshu.org.corpsuite.computer.service.ComputerService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ComputerInitUTest {

    @Mock
    private ComputerService computerService;

    @InjectMocks
    private ComputerInit computerInit;


    @Test
    void givenNoComputers_whenRun_thenCreateFirstComputers() throws Exception {

        when(computerService.getAllComputers(any())).thenReturn(Collections.emptyList());

        computerInit.run();

        verify(computerService, atLeastOnce()).addComputer(any(), any());
    }

    @Test
    void givenComputersExists_whenRun_thenDoNothing() throws Exception {

        when(computerService.getAllComputers(any())).thenReturn(List.of(new Computer()));

        computerInit.run();

        verify(computerService, never()).addComputer(any(), any());
    }
}
