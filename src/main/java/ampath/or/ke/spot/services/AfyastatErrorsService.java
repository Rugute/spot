package ampath.or.ke.spot.services;

import ampath.or.ke.spot.models.AfyastatErrors;
import ampath.or.ke.spot.repositories.AfyastatErrorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("afyastatErrorsService")
public class AfyastatErrorsService {
    Date nowDate = new Date();
    private AfyastatErrorsRepository afyastatErrorsRepository;
    @Autowired
    public AfyastatErrorsService(AfyastatErrorsRepository afyastatErrorsRepository) {
        this.afyastatErrorsRepository = afyastatErrorsRepository;
    }

    public AfyastatErrors save(AfyastatErrors dataset) {
        return afyastatErrorsRepository.save(dataset);
    }
    public void deleteBank(AfyastatErrors dataset){
        afyastatErrorsRepository.delete(dataset);
    }
    public List<AfyastatErrors> getAllErrors(){return  afyastatErrorsRepository.findAll();}
    public AfyastatErrors getByID( int id){return  afyastatErrorsRepository.findById(id);}
    public List<AfyastatErrors> getTop25Errors(String disc){return  afyastatErrorsRepository.findTop25ByDiscriminator(disc);}
    public List<AfyastatErrors> getErrors(String disc){return  afyastatErrorsRepository.findByDiscriminator(disc);}
    public List<AfyastatErrors> getErrorsnullcode(String disc){return  afyastatErrorsRepository.findByDiscriminatorAndResponsecodeIsNull(disc);}

    public List<AfyastatErrors> getByDiscriminatorAndResponsecode(String disc,String code){return  afyastatErrorsRepository.findByDiscriminatorAndResponsecode(disc,code);}
    public List<AfyastatErrors> getByDiscriminatorAndResponsecodeisNull(String disc){return  afyastatErrorsRepository.findByDiscriminatorAndResponsecodeIsNull(disc);}

    public List<AfyastatErrors> getPendingErrorsrrors(){return  afyastatErrorsRepository.findByResponsecodeIsNull();}

    // public List<AfyastatErrors> getErrorsnullcode(String disc){return  afyastatErrorsRepository.findByDiscriminatorAndLocationAndResponsecodeIsNull(disc,"28");}


   /* public List<AfyastatErrors> getSpecificAllData(int mid,String org,String dataset,String period){
        return  afyastatErrorsRepository.findByMidAndOrgunitAndDatasetAndPeriod(mid,org,dataset,period);}*/
}
