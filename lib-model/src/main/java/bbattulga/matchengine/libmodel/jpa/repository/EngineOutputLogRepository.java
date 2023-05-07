package bbattulga.matchengine.libmodel.jpa.repository;

import bbattulga.matchengine.libmodel.jpa.entity.EngineOutputLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EngineOutputLogRepository extends JpaRepository<EngineOutputLog, UUID> {
}
