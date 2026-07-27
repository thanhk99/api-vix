package vix.local.api.modules.worker.domain.repository;

import vix.local.api.modules.worker.domain.model.WorkerJob;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkerJobRepository {
    WorkerJob save(WorkerJob job);
    List<WorkerJob> findPendingJobs(String jobType, LocalDateTime currentTime, int limit);
}
