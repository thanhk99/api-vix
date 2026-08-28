package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.domain.model.PartnerSecuritiesAccount;
import vix.local.api.modules.capital_source.domain.repository.PartnerSecuritiesAccountRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerSecuritiesAccountEntity;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartnerSecuritiesAccountRepositoryImpl implements PartnerSecuritiesAccountRepository {
    private final PartnerSecuritiesAccountJpaRepository jpaRepository;

    @Override
    public PartnerSecuritiesAccount save(PartnerSecuritiesAccount account) {
        PartnerSecuritiesAccountEntity entity = convertToEntity(account);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<PartnerSecuritiesAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public Page<PartnerSecuritiesAccount> findByPartnerId(UUID partnerId, Pageable pageable) {
        return jpaRepository.findByPartnerIdAndStatusNot(partnerId, "DELETED", pageable).map(this::convertToModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerSecuritiesAccountEntity convertToEntity(PartnerSecuritiesAccount model) {
        if (model == null) return null;
        PartnerSecuritiesAccountEntity entity = new PartnerSecuritiesAccountEntity();
        entity.setId(model.getId());
        entity.setPartnerId(model.getPartnerId());
        entity.setAccountNumber(model.getAccountNumber());
        entity.setAccountName(model.getAccountName());
        entity.setTradingGateways(model.getTradingGateways());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    private PartnerSecuritiesAccount convertToModel(PartnerSecuritiesAccountEntity entity) {
        if (entity == null) return null;
        return PartnerSecuritiesAccount.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .accountNumber(entity.getAccountNumber())
                .accountName(entity.getAccountName())
                .tradingGateways(entity.getTradingGateways())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}