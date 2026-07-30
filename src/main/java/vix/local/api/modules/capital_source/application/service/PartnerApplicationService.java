package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.repository.AuthorizationRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerApplicationService {

    private final PartnerRepository partnerRepository;
    private final AuthorizationRepository authorizationRepository;
    private final CreditLimitRepository creditLimitRepository;
    private final AssetRepository assetRepository;

    public Partner createPartner(Partner partner) {
        // Validate partner
        partner.validatePartner();

        // Tạo đối tác mới
        return partnerRepository.save(partner);
    }

    public Partner getPartner(UUID id) {
        return partnerRepository.findById(id);
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    // Quản lý uỷ quyền cho đối tác
    public Authorization createAuthorization(UUID partnerId, Authorization authorization) {
        // Validate authorization
        authorization.validateAuthorization();

        // Gán partnerId và lưu
        authorization.setPartnerId(partnerId);
        return authorizationRepository.save(authorization);
    }

    public List<Authorization> getAuthorizationsByPartnerId(UUID partnerId) {
        return authorizationRepository.findByPartnerId(partnerId);
    }

    // Quản lý hạn mức cho đối tác
    public CreditLimit createCreditLimit(UUID partnerId, CreditLimit creditLimit) {
        // Validate credit limit
        creditLimit.validateCreditLimit();

        // Tính toán remainPool
        if (creditLimit.getTotalPool() != null) {
            creditLimit.setRemainPool(creditLimit.getTotalPool());
        }

        // Gán partnerId và lưu
        creditLimit.setPartnerId(partnerId);
        return creditLimitRepository.save(creditLimit);
    }

    public List<CreditLimit> getCreditLimitsByPartnerId(UUID partnerId) {
        return creditLimitRepository.findByPartnerId(partnerId);
    }

    // Quản lý tài sản đảm bảo cho đối tác
    public Asset createAsset(UUID partnerId, Asset asset) {
        // Validate asset
        asset.validateAsset();

        // Gán partnerId và lưu
        asset.setPartnerId(partnerId);
        return assetRepository.save(asset);
    }

    public List<Asset> getAssetsByPartnerId(UUID partnerId) {
        return assetRepository.findByPartnerId(partnerId);
    }
}