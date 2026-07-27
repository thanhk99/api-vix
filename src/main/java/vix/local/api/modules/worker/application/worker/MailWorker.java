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
public class MailWorker {

    private final WorkerJobRepository workerJobRepository;

    @Scheduled(fixedDelay = 10000) // Runs every 10 seconds
    @Transactional
    public void processMailJobs() {
        log.debug("MailWorker started processing");
        List<WorkerJob> jobs = workerJobRepository.findPendingJobs("MAIL", LocalDateTime.now(), 10);
        
        for (WorkerJob job : jobs) {
            job.markProcessing();
            workerJobRepository.save(job); // Commit status change early if possible (requires separate transaction in real app)

            try {
                // Simulate sending mail
                log.info("Sending mail for job: {}", job.getId());
                // Throw exception to test retry: if (true) throw new RuntimeException("Mail server down");
                
                job.markCompleted();
            } catch (Exception e) {
                log.error("Failed to process mail job: {}", job.getId(), e);
                job.markFailed(e.getMessage(), 3);
            }
            workerJobRepository.save(job);
        }
    }
}
