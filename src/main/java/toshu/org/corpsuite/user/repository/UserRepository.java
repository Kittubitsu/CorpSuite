package toshu.org.corpsuite.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByCorporateEmail(String corporateEmail);

    @Query(value = "SELECT u FROM User u WHERE u.active = true")
    List<User> findAllByActive();

    @Query(value = "SELECT u FROM User u WHERE u.department = ?1 and u.position = 'MANAGER' and u.active = true")
    Optional<User> findUserByDepartmentAndPosition_Manager(UserDepartment department);

    @Query(value = "SELECT u FROM User u WHERE u.department = ?1 and u.position != ?2 and u.active = true")
    List<User> findAllByDepartmentAndPositionNot(UserDepartment department, UserPosition position);

    @Query(value = "SELECT u FROM User u WHERE SIZE(u.computers) = 0 and u.active = true")
    List<User> findAllByComputersEmpty();

    @Query(value = "SELECT u FROM User u WHERE u.department = ?1 and u.active = true")
    List<User> findAllByDepartment(UserDepartment department);
}
