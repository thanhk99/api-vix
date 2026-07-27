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
    private String jobType; // e.g., "MAIL", "CLEANUP", "EXPORT"
    private String payload; // JSON data
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private int retryCount;
    private String errorLog;
    private LocalDateTime nextRunTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void markProcessing() {
        this.status = "PROCESSING";
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
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
