package toshu.org.corpsuite.computer.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.ComputerRequest;

@Component
public class ComputerInit implements CommandLineRunner {

    private final ComputerService computerService;

    public ComputerInit(ComputerService computerService) {
        this.computerService = computerService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (computerService.getAllComputers(true).isEmpty()) {
            ComputerRequest computerRequest = ComputerRequest.builder()
                    .computerName("SOFWKS0001")
                    .cpu("AMD Ryzen 7 5700X3D")
                    .gpu("nVidia RTX 3080")
                    .ram("32GB")
                    .barcode("20250101")
                    .macAddress("33:ee:34:b1:d3:5f")
                    .motherboard("AsRock X570 Phantom Gaming 4")
                    .operatingSystem("Windows 11")
                    .storage("2TB")
                    .active(true)
                    .age(0)
                    .build();

            ComputerRequest computerRequest2 = ComputerRequest.builder()
                    .computerName("SOFWKS0002")
                    .cpu("AMD Ryzen 7 5700X3D")
                    .gpu("nVidia RTX 3080")
                    .ram("32GB")
                    .barcode("20250101")
                    .macAddress("33:ee:34:b1:d3:6f")
                    .motherboard("AsRock X570 Phantom Gaming 4")
                    .operatingSystem("Windows 11")
                    .storage("2TB")
                    .active(true)
                    .age(0)
                    .build();

            ComputerRequest computerRequest3 = ComputerRequest.builder()
                    .computerName("SOFWKS0003")
                    .cpu("AMD Ryzen 7 5700X3D")
                    .gpu("nVidia RTX 3080")
                    .ram("32GB")
                    .barcode("20250101")
                    .macAddress("33:ee:34:b1:d3:6f")
                    .motherboard("AsRock X570 Phantom Gaming 4")
                    .operatingSystem("Windows 11")
                    .storage("2TB")
                    .active(true)
                    .age(0)
                    .build();

            ComputerRequest computerRequest4 = ComputerRequest.builder()
                    .computerName("SOFWKS0004")
                    .cpu("AMD Ryzen 7 5700X3D")
                    .gpu("nVidia RTX 3080")
                    .ram("32GB")
                    .barcode("20250101")
                    .macAddress("33:ee:34:b1:d3:6f")
                    .motherboard("AsRock X570 Phantom Gaming 4")
                    .operatingSystem("Windows 11")
                    .storage("2TB")
                    .active(true)
                    .age(0)
                    .build();

            ComputerRequest computerRequest5 = ComputerRequest.builder()
                    .computerName("SOFWKS0005")
                    .cpu("AMD Ryzen 7 5700X3D")
                    .gpu("nVidia RTX 3080")
                    .ram("32GB")
                    .barcode("20250101")
                    .macAddress("33:ee:34:b1:d3:6f")
                    .motherboard("AsRock X570 Phantom Gaming 4")
                    .operatingSystem("Windows 11")
                    .storage("2TB")
                    .active(true)
                    .age(0)
                    .build();


            computerService.addComputer(computerRequest, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            computerService.addComputer(computerRequest2, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            computerService.addComputer(computerRequest3, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            computerService.addComputer(computerRequest4, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            computerService.addComputer(computerRequest5, User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
        }
    }
}
