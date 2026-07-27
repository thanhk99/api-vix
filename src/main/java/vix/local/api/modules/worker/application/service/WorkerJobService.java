package vix.local.api.modules.worker.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

@Service
@RequiredArgsConstructor
public class WorkerJobService {

    private final WorkerJobRepository workerJobRepository;

    @Transactional
    public void scheduleJob(String jobType, String payload) {
        WorkerJob job = WorkerJob.builder()
                .jobType(jobType)
                .payload(payload)
                .status("PENDING")
                .retryCount(0)
                .build();
        workerJobRepository.save(job);
    }
}
