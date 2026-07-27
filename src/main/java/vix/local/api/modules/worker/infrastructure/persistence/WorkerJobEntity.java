package vix.local.api.modules.worker.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "shared", name = "worker_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerJobEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String jobType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    private int retryCount;

    @Column(columnDefinition = "TEXT")
    private String errorLog;

    private LocalDateTime nextRunTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.nextRunTime == null) {
            this.nextRunTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
