package bbattulga.matchengine.libmodel.jpa.repository;

import bbattulga.matchengine.libmodel.jpa.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, UUID> {
}
