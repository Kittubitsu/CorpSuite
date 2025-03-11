package toshu.org.corpsuite.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.web.dto.AddUserRequest;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {

        if (userService.getAllActiveUsers().isEmpty()) {
            AddUserRequest userAdd = AddUserRequest.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .corporateEmail("admin@corpsuite.com")
                    .position(UserPosition.ADMIN)
                    .country("Bulgaria")
                    .department(UserDepartment.ADMIN)
                    .password("admin123")
                    .active(true)
                    .build();

            userService.addUser(userAdd, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
        }


    }
}
