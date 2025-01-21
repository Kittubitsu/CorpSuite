package toshu.org.corpsuite.computer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.computer.repository.ComputerRepository;

@Service
public class ComputerService {

    private final ComputerRepository computerRepository;

    @Autowired
    public ComputerService(ComputerRepository computerRepository) {
        this.computerRepository = computerRepository;
    }



}
