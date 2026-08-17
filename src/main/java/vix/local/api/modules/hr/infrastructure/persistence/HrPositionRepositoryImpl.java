package vix.local.api.modules.hr.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.hr.domain.model.HrPosition;
import vix.local.api.modules.hr.domain.repository.HrPositionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HrPositionRepositoryImpl implements HrPositionRepository {

    private final HrPositionJpaRepository jpaRepository;

    @Override
    public Optional<HrPosition> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<HrPosition> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<HrPosition> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<HrPosition> findAllById(List<UUID> ids) {
        return jpaRepository.findAllById(ids).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public HrPosition save(HrPosition position) {
        HrPositionEntity entity = toEntity(position);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private HrPosition toDomain(HrPositionEntity e) {
        return HrPosition.builder()
                .id(e.getId())
                .name(e.getName())
                .code(e.getCode())
                .description(e.getDescription())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private HrPositionEntity toEntity(HrPosition p) {
        return HrPositionEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .code(p.getCode())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
