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

            AddUserRequest userAdd2 = AddUserRequest.builder()
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .corporateEmail("ivan.ivanov@corpsuite.com")
                    .position(UserPosition.JUNIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.IT)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd3 = AddUserRequest.builder()
                    .firstName("Petar")
                    .lastName("Petrov")
                    .corporateEmail("petar.petrov@corpsuite.com")
                    .position(UserPosition.SENIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.IT)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd4 = AddUserRequest.builder()
                    .firstName("Georgi")
                    .lastName("Georgiev")
                    .corporateEmail("georgi.georgiev@corpsuite.com")
                    .position(UserPosition.MANAGER)
                    .country("Bulgaria")
                    .department(UserDepartment.IT)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd5 = AddUserRequest.builder()
                    .firstName("Preslava")
                    .lastName("Dimitrova")
                    .corporateEmail("preslava.dimitrova@corpsuite.com")
                    .position(UserPosition.JUNIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.HR)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd6 = AddUserRequest.builder()
                    .firstName("Gergana")
                    .lastName("Petkova")
                    .corporateEmail("gergana.petkova@corpsuite.com")
                    .position(UserPosition.SENIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.HR)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd7 = AddUserRequest.builder()
                    .firstName("Victoria")
                    .lastName("Krasimirova")
                    .corporateEmail("victoria.krasimirova@corpsuite.com")
                    .position(UserPosition.MANAGER)
                    .country("Bulgaria")
                    .department(UserDepartment.HR)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd8 = AddUserRequest.builder()
                    .firstName("Boyan")
                    .lastName("Ivanov")
                    .corporateEmail("boyan.ivanov@corpsuite.com")
                    .position(UserPosition.JUNIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.PROGRAMMING)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd9 = AddUserRequest.builder()
                    .firstName("Nikolay")
                    .lastName("Dimitrov")
                    .corporateEmail("nikolay.dimitrov@corpsuite.com")
                    .position(UserPosition.SENIOR)
                    .country("Bulgaria")
                    .department(UserDepartment.PROGRAMMING)
                    .password("admin123")
                    .active(true)
                    .build();

            AddUserRequest userAdd10 = AddUserRequest.builder()
                    .firstName("Todor")
                    .lastName("Dimitrov")
                    .corporateEmail("todor.dimitrov@corpsuite.com")
                    .position(UserPosition.MANAGER)
                    .country("Bulgaria")
                    .department(UserDepartment.PROGRAMMING)
                    .password("admin123")
                    .active(true)
                    .build();

            userService.addUser(userAdd, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd2, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd3, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd4, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd5, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd6, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd7, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd8, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd9, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            userService.addUser(userAdd10, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());

        }


    }
}
