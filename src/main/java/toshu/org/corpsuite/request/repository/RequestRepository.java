package toshu.org.corpsuite.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.request.model.Request;

import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

}
