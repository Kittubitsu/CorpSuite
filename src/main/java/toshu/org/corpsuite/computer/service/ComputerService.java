package toshu.org.corpsuite.computer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.repository.ComputerRepository;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.ComputerAlreadyExistsException;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.ComputerRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ComputerService {

    private final ComputerRepository computerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ComputerService(ComputerRepository computerRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.computerRepository = computerRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void addComputer(ComputerRequest computerRequest, User user) {

        if (computerRepository.findComputerByComputerName(computerRequest.getComputerName()).isPresent()) {
            throw new ComputerAlreadyExistsException("Computer with that name exists already!");
        }

        Computer computer = Computer.builder()
                .computerName(computerRequest.getComputerName())
                .barcode(computerRequest.getBarcode())
                .comment(computerRequest.getComment())
                .operatingSystem(computerRequest.getOperatingSystem())
                .macAddress(computerRequest.getMacAddress())
                .cpu(computerRequest.getCpu())
                .ram(computerRequest.getRam())
                .motherboard(computerRequest.getMotherboard())
                .gpu(computerRequest.getGpu())
                .storage(computerRequest.getStorage())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .age(computerRequest.getAge())
                .active(computerRequest.getActive())
                .owner(computerRequest.getOwner())
                .build();

        Computer saved = computerRepository.save(computer);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("CREATE")
                .module("Computer")
                .comment("Computer created with id [%d]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }

    public List<Computer> getAllComputers(Boolean show) {
        List<Computer> computers = computerRepository.findAll();
        if (!show) {
            return computers.stream().filter(Computer::isActive).toList();
        }
        return computers;
    }

    public List<Computer> getAllActiveComputers() {
        return computerRepository.findAllComputersByActiveTrue();
    }

    public Computer getById(long id) {
        return computerRepository.findById(id).orElseThrow(() -> new DomainException("Computer does not exist!"));
    }

    public void editComputer(long id, ComputerRequest computerRequest, User user) {
        Computer computer = getById(id);

        computer.setComputerName(computerRequest.getComputerName());
        computer.setAge(computerRequest.getAge());
        computer.setActive(computerRequest.getActive());
        computer.setCpu(computerRequest.getCpu());
        computer.setComment(computerRequest.getComment());
        computer.setBarcode(computerRequest.getBarcode());
        computer.setGpu(computerRequest.getGpu());
        computer.setMacAddress(computerRequest.getMacAddress());
        computer.setMotherboard(computerRequest.getMotherboard());
        computer.setOperatingSystem(computerRequest.getOperatingSystem());
        computer.setRam(computerRequest.getRam());
        computer.setStorage(computerRequest.getStorage());
        computer.setOwner(computerRequest.getOwner());
        computer.setUpdatedOn(LocalDateTime.now());

        if (!computer.isActive()) {
            computer.setDecommissionedOn(LocalDateTime.now());
        }

        Computer saved = computerRepository.save(computer);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Computer")
                .comment("Computer edited with id [%d]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));

    }

    @Scheduled(cron = "@monthly")
    public void editComputerAge() {
        List<Computer> computers = getAllActiveComputers();
        for (Computer computer : computers) {
            long age = computer.getCreatedOn().until(LocalDateTime.now(), ChronoUnit.YEARS);
            while (computer.getAge() < age) {
                computer.setAge(computer.getAge() + 1);
            }
        }
        computerRepository.saveAll(computers);
    }
}
