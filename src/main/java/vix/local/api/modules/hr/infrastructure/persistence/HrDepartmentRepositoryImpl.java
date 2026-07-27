package vix.local.api.modules.hr.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HrDepartmentRepositoryImpl implements HrDepartmentRepository {

    private final HrDepartmentJpaRepository jpaRepository;

    @Override
    public Optional<HrDepartment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<HrDepartment> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<HrDepartment> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<HrDepartment> findByStatus(String status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public HrDepartment save(HrDepartment department) {
        HrDepartmentEntity entity = toEntity(department);
        return toDomain(jpaRepository.save(entity));
    }

    private HrDepartment toDomain(HrDepartmentEntity entity) {
        if (entity == null) return null;
        return HrDepartment.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .managerId(entity.getManagerId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private HrDepartmentEntity toEntity(HrDepartment domain) {
        if (domain == null) return null;
        return HrDepartmentEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .managerId(domain.getManagerId())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
