package bbattulga.matchengine.libmodel.jpa.repository;

import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PairRepository extends JpaRepository<Pair, Long> {
    Optional<Pair> findByPairIdAndStatus(Long pairId, Pair.Status status);
    Optional<Pair> findBySymbolAndStatus(String symbol, Pair.Status status);
    Optional<Pair> findByBaseAssetIdAndQuoteAssetIdAndStatus(Long baseAssetId, Long quoteAssetId, Pair.Status status);
}
