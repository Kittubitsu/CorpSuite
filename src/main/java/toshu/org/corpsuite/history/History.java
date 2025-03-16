package toshu.org.corpsuite.history;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Data
public class History {

    private boolean show;

}

