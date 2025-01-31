package toshu.org.corpsuite.card.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import toshu.org.corpsuite.user.model.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CardType type;

    private boolean isActive;

    private String code;

    @OneToOne(mappedBy = "card")
    private User owner;

}
