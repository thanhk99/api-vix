package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import vix.local.api.modules.capital_source.application.service.AssetApplicationService;
import vix.local.api.modules.capital_source.domain.model.AssetPledge;
import vix.local.api.modules.capital_source.domain.model.AssetPledgeRelease;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.Map;

@RestController
@RequestMapping("/v1/capital-source/asset-pledges")
@RequiredArgsConstructor
@Tag(name = "Capital Source - Asset Pledge")
public class AssetPledgeController {

    private final AssetApplicationService assetService;

    @PostMapping
    @Operation(summary = "Gán tài sản bảo đảm (8.3)")
    public ResponseEntity<ApiResponse<AssetPledge>> createPledge(@RequestBody AssetPledge pledge) {
        String createdBy = "admin";
        return ResponseEntity.ok(ApiResponse.success(assetService.createPledge(pledge, createdBy)));
    }

    @GetMapping
    @Operation(summary = "Danh sách Cầm cố (8.3)")
    public ResponseEntity<ApiResponse<PagedResponse<AssetPledge>>> searchPledges(
            @RequestParam(required = false) String cusId,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String limitId,
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<AssetPledge> resultPage = assetService.searchPledges(cusId, contractNo, limitId, assetId, status, pageable);

        PagedResponse<AssetPledge> response = PagedResponse.<AssetPledge>builder()
                .content(resultPage.getContent())
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .isLast(resultPage.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/releases")
    @Operation(summary = "Danh sách yêu cầu Giải tỏa TSĐB")
    public ResponseEntity<ApiResponse<PagedResponse<AssetPledgeRelease>>> getAllReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<AssetPledgeRelease> resultPage = assetService.searchReleases(pageable);

        PagedResponse<AssetPledgeRelease> response = PagedResponse.<AssetPledgeRelease>builder()
                .content(resultPage.getContent())
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .isLast(resultPage.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết Cầm cố")
    public ResponseEntity<ApiResponse<AssetPledge>> getPledgeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(assetService.getPledgeDetail(id)));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt Cầm cố")
    public ResponseEntity<ApiResponse<Void>> approvePledge(@PathVariable Long id) {
        String approvedBy = "admin";
        assetService.approvePledge(id, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Từ chối Cầm cố")
    public ResponseEntity<ApiResponse<Void>> rejectPledge(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String rejectedBy = "admin";
        String reason = request.get("reason");
        assetService.rejectPledge(id, reason, rejectedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{pledgeId}/releases")
    @Operation(summary = "Tạo yêu cầu Giải tỏa (8.4)")
    public ResponseEntity<ApiResponse<AssetPledgeRelease>> createRelease(
            @PathVariable Long pledgeId,
            @RequestBody AssetPledgeRelease release) {
        String createdBy = "admin";
        return ResponseEntity.ok(ApiResponse.success(assetService.createRelease(pledgeId, release, createdBy)));
    }

    @PutMapping("/releases/{releaseId}/approve")
    @Operation(summary = "Duyệt Giải tỏa")
    public ResponseEntity<ApiResponse<Void>> approveRelease(@PathVariable Long releaseId) {
        String approvedBy = "admin";
        assetService.approveRelease(releaseId, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/releases/{releaseId}/reject")
    @Operation(summary = "Từ chối Giải tỏa")
    public ResponseEntity<ApiResponse<Void>> rejectRelease(
            @PathVariable Long releaseId,
            @RequestBody Map<String, String> request) {
        String rejectedBy = "admin";
        String reason = request.get("reason");
        assetService.rejectRelease(releaseId, reason, rejectedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
