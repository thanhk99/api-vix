package vix.local.api.modules.worker.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkerJobJpaRepository extends JpaRepository<WorkerJobEntity, UUID> {
    
    @Query("SELECT j FROM WorkerJobEntity j WHERE j.jobType = :jobType AND j.status = 'PENDING' AND j.nextRunTime <= :currentTime ORDER BY j.nextRunTime ASC")
    List<WorkerJobEntity> findPendingJobs(String jobType, LocalDateTime currentTime, Pageable pageable);
}
