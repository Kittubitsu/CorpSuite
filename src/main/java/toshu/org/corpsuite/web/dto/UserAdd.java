package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import toshu.org.corpsuite.user.model.UserPosition;

@Data
@Builder
public class UserAdd {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String corporateEmail;

    @NotNull
    private UserPosition position;

    @NotNull
    @URL
    private String profilePicture;

    @NotNull
    private String country;

    @NotNull
    private String department;

    @Size(min = 8, message = "Password must be atleast 8 characters")
    @NotNull
    private String password;

    @NotNull
    private boolean isActive;


}

