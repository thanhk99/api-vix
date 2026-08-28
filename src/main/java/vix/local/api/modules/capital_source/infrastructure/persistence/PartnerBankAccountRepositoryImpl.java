package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.PartnerBankAccount;
import vix.local.api.modules.capital_source.domain.repository.PartnerBankAccountRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerBankAccountEntity;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartnerBankAccountRepositoryImpl implements PartnerBankAccountRepository {

    private final PartnerBankAccountJpaRepository jpaRepository;

    @Override
    public PartnerBankAccount save(PartnerBankAccount account) {
        return toModel(jpaRepository.save(toEntity(account)));
    }

    @Override
    public PartnerBankAccount findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel).orElse(null);
    }

    @Override
    public Page<PartnerBankAccount> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable) {
        return jpaRepository.findByPartnerIdAndStatusNot(partnerId, status, pageable).map(this::toModel);
    }

    @Override
    public Page<PartnerBankAccount> findByPartnerIdAndAccountTypeAndStatusNot(UUID partnerId, String accountType, String status, Pageable pageable) {
        return jpaRepository.findByPartnerIdAndAccountTypeAndStatusNot(partnerId, accountType, status, pageable).map(this::toModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerBankAccountEntity toEntity(PartnerBankAccount model) {
        if (model == null) return null;
        PartnerBankAccountEntity entity = new PartnerBankAccountEntity();
        entity.setId(model.getId());
        entity.setPartnerId(model.getPartnerId());
        entity.setAccountNumber(model.getAccountNumber());
        entity.setAccountName(model.getAccountName());
        entity.setBranch(model.getBranch());
        entity.setPurpose(model.getPurpose());
        entity.setStatus(model.getStatus());
        entity.setCitadCode(model.getCitadCode());
        entity.setAccountType(model.getAccountType());
        entity.setOpenPlace(model.getOpenPlace());
        entity.setDepositoryMemberNo(model.getDepositoryMemberNo());
        entity.setTradingGateway(model.getTradingGateway());
        entity.setCreatedBy(model.getCreatedBy());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedBy(model.getUpdatedBy());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    private PartnerBankAccount toModel(PartnerBankAccountEntity entity) {
        if (entity == null) return null;
        return PartnerBankAccount.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .accountNumber(entity.getAccountNumber())
                .accountName(entity.getAccountName())
                .branch(entity.getBranch())
                .purpose(entity.getPurpose())
                .status(entity.getStatus())
                .citadCode(entity.getCitadCode())
                .accountType(entity.getAccountType())
                .openPlace(entity.getOpenPlace())
                .depositoryMemberNo(entity.getDepositoryMemberNo())
                .tradingGateway(entity.getTradingGateway())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
