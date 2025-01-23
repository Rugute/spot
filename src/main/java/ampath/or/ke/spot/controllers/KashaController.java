package ampath.or.ke.spot.controllers;

import ampath.or.ke.spot.models.KashaDeliveries;
import ampath.or.ke.spot.services.KashaDeliveriesService;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Connection;
import java.util.List;

//@Controller
//@Transactional
//@RequestMapping("/clients")
@Controller
@Transactional
@RequestMapping("/kasha")
public class KashaController {

    @Value("${spring.etl.username}")
    public  String username;
    @Value("${spring.etl.password}")
    public String password;
    @Value("${spring.etl.server}")
    public  String server;

    @Value("${spring.openmrs.url}")
    private String url;

    @Value("${spring.openmrs.live.sync}")
    private String authSynccode;

    @Autowired
        KashaDeliveriesService kashaDeliveriesService;

    // new encounter '987009c6-6f24-43f7-9640-c285d6553c63' formId='1447'
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ResponseEntity<Object> view(HttpSession session) throws IOException, JSONException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, SQLException {

       List<KashaDeliveries> kashaDeliveriesList = kashaDeliveriesService.getDeliveredInAMRS(0,"true");
       String fvalues="";
        //kashaDeliveriesList.size()
       for(int x=0;x<1;x++) {

           KashaDeliveries ks = kashaDeliveriesList.get(x);

           String person_id = String.valueOf(kashaDeliveriesList.get(x).getPerson_id());
           String edate = kashaDeliveriesList.get(x).getCreated_at();

           String sql = "SELECT p.uuid,  c.uuid concept_uuid, \n" +
                   "x.encounter_id  ,x.location_uuid  , case when o.value_coded is null then o.value_datetime else cc.uuid end output, c.concept_id\n" +
                   " From amrs.obs o \n" +
                   " INNER JOIN amrs.concept c ON c.concept_id = o.concept_id\n" +
                   "  INNER JOIN amrs.person p on p.person_id=o.person_id\n" +
                   " left join amrs.concept cc on o.value_coded=cc.concept_id\n" +
                   " INNER JOIN\n" +
                   "(SELECT  \n" +
                   " e.encounter_id, \n" +
                   " e.location_id,\n" +
                   " l.uuid location_uuid\n" +
                   "FROM amrs.encounter e\n" +
                   " inner join amrs.location l on e.location_id=l.location_id\n" +
                   "WHERE e.patient_id = '"+person_id +"' \n" +
                   "  AND e.encounter_type = 2 \n" +
                   "ORDER BY e.encounter_id DESC \n" +
                   " LIMIT 1) x on x.encounter_id=o.encounter_id\n" +
                   " where o.concept_id in(1265,1261,1268,1088,1109,5096,1192,6744,1255)\n";
           Connection conn = DriverManager.getConnection(server, username, password);
           Statement stmtt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           System.out.println("Text " + sql);
           ResultSet resultSett = stmtt.executeQuery(sql);
           String puuid = "";
           JSONArray jsonObservations = new JSONArray();

           String luuid = "";

           while (resultSett.next()) {
               System.out.println("concept_id "+ resultSett.getString("concept_id").toString());
               luuid=resultSett.getString("location_uuid").toString();
               puuid = resultSett.getString("uuid").toString();
               JSONObject jsonObservation = new JSONObject();
               //  jsonObservation.put("person",resultSett.getString("uuid").toString());//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
               jsonObservation.put("concept", resultSett.getString("concept_uuid").toString());//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
               jsonObservation.put("value", resultSett.getString("output").toString());
               jsonObservation.put("obsDatetime", edate);//+"T06:08:25.000+0000"
               jsonObservations.put(jsonObservation);
           }
                //Pickup Facility
           JSONObject jsonObservation = new JSONObject();
           jsonObservation.put("concept","2de21f17-b9ec-4934-8b79-a6e7baa3a5a0" );//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
           jsonObservation.put("value", "a89c2f42-1350-11df-a1f1-0026b9348838");
           jsonObservation.put("obsDatetime", edate);//+"T06:08:25.000+0000"
           jsonObservations.put(jsonObservation);

           //INDIVIDUAL WHO PICKED DRUGS
           JSONObject jsonwhopickeddrugs = new JSONObject();
           jsonwhopickeddrugs.put("concept","a89cd410-1350-11df-a1f1-0026b9348838" );//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
           jsonwhopickeddrugs.put("value", "d1c4a2c9-6326-41ab-baae-8239ec697620");
           jsonwhopickeddrugs.put("obsDatetime", edate);//+"T06:08:25.000+0000"
           jsonObservations.put(jsonwhopickeddrugs);

           //THERAPEUTIC PLAN NOTES
           JSONObject jsonplan = new JSONObject();
           jsonplan.put("concept","23f710cc-7f9c-4255-9b6b-c3e240215dba" );//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
           jsonplan.put("value", "Drug delivery by project beyond");
           jsonplan.put("obsDatetime", edate);//+"T06:08:25.000+0000"
           jsonObservations.put(jsonplan);
           //


           //

           JSONObject jsonVisit = new JSONObject();
           jsonVisit.put("patient", puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
           jsonVisit.put("visitType", "d4ac2aa5-2899-42fb-b08a-d40161815b48");
           jsonVisit.put("startDatetime", edate); //+"T06:08:25.000+0000"
           jsonVisit.put("stopDatetime", edate); //+"T06:09:25.000+0000"

           JSONArray jsonProviders = new JSONArray();
           JSONObject jsonProvider = new JSONObject();
           jsonProvider.put("provider", "pb6e31da-1359-11df-a1f1-0026b9348838"); //Super Users
           jsonProvider.put("encounterRole", "a0b03050-c99b-11e0-9572-0800200c9a66");
           jsonProviders.put(jsonProvider);

           JSONObject jsonEncounter = new JSONObject();
           jsonEncounter.put("encounterProviders", jsonProviders);
           jsonEncounter.put("patient", puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
           jsonEncounter.put("encounterDatetime", edate); //+"T06:09:00.000+0000"
           jsonEncounter.put("encounterType", "d34bad47-8219-4615-a28c-b7d7867cf1f4");// "987009c6-6f24-43f7-9640-c285d6553c63"); Drug Pickup
           jsonEncounter.put("form", "66e02a21-345a-48cb-9334-ef1f4c17abaa"); //1447
           jsonEncounter.put("location", luuid);
           jsonEncounter.put("visit", jsonVisit);
           jsonEncounter.put("obs", jsonObservations);
           fvalues = fvalues+jsonEncounter.toString();

           System.out.println(fvalues);
           //set cookies
         /*  OkHttpClient sessionclient = new OkHttpClient();
           Request sessionrequest = new Request.Builder()
                   .url( url+"/session")
                   .method("GET", null)
                   .addHeader("Authorization", "Basic " + authSynccode)
                   .build();
           Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

           String json = sessionresponse.body().string();
           System.out.println("Session "+json);
           JSONObject jsonObject = new JSONObject(json);
           String sessionID = (String) jsonObject.get("sessionId");
           String cookie="JSESSIONID="+sessionID;
           session.setAttribute("cookie", cookie);  */
           OkHttpClient client = new OkHttpClient();
           MediaType mediaType = MediaType.parse("application/json");
           RequestBody body = RequestBody.create(mediaType, fvalues);
           Request request = new Request.Builder()
                   .url(url + "/encounter")
                   .method("POST", body)
                   .addHeader("Authorization", "Basic " + authSynccode)
                   .addHeader("Content-Type", "application/json")
                   .build();
           Response response = client.newCall(request).execute();

           String resBody = response.request().toString();
           int rescode = response.code();

           // System.out.println("Response ndo hii " + jsonUser.toString());
          // System.out.println("Response ndo hii " + response.request());

           //end of set cookies
          /* OkHttpClient client = new OkHttpClient();
           MediaType mediaType = MediaType.parse("application/json");
           RequestBody body = RequestBody.create(mediaType, fvalues);
           Request request = new Request.Builder()
                   .url(url + "encounter")
                   .method("POST", body)
                   .addHeader("Authorization", "Basic " + authSynccode)
                   .addHeader("Content-Type", "application/json")
                   .build();
           Response response = client.newCall(request).execute(); */

           //System.out.println("Payload "+payload);
          // System.out.println("Response ndo hii " + fvalues.toString());
           System.out.println("Response ndo hii " + resBody);
           ks.setInAMRS(1);
           if(rescode==201) {
               kashaDeliveriesService.save(ks);
           }
       }

          return new ResponseEntity<Object>(fvalues, HttpStatus.OK);
    }

}
