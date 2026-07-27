package vix.local.api.modules.hr.domain.repository;

import vix.local.api.modules.hr.domain.model.HrPosition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HrPositionRepository {
    Optional<HrPosition> findById(UUID id);
    Optional<HrPosition> findByCode(String code);
    List<HrPosition> findAll();
    HrPosition save(HrPosition position);
    void deleteById(UUID id);
}
