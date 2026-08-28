package vix.local.api.modules.worker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerJob {
    private UUID id;
    private String jobType; // e.g., "MAIL", "CLEANUP", "EXPORT_PARTNER", "EXPORT_CONTRACT"
    private String payload; // JSON data
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private int retryCount;
    private String errorLog;
    private LocalDateTime nextRunTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private UUID createdBy;
    private UUID departmentId;
    private String result; // File path or result JSON
    private String fileName; // Display file name
    private Long fileSize; // Bytes

    public void markProcessing() {
        this.status = "PROCESSING";
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted(String result, String fileName, Long fileSize) {
        this.status = "COMPLETED";
        this.result = result;
        if (fileName != null) this.fileName = fileName;
        if (fileSize != null) this.fileSize = fileSize;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        markCompleted(this.result, this.fileName, this.fileSize);
    }

    public void markFailed(String errorLog, int maxRetries) {
        this.retryCount++;
        this.errorLog = errorLog;
        if (this.retryCount >= maxRetries) {
            this.status = "FAILED";
        } else {
            this.status = "PENDING";
            this.nextRunTime = LocalDateTime.now().plusMinutes(5 * retryCount); // Backoff strategy
        }
        this.updatedAt = LocalDateTime.now();
    }
}
