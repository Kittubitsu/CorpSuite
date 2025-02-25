package toshu.org.corpsuite.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditUser {
    @NotEmpty(message = "First name cannot be empty!")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty!")
    private String lastName;

    @NotNull(message = "Department cannot be empty!")
    private UserDepartment department;

    @NotEmpty(message = "Country cannot be empty!")
    private String country;

    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Must be an email!")
    private String corporateEmail;

    private Boolean isActive;

    @NotEmpty(message = "Password cannot be empty!")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Position cannot be empty!")
    private UserPosition position;

    @NotEmpty(message = "Profile picture cannot be empty!")
    @URL(message = "Must be a URL!")
    private String profilePicture;

    private Card card;

}
