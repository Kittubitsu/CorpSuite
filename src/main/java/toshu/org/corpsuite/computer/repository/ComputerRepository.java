package toshu.org.corpsuite.computer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.computer.model.Computer;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {

    Optional<Computer> findComputerByComputerName(String computerName);

    @Query(value = "SELECT c FROM Computer c WHERE c.active = true")
    List<Computer> findAllComputersByActiveTrue();

}
