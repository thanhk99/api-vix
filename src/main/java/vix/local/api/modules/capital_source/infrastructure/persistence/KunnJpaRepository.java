package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KunnJpaRepository extends JpaRepository<KunnEntity, UUID> {
}
