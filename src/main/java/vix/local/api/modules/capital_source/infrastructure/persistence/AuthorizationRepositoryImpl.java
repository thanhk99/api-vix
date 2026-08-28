package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.modules.capital_source.domain.repository.AuthorizationRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AuthorizationEntity;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private final AuthorizationJpaRepository authorizationJpaRepository;

    @Override
    public Authorization save(Authorization authorization) {
        AuthorizationEntity entity = convertToEntity(authorization);
        AuthorizationEntity saved = authorizationJpaRepository.save(entity);
        return convertToModel(saved);
    }

    @Override
    public Page<Authorization> findByPartnerId(UUID partnerId, Pageable pageable) {
        return authorizationJpaRepository.findByPartnerId(partnerId, pageable)
                .map(this::convertToModel);
    }

    @Override
    public void deleteById(UUID id) {
        authorizationJpaRepository.deleteById(id);
    }

    @Override
    public Authorization findById(UUID id) {
        return convertToModel(authorizationJpaRepository.findById(id).orElse(null));
    }

    @Override
    public Integer getMaxSeqIdByPartnerId(UUID partnerId) {
        Optional<AuthorizationEntity> topAuth = authorizationJpaRepository.findTopByPartnerIdOrderBySeqIdDesc(partnerId);
        if (topAuth.isPresent() && topAuth.get().getSeqId() != null) {
            return topAuth.get().getSeqId();
        }
        return 0;
    }

    private AuthorizationEntity convertToEntity(Authorization authorization) {
        if (authorization == null) return null;
        AuthorizationEntity entity = new AuthorizationEntity();
        entity.setId(authorization.getId());
        entity.setPartnerId(authorization.getPartnerId());
        entity.setSeqId(authorization.getSeqId());
        entity.setAuthType(authorization.getAuthType());
        entity.setParentAuthId(authorization.getParentAuthId());
        entity.setAuthName(authorization.getAuthName());
        entity.setAuthPosition(authorization.getAuthPosition());
        entity.setAuthidNo(authorization.getAuthidNo());
        entity.setAuthissueDate(authorization.getAuthissueDate());
        entity.setAuthedName(authorization.getAuthedName());
        entity.setAuthedIdNo(authorization.getAuthedIdNo());
        entity.setAuthedIssueDate(authorization.getAuthedIssueDate());
        entity.setAuthedIssuePlace(authorization.getAuthedIssuePlace());
        entity.setIssuePlace(authorization.getIssuePlace());
        entity.setAuthNo(authorization.getAuthNo());
        entity.setEffDate(authorization.getEffDate());
        entity.setExpiryDate(authorization.getExpiryDate());
        entity.setAuthedPosition(authorization.getAuthedPosition());
        entity.setScope(authorization.getScope());
        entity.setNote(authorization.getNote());
        entity.setStatus(authorization.getStatus());
        entity.setPhone(authorization.getPhone());
        entity.setEmail(authorization.getEmail());
        return entity;
    }

    private Authorization convertToModel(AuthorizationEntity entity) {
        if (entity == null) return null;
        Authorization auth = new Authorization();
        auth.setId(entity.getId());
        auth.setPartnerId(entity.getPartnerId());
        auth.setSeqId(entity.getSeqId());
        auth.setAuthType(entity.getAuthType());
        auth.setParentAuthId(entity.getParentAuthId());
        auth.setAuthName(entity.getAuthName());
        auth.setAuthPosition(entity.getAuthPosition());
        auth.setAuthidNo(entity.getAuthidNo());
        auth.setAuthissueDate(entity.getAuthissueDate());
        auth.setAuthedName(entity.getAuthedName());
        auth.setAuthedIdNo(entity.getAuthedIdNo());
        auth.setAuthedIssueDate(entity.getAuthedIssueDate());
        auth.setAuthedIssuePlace(entity.getAuthedIssuePlace());
        auth.setIssuePlace(entity.getIssuePlace());
        auth.setAuthNo(entity.getAuthNo());
        auth.setEffDate(entity.getEffDate());
        auth.setExpiryDate(entity.getExpiryDate());
        auth.setAuthedPosition(entity.getAuthedPosition());
        auth.setScope(entity.getScope());
        auth.setNote(entity.getNote());
        auth.setStatus(entity.getStatus());
        auth.setPhone(entity.getPhone());
        auth.setEmail(entity.getEmail());
        return auth;
    }
}
