package toshu.org.corpsuite.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComputerAdd {

    private String computerName;

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

    private boolean isActive;

}
