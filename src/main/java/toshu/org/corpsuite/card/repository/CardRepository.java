package toshu.org.corpsuite.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.card.model.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findCardByCode(String code);

    @Query(value = "SELECT c FROM Card c WHERE c.owner is null AND c.isActive = TRUE")
    List<Card> findAllByOwnerIsNullAndActive();

    @Query(value = "SELECT c FROM Card c WHERE c.isActive = TRUE")
    List<Card> findAllActiveCards();
}
