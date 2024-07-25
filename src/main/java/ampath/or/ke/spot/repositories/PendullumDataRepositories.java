package ampath.or.ke.spot.repositories;

import ampath.or.ke.spot.models.PendullumData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("pendullumDataReporitory")
public interface PendullumDataRepositories extends JpaRepository<PendullumData, Long> {
    Page<PendullumData> findAll(Pageable pageable);
    Page<PendullumData> findByprojectbeyondEnrolled(int pid,Pageable pageable);
    List<PendullumData> findByPatientIdentifierAndEncounterDate(String pid, String Date);
  //  Page<PendullumData> findByEncounterDateGreaterThanEqual(Date encounterDate, Pageable pageable);

   // @Query(value="select * from ampath_spot_live.pendullum_data_live where date(encounter_date) >='2022-01-01'" , nativeQuery = true)
  //  Page<?> getAllsummaries (Pageable pageable);
    @Query("SELECT p FROM PendullumData p WHERE date(p.encounterDate) = date(:encounterDate)")
    Page<?> getAllsummaries(String encounterDate, Pageable pageable);


}
