package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.exception.AssetException;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.model.AssetPledge;
import vix.local.api.modules.capital_source.domain.model.AssetPledgeRelease;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.CreditLimitHistory;
import vix.local.api.modules.capital_source.domain.repository.AssetPledgeReleaseRepository;
import vix.local.api.modules.capital_source.domain.repository.AssetPledgeRepository;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssetApplicationService {

    private final AssetRepository assetRepository;
    private final AssetPledgeRepository pledgeRepository;
    private final AssetPledgeReleaseRepository releaseRepository;
    private final CreditLimitRepository creditLimitRepository;
    private final CreditLimitHistoryRepository creditLimitHistoryRepository;

    // --- 8.1 DANH MỤC TÀI SẢN ---

    @Transactional
    public Asset createAsset(Asset asset, String createdBy) {
        if (asset.getAssetId() == null || asset.getAssetId().trim().isEmpty()) {
            long count = assetRepository.count();
            String generatedId;
            do {
                count++;
                generatedId = String.format("AST%05d", count);
            } while (assetRepository.existsByAssetId(generatedId));
            asset.setAssetId(generatedId);
        }
        asset.validateForCreate();
        if (assetRepository.existsByAssetId(asset.getAssetId())) {
            throw new AssetException("Mã tài sản đã tồn tại: " + asset.getAssetId());
        }
        if (asset.getTotalQuantity() == null) {
            asset.setTotalQuantity(BigDecimal.ZERO);
        }
        if (asset.getAvailQuantity() == null) {
            asset.setAvailQuantity(asset.getTotalQuantity());
        }
        if (asset.getPledgedQuantity() == null) {
            asset.setPledgedQuantity(BigDecimal.ZERO);
        }
        asset.setStatus("AVAILABLE");
        asset.setCreatedBy(createdBy);
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset updateAsset(String assetId, Asset updateData, String updatedBy) {
        Asset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản"));
        
        boolean hasPledge = pledgeRepository.existsByAssetId(assetId);
        asset.validateForUpdate(hasPledge);
        
        // Update fields allowed
        asset.setIssuer(updateData.getIssuer());
        asset.setIssuerCode(updateData.getIssuerCode());
        if (updateData.getTotalQuantity() != null) {
            asset.setTotalQuantity(updateData.getTotalQuantity());
            BigDecimal pledged = asset.getPledgedQuantity() != null ? asset.getPledgedQuantity() : BigDecimal.ZERO;
            asset.setAvailQuantity(updateData.getTotalQuantity().subtract(pledged));
        }
        asset.setMarketPrice(updateData.getMarketPrice());
        asset.setHaircutRate(updateData.getHaircutRate());
        asset.setNote(updateData.getNote());
        asset.setUpdatedBy(updatedBy);
        
        return assetRepository.save(asset);
    }

    public Page<Asset> searchAssets(String assetId, String assetType, String symbol, String status, Pageable pageable) {
        return assetRepository.findByFilters(assetId, assetType, symbol, status, pageable);
    }

    public Asset getAssetDetail(String assetId) {
        return assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản"));
    }

    // 8.5 Cập nhật giá TT
    @Transactional
    public Asset updateMarketPrice(String assetId, BigDecimal newPrice, String updatedBy) {
        Asset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản"));
        asset.updateMarketPrice(newPrice);
        asset.setUpdatedBy(updatedBy);
        return assetRepository.save(asset);
    }

    // --- 8.3 CẦM CỐ ---

    @Transactional
    public AssetPledge createPledge(AssetPledge pledge, String createdBy) {
        pledge.validateForCreate();
        pledge.calculateValues();
        
        Asset asset = assetRepository.findByAssetId(pledge.getAssetId())
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản: " + pledge.getAssetId()));
        asset.validatePledge(pledge.getPledgeQty());
        
        pledge.setStatus("PENDING");
        pledge.setCreatedBy(createdBy);
        return pledgeRepository.save(pledge);
    }

    @Transactional
    public void approvePledge(Long id, String approvedBy) {
        AssetPledge pledge = pledgeRepository.findById(id)
                .orElseThrow(() -> new AssetException("Không tìm thấy giao dịch cầm cố"));
        
        pledge.approve(approvedBy);
        
        Asset asset = assetRepository.findByAssetId(pledge.getAssetId())
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản"));
        asset.applyPledge(pledge.getPledgeQty());
        
        pledgeRepository.save(pledge);
        assetRepository.save(asset);

        // Tự động tăng hạn mức tín dụng tương ứng khi duyệt Cầm cố
        if (pledge.getLimitId() != null && !pledge.getLimitId().isBlank()) {
            CreditLimit creditLimit = creditLimitRepository.findByLimitId(pledge.getLimitId());
            if (creditLimit != null) {
                BigDecimal inc = pledge.getCollateralValue() != null ? pledge.getCollateralValue() : BigDecimal.ZERO;
                if (inc.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal oldTotal = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : BigDecimal.ZERO;
                    BigDecimal oldUsed = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : BigDecimal.ZERO;
                    BigDecimal oldRemain = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : oldTotal.subtract(oldUsed);

                    BigDecimal newTotal = oldTotal.add(inc);
                    BigDecimal newRemain = newTotal.subtract(oldUsed);

                    creditLimit.setTotalPool(newTotal);
                    creditLimit.setRemainPool(newRemain);
                    creditLimitRepository.save(creditLimit);

                    CreditLimitHistory history = CreditLimitHistory.builder()
                            .creditLimitId(creditLimit.getId())
                            .transactionType("ASSET_MORTGAGE")
                            .amount(inc)
                            .preTotalPool(oldTotal)
                            .preUsedPool(oldUsed)
                            .preRemainPool(oldRemain)
                            .newTotalPool(newTotal)
                            .newUsedPool(oldUsed)
                            .newRemainPool(newRemain)
                            .referenceId("Cầm cố TSBĐ " + pledge.getAssetId() + " (SL: " + pledge.getPledgeQty() + ")")
                            .transactionDate(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build();
                    creditLimitHistoryRepository.save(history);
                }
            }
        }
    }

    @Transactional
    public void rejectPledge(Long id, String reason, String rejectedBy) {
        AssetPledge pledge = pledgeRepository.findById(id)
                .orElseThrow(() -> new AssetException("Không tìm thấy giao dịch cầm cố"));
        pledge.reject(rejectedBy, reason);
        pledgeRepository.save(pledge);
    }

    public Page<AssetPledge> searchPledges(String cusId, String contractNo, String limitId, String assetId, String status, Pageable pageable) {
        return pledgeRepository.findByFilters(cusId, contractNo, limitId, assetId, status, pageable);
    }

    public AssetPledge getPledgeDetail(Long id) {
        return pledgeRepository.findById(id)
                .orElseThrow(() -> new AssetException("Không tìm thấy giao dịch cầm cố"));
    }

    // --- 8.4 GIẢI TỎA ---

    @Transactional
    public AssetPledgeRelease createRelease(Long pledgeId, AssetPledgeRelease release, String createdBy) {
        AssetPledge pledge = pledgeRepository.findById(pledgeId)
                .orElseThrow(() -> new AssetException("Không tìm thấy giao dịch cầm cố"));
                
        release.validateForCreate(pledge.getRemainingQty());

        // Tính toán Giá trị giải tỏa quy đổi
        BigDecimal price = pledge.getPrice() != null ? pledge.getPrice() : BigDecimal.ZERO;
        BigDecimal haircut = pledge.getHaircutRate() != null ? pledge.getHaircutRate() : BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.ONE.subtract(haircut.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
        BigDecimal releaseCollateralVal = release.getReleaseQty().multiply(price).multiply(multiplier);
        release.setReleaseValue(releaseCollateralVal);

        // Quy tắc nghiệp vụ: Kiểm tra dư nợ & Tỷ lệ bảo đảm khi Giải tỏa
        if (pledge.getLimitId() != null && !pledge.getLimitId().isBlank()) {
            CreditLimit creditLimit = creditLimitRepository.findByLimitId(pledge.getLimitId());
            if (creditLimit != null) {
                BigDecimal currentTotal = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : BigDecimal.ZERO;
                BigDecimal currentUsed = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : BigDecimal.ZERO;
                BigDecimal newTotal = currentTotal.subtract(releaseCollateralVal);

                if (newTotal.compareTo(currentUsed) < 0 && !Boolean.TRUE.equals(release.getIsExceptionApproved())) {
                    throw new AssetException("Không thể giải tỏa tài sản: Hạn mức sau giải tỏa (" 
                            + newTotal.setScale(0, RoundingMode.HALF_UP).toPlainString() 
                            + " VND) nhỏ hơn dư nợ đã sử dụng (" 
                            + currentUsed.setScale(0, RoundingMode.HALF_UP).toPlainString() 
                            + " VND). Vui lòng tất toán bớt dư nợ hoặc xin phê duyệt ngoại lệ.");
                }
            }
        }

        release.setPledgeId(pledgeId);
        release.setStatus("PENDING");
        release.setCreatedBy(createdBy);
        return releaseRepository.save(release);
    }

    @Transactional
    public void approveRelease(Long releaseId, String approvedBy) {
        AssetPledgeRelease release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new AssetException("Không tìm thấy yêu cầu giải tỏa"));
        
        release.approve(approvedBy);
        
        AssetPledge pledge = pledgeRepository.findById(release.getPledgeId())
                .orElseThrow(() -> new AssetException("Không tìm thấy giao dịch cầm cố"));
                
        pledge.applyRelease(release.getReleaseQty());
        
        Asset asset = assetRepository.findByAssetId(pledge.getAssetId())
                .orElseThrow(() -> new AssetException("Không tìm thấy tài sản"));
                
        asset.applyRelease(release.getReleaseQty());
        
        releaseRepository.save(release);
        pledgeRepository.save(pledge);
        assetRepository.save(asset);

        // Tự động giảm hạn mức tín dụng tương ứng khi duyệt Giải tỏa
        if (pledge.getLimitId() != null && !pledge.getLimitId().isBlank()) {
            CreditLimit creditLimit = creditLimitRepository.findByLimitId(pledge.getLimitId());
            if (creditLimit != null) {
                BigDecimal dec = release.getReleaseValue() != null ? release.getReleaseValue() : BigDecimal.ZERO;
                if (dec.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal oldTotal = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : BigDecimal.ZERO;
                    BigDecimal oldUsed = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : BigDecimal.ZERO;
                    BigDecimal oldRemain = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : oldTotal.subtract(oldUsed);

                    BigDecimal newTotal = oldTotal.subtract(dec);
                    BigDecimal newRemain = newTotal.subtract(oldUsed);

                    creditLimit.setTotalPool(newTotal);
                    creditLimit.setRemainPool(newRemain);
                    creditLimitRepository.save(creditLimit);

                    CreditLimitHistory history = CreditLimitHistory.builder()
                            .creditLimitId(creditLimit.getId())
                            .transactionType("ASSET_RELEASE")
                            .amount(dec)
                            .preTotalPool(oldTotal)
                            .preUsedPool(oldUsed)
                            .preRemainPool(oldRemain)
                            .newTotalPool(newTotal)
                            .newUsedPool(oldUsed)
                            .newRemainPool(newRemain)
                            .referenceId("Giải tỏa TSBĐ " + pledge.getAssetId() + " (SL: " + release.getReleaseQty() + ")")
                            .transactionDate(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build();
                    creditLimitHistoryRepository.save(history);
                }
            }
        }
    }

    @Transactional
    public void rejectRelease(Long releaseId, String reason, String rejectedBy) {
        AssetPledgeRelease release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new AssetException("Không tìm thấy yêu cầu giải tỏa"));
        release.reject(rejectedBy, reason);
        releaseRepository.save(release);
    }

    public Page<AssetPledgeRelease> searchReleases(Pageable pageable) {
        Page<AssetPledgeRelease> page = releaseRepository.findAll(pageable);
        page.getContent().forEach(r -> {
            if (r.getPledgeId() != null) {
                pledgeRepository.findById(r.getPledgeId()).ifPresent(p -> {
                    r.setAssetId(p.getAssetId());
                    r.setContractNo(p.getContractNo());
                    r.setLimitId(p.getLimitId());
                    r.setCusId(p.getCusId());
                });
            }
        });
        return page;
    }
}
