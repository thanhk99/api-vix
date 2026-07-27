package vix.local.api.modules.hr.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.modules.hr.domain.repository.HrUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HrUserRepositoryImpl implements HrUserRepository {

    private final HrUserJpaRepository jpaRepository;

    @Override
    public Optional<HrUser> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<HrUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<HrUser> findByEmployeeCode(String employeeCode) {
        return jpaRepository.findByEmployeeCode(employeeCode).map(this::toDomain);
    }

    @Override
    public List<HrUser> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<HrUser> findByDepartmentId(UUID departmentId) {
        return jpaRepository.findByDepartmentId(departmentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<HrUser> findByStatus(String status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<HrUser> findAllPaged(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Page<HrUser> findByDepartmentIdPaged(UUID departmentId, Pageable pageable) {
        return jpaRepository.findByDepartmentId(departmentId, pageable).map(this::toDomain);
    }

    @Override
    public Page<HrUser> searchByKeyword(String keyword, Pageable pageable) {
        return jpaRepository.searchByKeyword(keyword, pageable).map(this::toDomain);
    }

    @Override
    public long countByDepartmentId(UUID departmentId) {
        return jpaRepository.countByDepartmentId(departmentId);
    }

    @Override
    public HrUser save(HrUser user) {
        return toDomain(jpaRepository.save(toEntity(user)));
    }

    private HrUser toDomain(HrUserEntity e) {
        if (e == null)
            return null;
        return HrUser.builder()
                .id(e.getId())
                .email(e.getEmail())
                .fullName(e.getFullName())
                .passwordHash(e.getPasswordHash())
                .status(e.getStatus())
                .employeeCode(e.getEmployeeCode())
                .phone(e.getPhone())
                .gender(e.getGender())
                .birthDate(e.getBirthDate())
                .address(e.getAddress())
                .idCardNumber(e.getIdCardNumber())
                .idCardIssuedDate(e.getIdCardIssuedDate())
                .idCardIssuedPlace(e.getIdCardIssuedPlace())
                .departmentId(e.getDepartmentId())
                .positionId(e.getPositionId())
                .joinDate(e.getJoinDate())
                .terminateDate(e.getTerminateDate())
                .avatarUrl(e.getAvatarUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private HrUserEntity toEntity(HrUser u) {
        if (u == null)
            return null;
        return HrUserEntity.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .passwordHash(u.getPasswordHash())
                .status(u.getStatus())
                .employeeCode(u.getEmployeeCode())
                .phone(u.getPhone())
                .gender(u.getGender())
                .birthDate(u.getBirthDate())
                .address(u.getAddress())
                .idCardNumber(u.getIdCardNumber())
                .idCardIssuedDate(u.getIdCardIssuedDate())
                .idCardIssuedPlace(u.getIdCardIssuedPlace())
                .departmentId(u.getDepartmentId())
                .positionId(u.getPositionId())
                .joinDate(u.getJoinDate())
                .terminateDate(u.getTerminateDate())
                .avatarUrl(u.getAvatarUrl())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
