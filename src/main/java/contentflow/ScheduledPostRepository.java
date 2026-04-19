package contentflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledPostRepository extends JpaRepository<ScheduledPost, Long> {
    List<ScheduledPost> findAllByOrderByScheduledForAsc();

    // "Give me all PENDING posts whose scheduledFor time is before NOW"
    List<ScheduledPost> findByStatusAndScheduledForBefore(String status, LocalDateTime time);
}