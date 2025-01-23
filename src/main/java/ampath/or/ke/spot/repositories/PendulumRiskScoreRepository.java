package ampath.or.ke.spot.repositories;

import ampath.or.ke.spot.models.PendullumData;
import ampath.or.ke.spot.models.Pendullums;
import ampath.or.ke.spot.models.PendulumRiskScores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendulumRiskScoreRepository extends JpaRepository<PendulumRiskScores, Long> {
   // Page<PendulumRiskScores> findAll(Pageable pageable);
    List<PendulumRiskScores> findAll();

}
