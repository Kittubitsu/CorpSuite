package toshu.org.corpsuite.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import toshu.org.corpsuite.user.model.User;

@Data
@Builder
public class AddComputerRequest {

    @NotEmpty(message = "Computer name cannot be empty!")
    private String computerName;

    @NotEmpty(message = "Computer barcode cannot be empty!")
    private String barcode;

    private String comment;

    private String operatingSystem;

    private String macAddress;

    private String cpu;

    private String ram;

    private String motherboard;

    private String gpu;

    private String storage;

    private int age;

    private Boolean isActive;

    private User owner;

}
