package toshu.org.corpsuite.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByCorporateEmail(String corporateEmail);

    @Query(value = "SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllByActive();
}
