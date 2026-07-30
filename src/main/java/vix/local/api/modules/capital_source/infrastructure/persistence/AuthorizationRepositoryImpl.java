package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.modules.capital_source.domain.repository.AuthorizationRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AuthorizationEntity;
import java.util.List;
import java.util.UUID;

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
    public List<Authorization> findByPartnerId(UUID partnerId) {
        return authorizationJpaRepository.findByPartnerId(partnerId).stream()
                .map(this::convertToModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        authorizationJpaRepository.deleteById(id);
    }

    @Override
    public Authorization findById(UUID id) {
        return convertToModel(authorizationJpaRepository.findById(id).orElse(null));
    }

    private AuthorizationEntity convertToEntity(Authorization authorization) {
        AuthorizationEntity entity = AuthorizationEntity.builder()
                .id(authorization.getId())
                .partnerId(authorization.getPartnerId())
                .seqId(authorization.getSeqId())
                .authName(authorization.getAuthName())
                .authPosition(authorization.getAuthPosition())
                .authidNo(authorization.getAuthidNo())
                .authissueDate(authorization.getAuthissueDate())
                .authedName(authorization.getAuthedName())
                .authedIdNo(authorization.getAuthedIdNo())
                .authedIssueDate(authorization.getAuthedIssueDate())
                .issuePlace(authorization.getIssuePlace())
                .authNo(authorization.getAuthNo())
                .effDate(authorization.getEffDate())
                .ExpiryDate(authorization.getExpiryDate())
                .authedPosition(authorization.getAuthedPosition())
                .phone(authorization.getPhone())
                .email(authorization.getEmail())
                .build();
        return entity;
    }

    private Authorization convertToModel(AuthorizationEntity entity) {
        if (entity == null) return null;
        return Authorization.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .seqId(entity.getSeqId())
                .authName(entity.getAuthName())
                .authPosition(entity.getAuthPosition())
                .authidNo(entity.getAuthidNo())
                .authissueDate(entity.getAuthissueDate())
                .authedName(entity.getAuthedName())
                .authedIdNo(entity.getAuthedIdNo())
                .authedIssueDate(entity.getAuthedIssueDate())
                .issuePlace(entity.getIssuePlace())
                .authNo(entity.getAuthNo())
                .effDate(entity.getEffDate())
                .ExpiryDate(entity.getExpiryDate())
                .authedPosition(entity.getAuthedPosition())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .build();
    }
}