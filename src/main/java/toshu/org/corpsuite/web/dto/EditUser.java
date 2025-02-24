package toshu.org.corpsuite.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.user.model.UserPosition;

@Data
@Builder
public class EditUser {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String department;

    @NotNull
    private String country;

    @NotNull
    private String corporateEmail;

    @NotNull
    private Boolean isActive;

    @NotNull
    @Size(min = 8, message = "Password must be atleast 8 characters")
    private String password;

    @NotNull
    private UserPosition position;

    @NotNull
    @URL
    private String profilePicture;

    @NotNull
    private Card cardId;

}
