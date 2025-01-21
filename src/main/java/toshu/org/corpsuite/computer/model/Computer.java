package toshu.org.corpsuite.computer.model;

import jakarta.persistence.*;
import lombok.*;
import toshu.org.corpsuite.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String computerName;

    @Column(nullable = false)
    private String barcode;

    private String comment;

    private String operatingSystem;

    private String macAddress;

    private String cpu;

    private String ram;

    private String motherboard;

    private String gpu;

    private String storage;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private LocalDateTime decommissionedOn;

    private int age;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToOne
    private User owner;
}
