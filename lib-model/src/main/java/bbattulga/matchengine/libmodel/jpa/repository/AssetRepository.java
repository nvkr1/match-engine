package bbattulga.matchengine.libmodel.jpa.repository;

import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetIdAndStatus(Long assetId, Asset.Status status);
    Optional<Asset> findBySymbolAndStatus(String symbol, Asset.Status status);
}
