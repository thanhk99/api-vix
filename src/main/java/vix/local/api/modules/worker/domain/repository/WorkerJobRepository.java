package vix.local.api.modules.worker.domain.repository;

import vix.local.api.modules.worker.domain.model.WorkerJob;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerJobRepository {
    WorkerJob save(WorkerJob job);
    Optional<WorkerJob> findById(UUID id);
    List<WorkerJob> findPendingJobs(String jobType, LocalDateTime currentTime, int limit);
    List<WorkerJob> findPendingExportJobs(LocalDateTime currentTime, int limit);
    List<WorkerJob> findByCreatedBy(UUID createdBy);
    List<WorkerJob> findAllExports();
}
