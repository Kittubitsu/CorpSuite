package toshu.org.corpsuite.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.web.dto.UserAdd;

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
            UserAdd userAdd = UserAdd.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .corporateEmail("admin@corpsuite.com")
                    .position(UserPosition.ADMIN)
                    .country("Bulgaria")
                    .department("ADMIN")
                    .password("admin123")
                    .isActive(true)
                    .build();

            userService.addUser(userAdd);
        }


    }
}
