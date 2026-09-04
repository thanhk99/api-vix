package vix.local.api.modules.capital_source.domain.service;

import org.springframework.stereotype.Service;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;

import java.util.UUID;

@Service
public class PartnerDomainService {

    private final PartnerRepository partnerRepository;

    public PartnerDomainService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Kiểm tra tính duy nhất của Mã đơn vị GD trong hệ thống
     */
    public void validateBranchCusIdUniqueness(String branchCusId, UUID excludeId) {
        if (branchCusId == null || branchCusId.trim().isEmpty()) {
            return;
        }
        String cleanBranchCusId = branchCusId.trim();
        boolean exists = (excludeId != null)
                ? partnerRepository.existsByBranchCusIdAndIdNot(cleanBranchCusId, excludeId)
                : partnerRepository.existsByBranchCusId(cleanBranchCusId);

        if (exists) {
            throw new PartnerException("Mã đơn vị GD đã tồn tại trong hệ thống!");
        }
    }

    /**
     * Xác thực toàn diện các quy tắc nghiệp vụ khi tạo mới đối tác
     */
    public void validateForCreation(Partner partner) {
        partner.validatePartner();
        validateBranchCusIdUniqueness(partner.getBranchCusId(), null);
    }

    /**
     * Xác thực toàn diện các quy tắc nghiệp vụ khi cập nhật đối tác
     */
    public void validateForUpdate(Partner partner, UUID currentId) {
        partner.validatePartner();
        validateBranchCusIdUniqueness(partner.getBranchCusId(), currentId);
    }

    /**
     * Kiểm tra xem Mã đơn vị GD có bị trùng lặp hay không (không ném Exception)
     */
    public boolean isBranchCusIdDuplicate(String branchCusId, UUID excludeId) {
        if (branchCusId == null || branchCusId.trim().isEmpty()) {
            return false;
        }
        String cleanBranchCusId = branchCusId.trim();
        return (excludeId != null)
                ? partnerRepository.existsByBranchCusIdAndIdNot(cleanBranchCusId, excludeId)
                : partnerRepository.existsByBranchCusId(cleanBranchCusId);
    }
}
