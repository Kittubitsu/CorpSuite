package toshu.org.corpsuite.web.dto;

import lombok.Builder;
import lombok.Data;
import toshu.org.corpsuite.card.model.CardType;

@Data
@Builder
public class CardAdd {

    private String code;

    private CardType type;

    private boolean isActive;
}
