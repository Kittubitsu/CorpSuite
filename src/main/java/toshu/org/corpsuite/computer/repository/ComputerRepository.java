package toshu.org.corpsuite.computer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.computer.model.Computer;

@Repository
public interface ComputerRepository extends JpaRepository<Computer,Long> {

}
