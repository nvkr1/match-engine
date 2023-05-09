package bbattulga.matchengine.libmodel.jpa.repository;

import bbattulga.matchengine.libmodel.jpa.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    public static interface RecentTrade {
        String getMatchId();
        BigDecimal getPrice();
        BigDecimal getQty();
        BigDecimal getTotal();
        Long getUtc();
    }

    Optional<Match> findByExecOrderIdAndRemainingOrderId(UUID execOrderId, UUID remainingOrderId);

    @Query(value = "select\n" +
            "em.match_id as \"matchId\",\n" +
            "em.price as \"price\",\n" +
            "em.qty as \"qty\",\n" +
            "em.total as \"total\",\n" +
            "em.utc as \"utc\"\n" +
            "from ex_match em\n" +
            "where em.pair_id = :pairId\n" +
            "order by em.created_at asc\n" +
            "limit :size", nativeQuery = true)
    List<RecentTrade> findRecentTradesByPairId(@Param("pairId") Long pairId,  @Param("size") Integer size);
}
