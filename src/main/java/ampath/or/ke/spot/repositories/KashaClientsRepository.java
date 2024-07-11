package ampath.or.ke.spot.repositories;

import ampath.or.ke.spot.models.HTSSummaries;
import ampath.or.ke.spot.models.KashaClients;
import ampath.or.ke.spot.models.PendullumData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("kashaclientsRepository")
public interface KashaClientsRepository extends JpaRepository<KashaClients, Long> {
    List<KashaClients> findAll();
    List<KashaClients> findByIdentifier(String ccno);
    List<KashaClients> findByEligibleAndModifiedOnGreaterThanEqual(int eligible,Date createdOn);
    List<KashaClients> findByEligible(int eligible);

    Page<KashaClients> findByEligibleAndConsented(int eligible,int consented,Pageable pageable);
    List<KashaClients> findByEligibleAndConsentedAndSyncedToPendulum(int eligible,int consented, int syncedToPendulum);

}
