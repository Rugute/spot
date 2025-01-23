package ampath.or.ke.spot.scheduling;

import ampath.or.ke.spot.models.VlsOrders;
import ampath.or.ke.spot.services.VlsOrdersService;
import com.squareup.okhttp.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public  class  VLTask {
    @Value("${spring.openmrs.username}")
    private String openmrs_username;
    @Value("${spring.openmrs.password}")
    private String openmrs_password;
    @Value("${spring.openmrslive.username}")
    private String openmrslive_username;
    @Value("${spring.openmrslive.password}")
    private String openmrslive_password;


    @Value("${spring.etl.username}")
    public  String username;
    @Value("${spring.etl.password}")
    public String password;
    @Value("${spring.etl.server}")
    public  String server;

    @Value("${spring.etl.amrsserver}")
    public  String amrsserver;
    @Value("${spring.openmrs.live.authcode}")
    public String authcode;
    @Value("${spring.openmrs.local.url}")
    public  String openmrsURL;
    @Autowired
    private VlsOrdersService vlsOrdersService;

    //@Scheduled(cron = "0 */30 * ? * *")
    //@Scheduled(cron = "0 */1 * ? * *")
    //@Scheduled(cron = "0 0,59 * * * *") // 30 minutes
    public void  Processencounters() throws IOException, JSONException, SQLException {

        String originalInput = openmrslive_username+":"+openmrslive_password;
        String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());
        System.out.println(openmrsURL);
        OkHttpClient sessionclient = new OkHttpClient();
        Request sessionrequest = new Request.Builder()
                .url( openmrsURL+"session")
                .method("GET", null)
                .addHeader("Authorization", "Basic " + authcode)
                .build();
        Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

        String json = sessionresponse.body().string();

        int x = 0;
        String pendingVlsSQL = "select \n" +
                "o.patient_id,\n" +
                "pii.identifier,\n" +
                "o.order_id,\n" +
                "o.encounter_id,\n" +
                "ob.uuid obs_uuid,\n" +
                "e.uuid ecounter_uuid ,\n" +
                "l.name,\n" +
                "Date(e.encounter_datetime) encounter_datetime ,\n" +
                "o.concept_id,\n" +
                "o.date_activated,\n" +
                "o.order_number,\n" +
                "e.encounter_type,\n" +
                "ob.value_numeric,\n" +
                "ob.comments,\n" +
                "ob.voided,\n" +
                "o.voided\n" +
                "from orders o\n" +
                "inner join encounter e on o.encounter_id=e.encounter_id and e.voided=0\n" +
                "inner join patient_identifier pii on pii.patient_id=o.patient_id and pii.identifier_type=28\n" +
                "inner join location l on e.location_id=l.location_id\n" +
                "left join obs ob on ob.person_id = o.patient_id and ob.order_id=o.order_id and ob.concept_id=856\n" +
                " where o.voided=0 and o.concept_id=856 and o.date_activated>='2024-05-01' and e.encounter_datetime >='2024-05-01'  and ob.value_numeric is null";

        Connection con = DriverManager.getConnection(amrsserver , username, password);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(pendingVlsSQL);
        rs.last();
        x = rs.getRow();
        rs.beforeFirst();
        while (rs.next()) {
            String pid = rs.getString(1);
            String obsdate = rs.getString("encounter_datetime");
            String encounter = rs.getString("ecounter_uuid");
            String order = rs.getString("order_number");
            String ccc =rs.getString("identifier");

            Connection conn = DriverManager.getConnection(amrsserver , username, password);

            String obsSql = "select uuid,obs_id,person_id,value_numeric,comments from obs where concept_id =856 and person_id='" + pid + "' and date(obs_datetime)='" + obsdate + "';";
            Statement obsStmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet obsRs = obsStmt.executeQuery(obsSql);
            obsRs.last();
            int y = obsRs.getRow();
            String obsuuid = "", comments = "", results = "";
            if (y > 0) {
                // obsRs.beforeFirst();
                obsuuid = obsRs.getString("uuid");
                comments = obsRs.getString("comments");
                results = obsRs.getString("value_numeric");

            }
            conn.close();

            System.out.println("Person_id " + pid + " encounter_id " + encounter + " Order " + order + " encounter_Date " + obsdate + " Obs_UUID " + obsuuid);
            String status="";
            int code=0;

            if(obsuuid==""||obsuuid.length()==0||obsuuid.isEmpty()){
                code=0;
                status="Pending  Results";
                System.out.println("No Results");
            }else {

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("encounter", encounter);
                jsonBody.put("order", order);

                String url = openmrsURL + "obs/" + obsuuid;
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Content-Type", "application/json")
                        // .addHeader("Cookie", (String) session.getAttribute("cookie"))
                        .build();
                Response response = client.newCall(request).execute();
                String bb = response.body().string();

                System.out.println(bb);
                code = response.code();
                System.out.println("Response Code Imesave " + code);
                if (code == 400) {
                    status = "Error Not Save";
                } else {
                    status = "Processed Successfully";
                }

            }
            java.util.Date nowDate = new Date();

            List<VlsOrders> vls = vlsOrdersService.findEncounterUUID(encounter);
            System.out.println("Systems VLs Size is "+ vls.size());
            if(vls.size()>0){
                VlsOrders vlsOrders = vls.get(0);
                vlsOrders.setPersonId(Integer.parseInt(pid));
                vlsOrders.setEncounterUuid(encounter);
                vlsOrders.setObsUuid(obsuuid);
                vlsOrders.setComments(comments);
                vlsOrders.setVlresult(results);
                vlsOrders.setOrderNumber(order);
                vlsOrders.setEcounterDate(obsdate);
                vlsOrders.setObsdate(obsdate);
                vlsOrders.setCcc(ccc);
                vlsOrders.setStatus(status);
                vlsOrders.setStatusCode(code);
                vlsOrders.setDateModified(nowDate);
                // vlsOrders.setDateProcossed(nowDate);
                try {
                    vlsOrdersService.save(vlsOrders);
                    System.out.println("Data Saved");
                } catch (Exception e) {
                    System.out.println("Error saving data: " + e.getMessage());
                    e.printStackTrace();
                }
                //  vlsOrdersService.save(vlsOrders);
            }else {
                VlsOrders vlsOrders = new VlsOrders();
                vlsOrders.setPersonId(Integer.parseInt(pid));
                vlsOrders.setEncounterUuid(encounter);
                vlsOrders.setObsUuid(obsuuid);
                vlsOrders.setComments(comments);
                vlsOrders.setVlresult(results);
                vlsOrders.setOrderNumber(order);
                vlsOrders.setEcounterDate(obsdate);
                vlsOrders.setObsdate(obsdate);
                vlsOrders.setCcc(ccc);
                vlsOrders.setStatus(status);
                vlsOrders.setStatusCode(code);
                vlsOrders.setDateProcossed(nowDate);
                try {
                    vlsOrdersService.save(vlsOrders);
                    System.out.println("Data Saved");
                } catch (Exception e) {
                    System.out.println("Error saving data: " + e.getMessage());
                    e.printStackTrace();
                }
                // vlsOrders.setDateModified(nowDate);
                // vlsOrdersService.save(vlsOrders);
                System.out.println("Data Saved");
            }

        }
        //return "IKo sawa Kabisa";
    }
    @Scheduled(cron = "0 */1 * ? * *")
    public void  FutureVlencounters() throws IOException, JSONException, SQLException {

        String originalInput = openmrslive_username+":"+openmrslive_password;
        String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());
        System.out.println(openmrsURL);
        OkHttpClient sessionclient = new OkHttpClient();
        Request sessionrequest = new Request.Builder()
                .url( openmrsURL+"session")
                .method("GET", null)
                .addHeader("Authorization", "Basic " + authcode)
                .build();
        Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

        String json = sessionresponse.body().string();

        int x = 0;
        String pendingVlsSQL = "SELECT \n" +
                "    o.person_id,\n" +
                "    o.ccc,\n" +
                "    o.order_number,\n" +
                "    o.ecounter_date,\n" +
                "    ob.obs_datetime,\n" +
                "    DATEDIFF(ob.obs_datetime, o.ecounter_date) AS tot,\n" +
                "    ob.value_numeric,\n" +
                "    ob.uuid obs_uuid,\n" +
                "    o.encounter_uuid,\n" +
                "    o.comments\n" +
                "FROM\n" +
                "    ampath_spot_live.vls_orders_new o\n" +
                "        LEFT JOIN\n" +
                "    amrs.obs ob ON ob.person_id = o.person_id\n" +
                "        AND ob.concept_id = 856\n" +
                "        AND ob.obs_datetime >= o.ecounter_date\n" +
                "WHERE\n" +
                "    obs_uuid = '' AND o.status_code != 200\n" +
                "        AND ob.voided = 0\n" +
                "        AND DATEDIFF(ob.obs_datetime, o.ecounter_date) <= 21";

        Connection con = DriverManager.getConnection(amrsserver , username, password);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(pendingVlsSQL);
        rs.last();
        x = rs.getRow();
        rs.beforeFirst();
        while (rs.next()) {
            String pid = rs.getString(1);
            String obsdate = rs.getString("obs_datetime");
            String encounter = rs.getString("encounter_uuid");
            String order = rs.getString("order_number");
            String ccc =rs.getString("ccc");
            String obsuuid = rs.getString("obs_uuid");
            String comments = rs.getString("comments");
            String results = rs.getString("value_numeric");

            // System.out.println("Person_id " + pid + " encounter_id " + encounter + " Order " + order + " encounter_Date " + obsdate + " Obs_UUID " + obsuuid);
            String status="";
            int code=0;

            if(obsuuid==""||obsuuid.length()==0||obsuuid.isEmpty()){
                code=0;
                status="Pending  Results";
                System.out.println("No Results");
            }else {

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("encounter", encounter);
                jsonBody.put("order", order);

                String url = openmrsURL + "obs/" + obsuuid;
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Content-Type", "application/json")
                        // .addHeader("Cookie", (String) session.getAttribute("cookie"))
                        .build();
                Response response = client.newCall(request).execute();
                String bb = response.body().string();

                System.out.println(bb);
                code = response.code();
                System.out.println("Response Code Imesave " + code);
                if (code == 400) {
                    status = "Error Not Save";
                } else {
                    status = "Processed Successfully";
                }

            }
            java.util.Date nowDate = new Date();

            List<VlsOrders> vls = vlsOrdersService.findEncounterUUID(encounter);
            System.out.println("Systems VLs Size is "+ vls.size());
            if(vls.size()>0){
                VlsOrders vlsOrders = vls.get(0);
                vlsOrders.setPersonId(Integer.parseInt(pid));
                vlsOrders.setEncounterUuid(encounter);
                vlsOrders.setObsUuid(obsuuid);
                vlsOrders.setComments(comments);
                vlsOrders.setVlresult(results);
                vlsOrders.setOrderNumber(order);
                vlsOrders.setEcounterDate(obsdate);
                vlsOrders.setObsdate(obsdate);
                vlsOrders.setCcc(ccc);
                vlsOrders.setStatus(status);
                vlsOrders.setStatusCode(code);
                vlsOrders.setDateModified(nowDate);
                // vlsOrders.setDateProcossed(nowDate);
                try {
                    vlsOrdersService.save(vlsOrders);
                    System.out.println("Data Saved");
                } catch (Exception e) {
                    System.out.println("Error saving data: " + e.getMessage());
                    e.printStackTrace();
                }
                //  vlsOrdersService.save(vlsOrders);
            }else {
                VlsOrders vlsOrders = new VlsOrders();
                vlsOrders.setPersonId(Integer.parseInt(pid));
                vlsOrders.setEncounterUuid(encounter);
                vlsOrders.setObsUuid(obsuuid);
                vlsOrders.setComments(comments);
                vlsOrders.setVlresult(results);
                vlsOrders.setOrderNumber(order);
                vlsOrders.setEcounterDate(obsdate);
                vlsOrders.setObsdate(obsdate);
                vlsOrders.setCcc(ccc);
                vlsOrders.setStatus(status);
                vlsOrders.setStatusCode(code);
                vlsOrders.setDateProcossed(nowDate);
                try {
                    vlsOrdersService.save(vlsOrders);
                    System.out.println("Data Saved");
                } catch (Exception e) {
                    System.out.println("Error saving data: " + e.getMessage());
                    e.printStackTrace();
                }
                // vlsOrders.setDateModified(nowDate);
                // vlsOrdersService.save(vlsOrders);
                System.out.println("Data Saved");
            }

        }
        // return "IKo sawa Kabisa";
    }
}
