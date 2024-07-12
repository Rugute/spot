package ampath.or.ke.spot.controllers.Rest;

import ampath.or.ke.spot.models.KashaClients;
import ampath.or.ke.spot.models.KashaDrugs;
import ampath.or.ke.spot.repositories.KashaClientsRepository;
import ampath.or.ke.spot.repositories.PendullumReporitory;
import ampath.or.ke.spot.services.KashaClientsServices;
import ampath.or.ke.spot.services.KashaDrugService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/rest/v2/api/kasha")
public class KashaAPIV2 {

    @Autowired
    private KashaClientsServices kashaClientsServices;
    @Autowired
    private KashaDrugService kashaDrugService;

    @Autowired
    private KashaClientsRepository kashaClientsRepository;

    @ResponseBody
    @RequestMapping(value="/clients", method = RequestMethod.GET,produces = "application/json")
    public ResponseEntity<Object> handlePostRequest(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "100") int size) throws ParseException, JSONException

        {

        String output = "";
        Date nowDate = new Date();
        String pattern = "yyyy-MM-dd hh:mm:ss";
            Pageable pageable = PageRequest.of(page, size);
            Page<KashaClients> kk =  kashaClientsRepository.findByEligibleAndConsented(1,1,pageable);
            List<KashaClients> kashaClientsList = kk.getContent();
            // List<KashaClients> kashaClientsList = (List<KashaClients>) kk;
        List<JSONObject> clients_entities = new ArrayList<JSONObject>();
        if(kashaClientsList.size()>0){

        String jssons="";
        for (int x=0;x<kashaClientsList.size();x++) {

            JSONObject client = new JSONObject();

            KashaClients att = kashaClientsList.get(x);

            client.put("id", kashaClientsList.get(x).getId());
            client.put("uuid", kashaClientsList.get(x).getUuid());
            client.put("person_id", kashaClientsList.get(x).getPerson_id());
            client.put("identifier", kashaClientsList.get(x).getIdentifier());
            client.put("first_name", kashaClientsList.get(x).getFirst_name());
            client.put("last_name", kashaClientsList.get(x).getLast_name());
            client.put("phone_number", kashaClientsList.get(x).getPhone_number());
            client.put("secondary_phone_number", kashaClientsList.get(x).getSecondary_phone_number());
            client.put("address", kashaClientsList.get(x).getAddress());
            client.put("estate_village", kashaClientsList.get(x).getEstate_village());
            client.put("county", kashaClientsList.get(x).getCounty());
            client.put("expected_next_delivery_date", kashaClientsList.get(x).getExpected_next_delivery_date());
            client.put("nearest_landmark", kashaClientsList.get(x).getNearest_landmark());
            client.put("gender", kashaClientsList.get(x).getGender());
            client.put("age", kashaClientsList.get(x).getAge());
            client.put("consented", kashaClientsList.get(x).getConsented());
            client.put("eligible", kashaClientsList.get(x).getEligible());
            client.put("dateConsented", kashaClientsList.get(x).getDateConsented());
            client.put("created_by", kashaClientsList.get(x).getCreated_by());
            client.put("dateCreated", kashaClientsList.get(x).getDateCreated());
            client.put("modified_by", kashaClientsList.get(x).getModified_by());
            client.put("modifiedOn", kashaClientsList.get(x).getModifiedOn());
            client.put("medication_type","ART");
            client.put("facility", kashaClientsList.get(x).getMflcode() );//
           /* List<KashaDrugs> drugsList = kashaDrugService.findByPatientId(String.valueOf(kashaClientsList.get(x).getPerson_id()));
            JSONObject drugs = new JSONObject();
            if(drugsList.size()>0){
                for(int y=0;y<drugsList.size();y++){
                    drugs.put("medication_pick_up_date",drugsList.get(y).getMedicalPickUpTCA());
                    drugs.put("regimen",drugsList.get(y).getDrug());
                    drugs.put("medication_type",drugsList.get(y).getMedicationType());
                    drugs.put("clinical_TCA",drugsList.get(y).getClinicalDate());
                    drugs.put("qty",drugsList.get(y).getQty());
                    drugs.put("prediction_score",drugsList.get(y).getPredictionScores());
                }
            }

            client.put("medication", drugs);

            */


            clients_entities.add(client);

        }

        }

        return new ResponseEntity<Object>(clients_entities, HttpStatus.OK);
    }
    @ResponseBody
    @RequestMapping(value="/clinical/{concestedDate}", method = RequestMethod.GET,produces = "application/json")//, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> handlePostRequest(@PathVariable String concestedDate) throws ParseException, JSONException {

        String output = "";
        Date nowDate = new Date();
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date concested_Date = formatter.parse(concestedDate);
        List<JSONObject> clients_entities = new ArrayList<JSONObject>();
        List<KashaClients> kashaClientsList =  kashaClientsServices.findByDateConsentedGreaterThanEqual(1,concested_Date);

        if(kashaClientsList.size()>0){
            for(int x =0;x<kashaClientsList.size();x++) {
                JSONObject client = new JSONObject();
                client.put("ccc", kashaClientsList.get(x).getIdentifier());
                client.put("first_name", kashaClientsList.get(x).getFirst_name());
                client.put("last_name", kashaClientsList.get(x).getLast_name());
                client.put("phone_number", kashaClientsList.get(x).getPhone_number());
                client.put("secondary_phone_number", kashaClientsList.get(x).getSecondary_phone_number());
                client.put("address", kashaClientsList.get(x).getAddress());
                client.put("estate_village", kashaClientsList.get(x).getEstate_village());
                client.put("county", kashaClientsList.get(x).getCounty());
                client.put("expected_next_delivery_date", kashaClientsList.get(x).getExpected_next_delivery_date());
                client.put("nearest_landmark", kashaClientsList.get(x).getNearest_landmark());
                client.put("gender", kashaClientsList.get(x).getGender());
                client.put("age", kashaClientsList.get(x).getAge());
                List<KashaDrugs> drugsList = kashaDrugService.findByPatientId(String.valueOf(kashaClientsList.get(x).getPerson_id()));
                JSONObject drugs = new JSONObject();
                if(drugsList.size()>0){
                    for(int y=0;y<drugsList.size();y++){
                        drugs.put("medication_pick_up_date",drugsList.get(y).getMedicalPickUpTCA());
                        drugs.put("regimen",drugsList.get(y).getDrug());
                        drugs.put("medication_type",drugsList.get(y).getMedicationType());
                        drugs.put("clinical_TCA",drugsList.get(y).getClinicalDate());
                        drugs.put("qty",drugsList.get(y).getQty());
                        drugs.put("prediction_score",drugsList.get(y).getPredictionScores());
                    }
                }
                client.put("medication", drugs);
                clients_entities.add(client);
            }
        }
        return new ResponseEntity<Object>(clients_entities, HttpStatus.OK);
        //return ResponseEntity.ok("Request received successfully");
    }
}
