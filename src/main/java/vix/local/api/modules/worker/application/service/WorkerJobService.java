package vix.local.api.modules.worker.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerJobService {

    private final WorkerJobRepository workerJobRepository;

    @Transactional
    public WorkerJob scheduleJob(String jobType, String payload) {
        WorkerJob job = new WorkerJob();
        job.setJobType(jobType);
        job.setPayload(payload);
        job.setStatus("PENDING");
        job.setRetryCount(0);
        job.setNextRunTime(LocalDateTime.now());
        return workerJobRepository.save(job);
    }

    @Transactional
    public WorkerJob createExportJob(String jobType, String payload, String fileName, UUID createdBy, UUID departmentId) {
        WorkerJob job = new WorkerJob();
        job.setJobType(jobType);
        job.setPayload(payload);
        job.setFileName(fileName);
        job.setCreatedBy(createdBy);
        job.setDepartmentId(departmentId);
        job.setStatus("PENDING");
        job.setRetryCount(0);
        job.setNextRunTime(LocalDateTime.now());
        return workerJobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public Optional<WorkerJob> getJobById(UUID id) {
        return workerJobRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<WorkerJob> getUserJobs(UUID createdBy) {
        if (createdBy == null) {
            return workerJobRepository.findAllExports();
        }
        return workerJobRepository.findByCreatedBy(createdBy);
    }

    @Transactional(readOnly = true)
    public List<WorkerJob> getAllExportJobs() {
        return workerJobRepository.findAllExports();
    }
}
