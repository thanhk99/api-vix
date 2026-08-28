package vix.local.api.modules.worker.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.worker.api.v1.dto.ExportJobRequest;
import vix.local.api.modules.worker.api.v1.dto.ExportJobResponseDto;
import vix.local.api.modules.worker.application.service.WorkerJobService;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.shared.dto.ApiResponse;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/exports/jobs")
@RequiredArgsConstructor
@Tag(name = "Export Jobs", description = "Quản lý tiến trình xuất file ngầm toàn hệ thống")
public class ExportController {

    private final WorkerJobService workerJobService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Tạo yêu cầu xuất file ngầm")
    public ResponseEntity<ApiResponse<ExportJobResponseDto>> createExportJob(
            @RequestBody(required = false) ExportJobRequest request,
            Authentication auth) {
        UUID userId = null;
        UUID deptId = null;
        if (auth != null && auth.getName() != null) {
            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
                deptId = user.getDepartmentId();
            }
        }

        String jobType = (request != null && request.getJobType() != null) ? request.getJobType() : "EXPORT_PARTNER";
        String payload = (request != null) ? request.getPayload() : null;
        String fileName = (request != null && request.getFileName() != null) ? request.getFileName() : "Danh_sach_doi_tac.xlsx";

        WorkerJob job = workerJobService.createExportJob(
                jobType,
                payload,
                fileName,
                userId,
                deptId
        );

        return ResponseEntity.ok(ApiResponse.success(mapToDto(job)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Kiểm tra trạng thái job xuất file")
    public ResponseEntity<ApiResponse<ExportJobResponseDto>> getJobStatus(@PathVariable UUID id) {
        WorkerJob job = workerJobService.getJobById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiến trình xuất file"));
        return ResponseEntity.ok(ApiResponse.success(mapToDto(job)));
    }

    @GetMapping("/my")
    @Operation(summary = "Danh sách job xuất file của người dùng")
    public ResponseEntity<ApiResponse<List<ExportJobResponseDto>>> getMyExportJobs(Authentication auth) {
        UUID userId = null;
        if (auth != null && auth.getName() != null) {
            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }
        List<WorkerJob> jobs = workerJobService.getUserJobs(userId);
        List<ExportJobResponseDto> dtos = jobs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Tải file đã xuất thành công")
    public ResponseEntity<Resource> downloadExportFile(@PathVariable UUID id) {
        WorkerJob job = workerJobService.getJobById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiến trình xuất file"));

        if (!"COMPLETED".equalsIgnoreCase(job.getStatus()) || job.getResult() == null) {
            throw new RuntimeException("File chưa hoàn thành hoặc không tồn tại");
        }

        File file = new File(job.getResult());
        if (!file.exists()) {
            throw new RuntimeException("File không tồn tại trên hệ thống lưu trữ");
        }

        Resource resource = new FileSystemResource(file);
        String downloadName = job.getFileName() != null ? job.getFileName() : file.getName();
        String encodedFileName = URLEncoder.encode(downloadName, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    private ExportJobResponseDto mapToDto(WorkerJob job) {
        return ExportJobResponseDto.builder()
                .id(job.getId())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .fileName(job.getFileName())
                .fileSize(job.getFileSize())
                .errorLog(job.getErrorLog())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .downloadUrl("/v1/exports/jobs/" + job.getId() + "/download")
                .build();
    }
}
