package contentflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Spring generates all SQL automatically — you just define the interface
@Repository
public interface ContentRepository extends JpaRepository<ContentItem, Long> {
    List<ContentItem> findAllByOrderByCreatedAtDesc();
    List<ContentItem> findByTypeOrderByCreatedAtDesc(String type);
}