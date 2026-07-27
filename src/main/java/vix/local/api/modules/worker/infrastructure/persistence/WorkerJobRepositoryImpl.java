package vix.local.api.modules.worker.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkerJobRepositoryImpl implements WorkerJobRepository {

    private final WorkerJobJpaRepository jpaRepository;

    @Override
    public WorkerJob save(WorkerJob job) {
        WorkerJobEntity entity = toEntity(job);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<WorkerJob> findPendingJobs(String jobType, LocalDateTime currentTime, int limit) {
        return jpaRepository.findPendingJobs(jobType, currentTime, PageRequest.of(0, limit)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private WorkerJob toDomain(WorkerJobEntity entity) {
        if (entity == null) return null;
        return WorkerJob.builder()
                .id(entity.getId())
                .jobType(entity.getJobType())
                .payload(entity.getPayload())
                .status(entity.getStatus())
                .retryCount(entity.getRetryCount())
                .errorLog(entity.getErrorLog())
                .nextRunTime(entity.getNextRunTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private WorkerJobEntity toEntity(WorkerJob domain) {
        if (domain == null) return null;
        return WorkerJobEntity.builder()
                .id(domain.getId())
                .jobType(domain.getJobType())
                .payload(domain.getPayload())
                .status(domain.getStatus())
                .retryCount(domain.getRetryCount())
                .errorLog(domain.getErrorLog())
                .nextRunTime(domain.getNextRunTime())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
