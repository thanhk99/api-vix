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
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/v1/capital-source/assets")
@RequiredArgsConstructor
@Tag(name = "Capital Source - Asset Catalog")
public class AssetCatalogController {

    private final AssetApplicationService assetService;

    @PostMapping
    @Operation(summary = "Thêm mới tài sản (8.1)")
    public ResponseEntity<ApiResponse<Asset>> createAsset(@RequestBody Asset asset) {
        String createdBy = "admin"; // Lấy từ security context sau
        Asset created = assetService.createAsset(asset, createdBy);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @Operation(summary = "Danh sách tài sản (8.1)")
    public ResponseEntity<ApiResponse<PagedResponse<Asset>>> searchAssets(
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Asset> resultPage = assetService.searchAssets(assetId, assetType, symbol, status, pageable);

        PagedResponse<Asset> response = PagedResponse.<Asset>builder()
                .content(resultPage.getContent())
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .isLast(resultPage.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Chi tiết tài sản")
    public ResponseEntity<ApiResponse<Asset>> getAsset(@PathVariable String assetId) {
        return ResponseEntity.ok(ApiResponse.success(assetService.getAssetDetail(assetId)));
    }

    @PutMapping("/{assetId}")
    @Operation(summary = "Cập nhật tài sản")
    public ResponseEntity<ApiResponse<Asset>> updateAsset(
            @PathVariable String assetId, 
            @RequestBody Asset asset) {
        String updatedBy = "admin"; // Lấy từ security context sau
        return ResponseEntity.ok(ApiResponse.success(assetService.updateAsset(assetId, asset, updatedBy)));
    }

    @PatchMapping("/{assetId}/price")
    @Operation(summary = "Cập nhật giá thị trường (8.5)")
    public ResponseEntity<ApiResponse<Asset>> updatePrice(
            @PathVariable String assetId, 
            @RequestBody Map<String, BigDecimal> request) {
        String updatedBy = "admin";
        BigDecimal newPrice = request.get("marketPrice");
        return ResponseEntity.ok(ApiResponse.success(assetService.updateMarketPrice(assetId, newPrice, updatedBy)));
    }
}
