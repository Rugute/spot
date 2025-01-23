package ampath.or.ke.spot.services;

import ampath.or.ke.spot.models.AfyastatClientLineList;
import ampath.or.ke.spot.models.Facilities;
import ampath.or.ke.spot.models.KashaClients;
import ampath.or.ke.spot.repositories.AfyastatErrorsRepository;
import ampath.or.ke.spot.repositories.KashaClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

@Service("kashaService")
public class KashaClientsServices {
    Date nowDate = new Date();
    private KashaClientsRepository kashaClientsRepository;
    @Autowired
    public KashaClientsServices(KashaClientsRepository kashaClientsRepository) {
        this.kashaClientsRepository = kashaClientsRepository;
    }
    public KashaClients save(KashaClients dataset) {
        return kashaClientsRepository.save(dataset);
    }
    public List<KashaClients> getAllDataset(){return  kashaClientsRepository.findAll();}

    public List<KashaClients> findByDateConsentedGreaterThanEqual(int eligible,Date created_on){
        return  kashaClientsRepository.findByEligibleAndModifiedOnGreaterThanEqual(eligible,created_on);}


    public List<KashaClients> getByIdentifier(String ccc){return  kashaClientsRepository.findByIdentifier(ccc);}

    public List<KashaClients> getDatasetByDate(){return  kashaClientsRepository.findAll();}

    public List<KashaClients> getAllEligible(int eligible){return  kashaClientsRepository.findByEligible(eligible);}
    //public List<KashaClients> getAllEligible(int eligible){return  kashaClientsRepository.findByEligible(eligible);}
   public List<KashaClients> getEligibleClients(int eligible, Pageable pageable) {
       Page<KashaClients> clientsPage = kashaClientsRepository.findByEligibleAndConsented(eligible,1, (org.springframework.data.domain.Pageable) pageable);
       return clientsPage.getContent(); // Get the list of clients from the page
   }
    public List<KashaClients> getNoSyncedToPendulum(int eligible,int consented,int inpedulum){return  kashaClientsRepository.findByEligibleAndConsentedAndSyncedToPendulum(eligible,consented,inpedulum);}

    public List<KashaClients> findByMd5PersonId(String md5){return  kashaClientsRepository.findByMd5PersonId(md5);}

}
