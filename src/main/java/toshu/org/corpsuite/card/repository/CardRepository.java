package toshu.org.corpsuite.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.card.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

}
