package toshu.org.corpsuite.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
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
public class EditUserRequest {

    @NotEmpty(message = "First name cannot be empty!")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty!")
    private String lastName;

    @Nullable
    private UserDepartment department;

    @NotEmpty(message = "Country cannot be empty!")
    private String country;

    @Nullable
    @Email(message = "Must be an email!")
    private String corporateEmail;

    @Nullable
    private Boolean active;

    @Pattern(regexp = "^$|.{8,}", message = "Password must be at least 8 characters!")
    private String password;

    @Nullable
    private UserPosition position;

    @URL(message = "Must be a URL!")
    private String profilePicture;

    @Nullable
    private Card card;

}
