package vix.local.api.modules.worker.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public Optional<WorkerJob> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorkerJob> findPendingJobs(String jobType, LocalDateTime currentTime, int limit) {
        return jpaRepository.findPendingJobs(jobType, currentTime, PageRequest.of(0, limit)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkerJob> findPendingExportJobs(LocalDateTime currentTime, int limit) {
        return jpaRepository.findPendingExportJobs(currentTime, PageRequest.of(0, limit)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkerJob> findByCreatedBy(UUID createdBy) {
        return jpaRepository.findByCreatedByOrderByCreatedAtDesc(createdBy).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkerJob> findAllExports() {
        return jpaRepository.findByJobTypeStartingWithOrderByCreatedAtDesc("EXPORT_").stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private WorkerJob toDomain(WorkerJobEntity entity) {
        if (entity == null) return null;
        WorkerJob job = new WorkerJob();
        job.setId(entity.getId());
        job.setJobType(entity.getJobType());
        job.setPayload(entity.getPayload());
        job.setStatus(entity.getStatus());
        job.setRetryCount(entity.getRetryCount());
        job.setErrorLog(entity.getErrorLog());
        job.setNextRunTime(entity.getNextRunTime());
        job.setCreatedBy(entity.getCreatedBy());
        job.setDepartmentId(entity.getDepartmentId());
        job.setResult(entity.getResult());
        job.setFileName(entity.getFileName());
        job.setFileSize(entity.getFileSize());
        job.setCreatedAt(entity.getCreatedAt());
        job.setUpdatedAt(entity.getUpdatedAt());
        return job;
    }

    private WorkerJobEntity toEntity(WorkerJob domain) {
        if (domain == null) return null;
        WorkerJobEntity entity = new WorkerJobEntity();
        entity.setId(domain.getId());
        entity.setJobType(domain.getJobType());
        entity.setPayload(domain.getPayload());
        entity.setStatus(domain.getStatus());
        entity.setRetryCount(domain.getRetryCount());
        entity.setErrorLog(domain.getErrorLog());
        entity.setNextRunTime(domain.getNextRunTime());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setResult(domain.getResult());
        entity.setFileName(domain.getFileName());
        entity.setFileSize(domain.getFileSize());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
