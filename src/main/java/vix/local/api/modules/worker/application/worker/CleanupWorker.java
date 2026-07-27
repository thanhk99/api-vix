package vix.local.api.modules.worker.application.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupWorker {

    private final WorkerJobRepository workerJobRepository;

    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2 AM
    @Transactional
    public void processCleanupJobs() {
        log.info("CleanupWorker started processing");
        List<WorkerJob> jobs = workerJobRepository.findPendingJobs("CLEANUP", LocalDateTime.now(), 10);
        
        for (WorkerJob job : jobs) {
            job.markProcessing();
            workerJobRepository.save(job);

            try {
                // Simulate cleaning up old data or temporary files
                log.info("Running cleanup task: {}", job.getPayload());
                job.markCompleted();
            } catch (Exception e) {
                log.error("Failed to process cleanup job: {}", job.getId(), e);
                job.markFailed(e.getMessage(), 3);
            }
            workerJobRepository.save(job);
        }
    }
}
