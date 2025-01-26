package toshu.org.corpsuite.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Login {

    @NotNull
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters!")
    private String password;
}
