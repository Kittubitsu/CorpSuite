package toshu.org.corpsuite.computer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.repository.ComputerRepository;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.web.dto.ComputerAdd;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComputerService {

    private final ComputerRepository computerRepository;

    @Autowired
    public ComputerService(ComputerRepository computerRepository) {
        this.computerRepository = computerRepository;
    }

    public Computer addComputer(ComputerAdd computerAdd) {

        if (computerRepository.findComputerByComputerName(computerAdd.getComputerName()).isPresent()) {
            throw new DomainException("Computer with that name exists already!");
        }

        Computer computer = Computer.builder()
                .computerName(computerAdd.getComputerName())
                .barcode(computerAdd.getBarcode())
                .comment(computerAdd.getComment())
                .operatingSystem(computerAdd.getOperatingSystem())
                .macAddress(computerAdd.getMacAddress())
                .cpu(computerAdd.getCpu())
                .ram(computerAdd.getRam())
                .motherboard(computerAdd.getMotherboard())
                .gpu(computerAdd.getGpu())
                .storage(computerAdd.getStorage())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .age(0)
                .isActive(computerAdd.isActive())
                .build();

        return computerRepository.save(computer);
    }

    public List<Computer> getAllComputers() {

        return computerRepository.findAll();
    }

    public List<Computer> getAllActiveComputers() {
        return computerRepository.findAllComputersByActiveTrue();
    }

}
