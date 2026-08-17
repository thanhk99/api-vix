package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Kunn;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface KunnRepository {
    Kunn save(Kunn kunn);
    Optional<Kunn> findById(UUID id);
    List<Kunn> findAll();
    // Additional find methods can be added here if needed by the application service (e.g. find by partnerId, limitId)
}
