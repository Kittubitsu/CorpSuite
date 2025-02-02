package toshu.org.corpsuite.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
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
    private boolean isActive;
    @NotNull
    private String password;
    @NotNull
    private UserPosition position;
    @NotNull
    private String profile_picture;
    @NotNull
    private Card cardId;

}
