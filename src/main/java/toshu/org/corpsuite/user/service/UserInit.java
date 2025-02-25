package toshu.org.corpsuite.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.web.dto.EditUser;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {

        if (userService.getAllUsers().isEmpty()) {
            EditUser userAdd = EditUser.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .corporateEmail("admin@corpsuite.com")
                    .position(UserPosition.ADMIN)
                    .country("Bulgaria")
                    .department(UserDepartment.ADMIN)
                    .password("admin123")
                    .isActive(true)
                    .build();

            userService.addUser(userAdd);
        }


    }
}
