package ampath.or.ke.spot.services;

import ampath.or.ke.spot.models.SMTPServer;
import ampath.or.ke.spot.models.User;
import ampath.or.ke.spot.models.VlsOrders;
import ampath.or.ke.spot.repositories.SMTPServerRepository;
import ampath.or.ke.spot.repositories.VlsOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("vlsOrdersService")
public class VlsOrdersService {
    private VlsOrdersRepository vlsOrdersRepository;
    Date nowDate = new Date();
    @Autowired
    public VlsOrdersService(VlsOrdersRepository vlsOrdersRepository) {
        this.vlsOrdersRepository = vlsOrdersRepository;

    }
    public VlsOrders save( VlsOrders vlsOrders) {
        return vlsOrdersRepository.saveAndFlush(vlsOrders);
    }
    public List<VlsOrders> findEncounterUUID(String uuid){return vlsOrdersRepository.findByEncounterUuid(uuid);}
}
