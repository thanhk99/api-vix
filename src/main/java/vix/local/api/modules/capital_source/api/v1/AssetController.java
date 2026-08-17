package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;

import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/assets")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class AssetController {

    private final PartnerApplicationService partnerService;

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_ASSET, action = ActionCode.CREATE)
    @Operation(summary = "Thêm tài sản cho đối tác")
    public ResponseEntity<ApiResponse<Asset>> createAsset(
            @PathVariable UUID partnerId,
            @RequestBody Asset asset) {
        Asset created = partnerService.createAsset(partnerId, asset);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_ASSET, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách tài sản của đối tác")
    public ResponseEntity<ApiResponse<List<Asset>>> getAssets(
            @PathVariable UUID partnerId) {
        List<Asset> assets = partnerService.getAssetsByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.success(assets));
    }
}