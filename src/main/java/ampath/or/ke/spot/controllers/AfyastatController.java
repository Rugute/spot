package ampath.or.ke.spot.controllers;

import ampath.or.ke.spot.models.AfyastatClientLineList;
import ampath.or.ke.spot.models.AfyastatErrors;
import ampath.or.ke.spot.services.AfyastatClientLineListService;
import ampath.or.ke.spot.services.AfyastatErrorsService;
import ampath.or.ke.spot.utils.AfyaStatData;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/afyastat")
public class AfyastatController {
    @Value("${spring.afyastat.username}")
    public  String username;
    @Value("${spring.afyastat.password}")
    public String password;
    @Value("${spring.afyastat.server}")
    public  String server;
    @Value("${spring.openmrs.url}")
    private String url;

    @Value("${spring.openmrs.username}")
    private String openmrs_username;
    @Value("${spring.openmrs.password}")
    private String openmrs_password;
    @Autowired
    private AfyastatClientLineListService afyastatClientLineListService;

    @Autowired
    private AfyastatErrorsService afyastatErrorsService;

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public void processencounters( ) throws IOException, JSONException, SQLException, ParseException {
        // if (session.getAttribute("user") != null) {
        //    ModelAndView modelAndView = new ModelAndView();
        String discripter = "";
        // HttpSession session = new ;
        String originalInput = openmrs_username+":"+openmrs_password;
        String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());

        OkHttpClient sessionclient = new OkHttpClient();
        Request sessionrequest = new Request.Builder()
                .url( url+"/session")
                .method("GET", null)
                .addHeader("Authorization", "Basic " + authcode)
                .build();
        Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

        String json = sessionresponse.body().string();
        // System.out.println("Session "+json);
      /*  JSONObject jsonObject = new JSONObject(json);
        String sessionID = (String) jsonObject.get("sessionId");
        String cookie="JSESSIONID="+sessionID; */
        // session.setAttribute("cookie", cookie);

        // List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getByDiscriminatorAndResponsecode("json-encounter","500");
        List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getByDiscriminatorAndResponsecode("json-encounter","500");
        int xxx = afyastatErrorsList.size();
        for (int y = 0; y < xxx; y++) {
            AfyastatErrors afyastatErrors = afyastatErrorsList.get(y);
            JSONObject jsonPayload = new JSONObject(afyastatErrors.getPayload());
            // String originalInput = openmrs_username + ":" + openmrs_password;
            // String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());

            if(afyastatErrors.getDiscriminator().equals("json-encounter")){
                String xx =   jsonPayload.getJSONObject("observation").toString();
                System.out.println(" Payload "+xx);
                JSONArray jsonObservations = new JSONArray();
                String edate="";
                String dtime = jsonPayload.getJSONObject("encounter").getString("encounter.encounter_datetime");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                edate =sdf2.format(sdf.parse(dtime));

                JSONObject jsonObs = new JSONObject(jsonPayload.getJSONObject("observation").toString());
                JSONObject obsJsonObject = (JSONObject) jsonObs;
                for (Iterator it = obsJsonObject.keys(); it.hasNext(); ) {
                    String conceptQuestion = String.valueOf(it.next());
                    Object valueObject = obsJsonObject.get(conceptQuestion);
                    String rquotes = conceptQuestion.substring(1, conceptQuestion.length() - 1);
                    String ovalues = valueObject.toString();
                    String occupationConAns = handleEditObsValues(rquotes.replace("^", "_"));
                    String[] ObsQuestion = occupationConAns.split("_");
                    String values="";
                    //  String puuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                    String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                    String puuid = getpersonuuid(cuuid);
                    String obvaluesAns = ovalues.replace("^", "_");
                    String[] ObsValues = obvaluesAns.split("_");

                    if(ObsValues.length>1){
                        values = ObsValues[1].toString();
                    }else{
                        values= ObsValues[0].toString();
                    }
                    if (ObsQuestion.length >0){
                        try{
                            int conceptId = Integer.parseInt(ObsQuestion[0]);
                            if(obvaluesAns.contains("{")){
                                //Obs Set
                                // System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue { } "+valueObject.toString() +"size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues iko na { } "+values ); //+" value "+ObsValues[1].toString()
                                JSONObject jsonObsSet = new JSONObject(obvaluesAns);
                                String parentuuid="";
                                JSONArray jsonchildObservation = new JSONArray();


                                for (Iterator itset = jsonObsSet.keys(); itset.hasNext(); ) {
                                    String conceptSetQuestion = String.valueOf(itset.next());
                                    // System.out.println("Set concempt "+ conceptSetQuestion);
                                    Object valueSetObject = jsonObsSet.get(conceptSetQuestion);
                                    String rsetquotes = conceptSetQuestion.substring(1, conceptSetQuestion.length() - 1);
                                    String obsSetConAns = handleEditObsValues(rsetquotes.replace("^", "_"));
                                    String[] ObsSetQuestion = obsSetConAns.split("_");
                                    String obsetvaluesAns = valueSetObject.toString();
                                    String[] ObsSetValues = obsetvaluesAns.split("_");
                                    String setvalues="";
                                    if(ObsSetValues.length>1){
                                        setvalues = ObsSetValues[1].toString();
                                    }else{
                                        setvalues= ObsSetValues[0].toString();
                                    }
                                    if (ObsSetQuestion.length >0){
                                        int conceptsetId = Integer.parseInt(ObsSetQuestion[0]);
                                        String conceptuuid =getconceptuuid(String.valueOf(conceptsetId))[0];
                                        String dtype =getconceptuuid(String.valueOf(conceptsetId))[1];
                                        parentuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                        //    System.out.println("SetParentkey delimiter : "+ conceptsetId +" SetParentvalue "+valueSetObject.toString() +" Setsize "+ObsSetValues.length+" SetobvaluesAns "+obsetvaluesAns +" Setfvalues "+setvalues );
                                        System.out.println("Parent concept "+conceptId +"SetParentkey delimiter : "+ conceptsetId +" Setfvalues "+setvalues +" dtype "+dtype);

                                        JSONObject jsonObservation = new JSONObject();
                                        jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                        jsonObservation.put("concept",conceptuuid);///String.valueOf(conceptsetId));

                                        String value ="";
                                        int Numericvalue=0;
                                        boolean boovalu;
                                        if(dtype.equals("2")){
                                            value =getconceptuuid(setvalues)[0];
                                            jsonObservation.put("value", value);
                                        }else if(dtype.equals("1")){
                                            if(values.equals("")){
                                                Numericvalue=0;
                                            }else{
                                                Numericvalue = Integer.parseInt(values);
                                            }

                                            jsonObservation.put("value", Numericvalue);
                                        }
                                        else if(dtype.equals("3") && values.equals("")){
                                            value="None";
                                            jsonObservation.put("value", value);
                                        }
                                        else{
                                            value = setvalues;
                                            jsonObservation.put("value", value);
                                        }
                                        //  JSONObject jsonObservation = new JSONObject();
                                        //  jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                        //  jsonObservation.put("concept",conceptuuid);///String.valueOf(conceptsetId));
                                        //  jsonObservation.put("value", value);
                                        jsonObservation.put("obsDatetime",edate );
                                        if(conceptuuid.equals("a8aac5fc-1350-11df-a1f1-0026b9348838")||conceptuuid.equals("a8aab45e-1350-11df-a1f1-0026b9348838")||conceptuuid.equals("a89ff438-1350-11df-a1f1-0026b9348838")|| conceptuuid.equals("a89ce69e-1350-11df-a1f1-0026b9348838")){

                                        }else {
                                            jsonchildObservation.put(jsonObservation);
                                        }

                                    }
                                }
                                // Add ObsGroping here
                                JSONObject jsonObservation = new JSONObject();
                                // jsonObservation.put("person","60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                jsonObservation.put("concept",parentuuid);///String.valueOf(conceptsetId));
                                jsonObservation.put("groupMembers", jsonchildObservation);
                                jsonObservations.put(jsonObservation);

                                //End of Obs Set
                            }else{
                                //System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue "+valueObject.toString() +" size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues "+values );
                                //    select uuid from concept where concept_id= 11838
                                String conceptuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                String dtype =getconceptuuid(String.valueOf(conceptId))[1];
                                System.out.println("Parent key delimiter : "+ conceptId +" fvalues "+values +" dtype "+dtype );

                                JSONObject jsonObservation = new JSONObject();
                                jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                jsonObservation.put("concept",conceptuuid);//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"

                                String value ="";
                                int Numericvalue=0;
                                Boolean boolenvalue;
                                if(dtype.equals("2")){
                                    value =getconceptuuid(values)[0];
                                    jsonObservation.put("value", value);

                                }
                                else if(dtype.equals("1")){
                                    if(values.equals("")){
                                        Numericvalue=0;
                                    }else{
                                        Numericvalue = Integer.parseInt(values);
                                    }

                                    jsonObservation.put("value", Numericvalue);
                                }
                                else if(dtype.equals("3") && values.equals("")){
                                    value="None";
                                    jsonObservation.put("value", value);

                                }else if(dtype.equals("10")){
                                    boolenvalue =Boolean.TRUE;
                                    if(values.equals("1")) {
                                        boolenvalue =Boolean.TRUE;
                                        value = getconceptuuid("1065")[0];
                                    }else{
                                        boolenvalue =Boolean.FALSE;
                                        value = getconceptuuid("1066")[0];
                                    }
                                    jsonObservation.put("value", boolenvalue);


                                }
                                else if(conceptuuid.equals("64807fb6-0ca3-4def-a62a-2d5f66c4328a") && values.contains("")){
                                    value =edate;
                                    jsonObservation.put("value", value);

                                }
                                else{
                                    value = values;
                                    jsonObservation.put("value", value);

                                }
                                // JSONObject jsonObservation = new JSONObject();
                                // jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                // jsonObservation.put("concept",conceptuuid);//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                // jsonObservation.put("value", value);
                                jsonObservation.put("obsDatetime",edate );//+"T06:08:25.000+0000"
                                if(conceptuuid.equals("a8aac5fc-1350-11df-a1f1-0026b9348838")||conceptuuid.equals("a8aab45e-1350-11df-a1f1-0026b9348838") || conceptuuid.equals("a89ff438-1350-11df-a1f1-0026b9348838")||conceptuuid.equals("a8a710ba-1350-11df-a1f1-0026b9348838")|| conceptuuid.equals("a8aafc70-1350-11df-a1f1-0026b9348838")|| conceptuuid.equals("a89ce69e-1350-11df-a1f1-0026b9348838")){

                                }else {
                                    jsonObservations.put(jsonObservation);
                                }

                            }
                        } catch (Exception ex){
                            // System.out.println("key delimiter NaN : "+ ex.getMessage() +" kdkdkdk "+ObsQuestion[0]);
                        }
                    }else{
                        //int conceptId = Integer.parseInt(ObsQuestion[0]);
                        //System.out.println("key delimiter : "+ conceptId );
                    }
                }
                //Encounter Details
                String euuid = jsonPayload.getJSONObject("encounter").getString("encounter.setup_config_uuid");
                String location = jsonPayload.getJSONObject("encounter").getString("encounter.location_id");
                String provider = jsonPayload.getJSONObject("encounter").getString("encounter.provider_id_select");
                String form = jsonPayload.getJSONObject("encounter").getString("encounter.form_uuid");
                String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                String puuid = getpersonuuid(cuuid);
                Connection con = DriverManager.getConnection(server, username, password);
                String luuid="";
                String pruuid="";

                int x = 0;
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery("select uuid from afyastat.location where location_id="+ location+"");
                rs.last();
                x = rs.getRow();
                rs.beforeFirst();
                while (rs.next()) {
                    luuid = rs.getString(1).toString();
                }
                ResultSet prs = stmt.executeQuery("select uuid from afyastat.provider where identifier="+ provider+"");
                prs.last();
                x = prs.getRow();
                prs.beforeFirst();
                while (prs.next()) {
                    pruuid = prs.getString(1).toString();
                }

                con.close();

                JSONObject jsonVisit = new JSONObject();
                jsonVisit.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                jsonVisit.put("visitType","9865c08a-97a4-4571-b873-dd422583b3a7");
                jsonVisit.put("startDatetime",edate); //+"T06:08:25.000+0000"
                jsonVisit.put("stopDatetime",edate); //+"T06:09:25.000+0000"


                JSONArray jsonProviders = new JSONArray();
                JSONObject jsonProvider = new JSONObject();
                jsonProvider.put("provider", pruuid);
                jsonProvider.put("encounterRole", "a0b03050-c99b-11e0-9572-0800200c9a66");
                jsonProviders.put(jsonProvider);

                JSONObject jsonEncounter = new JSONObject();
                jsonEncounter.put("encounterProviders",jsonProviders);
                jsonEncounter.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                jsonEncounter.put("encounterDatetime",edate); //+"T06:09:00.000+0000"
                jsonEncounter.put("encounterType",euuid);
                jsonEncounter.put("form",form);
                jsonEncounter.put("location",luuid);
                jsonEncounter.put("visit",jsonVisit);
                jsonEncounter.put("obs",jsonObservations);

                // System.out.println("Patient "+ puuid+" Encounter uuid "+euuid+" Etime "+ edate+" location "+luuid+" provider "+pruuid+" form "+form);
                //  System.out.println("Index "+ y + " Of "+xxx + " Payload "+ jsonEncounter.toString());
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonEncounter.toString());
                Request request = new Request.Builder()
                        .url(url + "/encounter")
                        .method("POST", body)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();

                String resBody = response.request().toString();
                int rescode = response.code();


                   /* OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType,jsonEncounter.toString());
                    Request request = new Request.Builder()
                            .url( url +"encounter")
                            .method("POST", body)
                            .addHeader("Authorization", "Basic " + authcode)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Cookie", cookie)// (String) session.getAttribute("cookie"))
                            .build();
                    Response response = client.newCall(request).execute(); */
                String bb = response.body().string();
                int code = response.code();
                String status ="";
                System.out.println("Response Code Imesave "+code);
                System.out.println("Payload "+jsonEncounter.toString());
                if(code==400){
                    status = "Record Exists";
                }else{
                    status = "Processed Successfully";
                }
                afyastatErrors.setStatus(status);
                afyastatErrors.setResponsecode(String.valueOf(code));
                afyastatErrorsService.save(afyastatErrors);

                System.out.println(bb);

            }


        }
        List<AfyastatErrors> afyastatErrorsListt = afyastatErrorsService.getErrors("json-encounter");
        // modelAndView.addObject("errors", afyastatErrorsListt);
        // modelAndView.setViewName("pm/afyastatnew");
        // return modelAndView;

    }

    @RequestMapping(value = "/all_errorqueue", method = RequestMethod.GET)
    public String encounters(HttpSession session) throws IOException, JSONException, SQLException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            System.out.println("url " + server + "pass " + username + "Password " + password);
            Connection con = DriverManager.getConnection(server, username, password);
            int x = 0;
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
    /*        ResultSet rs = stmt.executeQuery("SELECT e.id,e.uuid,e.location,provider,e.discriminator,data_source,payload,form_name,patient_uuid,form_data_uuid,m.message\n" +
                    " FROM afyastat.medic_error_data e\n" +
                    " inner join afyastat.medic_error_message m on e.id=m.medic_error_data_id\n" +
                    " where e.date_created>='2022-11-01' order by e.id desc");*/
            ResultSet rs = stmt.executeQuery("SELECT e.id,e.uuid,e.location,provider,e.discriminator,data_source,payload,form_name,patient_uuid,form_data_uuid,m.message\n" +
                    " FROM afyastat.medic_error_data e\n" +
                    " inner join afyastat.medic_error_message m on e.id=m.medic_error_data_id\n" +
                    " where e.date_created>='2024-01-01'  order by e.id desc");
            //and e.date_created<='2022-12-31' and e.location=28
            rs.last();
            x = rs.getRow();
            rs.beforeFirst();
            while (rs.next()) {
                AfyastatErrors afyastatErrors = afyastatErrorsService.getByID(Integer.parseInt(rs.getString(1)));
                if (afyastatErrors == null) {
                    AfyastatErrors ae = new AfyastatErrors();
                    ae.setId(Integer.valueOf(rs.getString(1)));
                    ae.setUuid(rs.getString(2));
                    ae.setLocation(rs.getString(3));
                    ae.setProvider(rs.getString(4));
                    ae.setDiscriminator(rs.getString(5));
                    ae.setDataSource(rs.getString(6));
                    ae.setPayload(rs.getString(7));
                    ae.setFormName(rs.getString(8));
                    ae.setPatientUuid(rs.getString(9));
                    ae.setFormDataUuid(rs.getString(10));
                    ae.setErrormessage(rs.getString(11));
                    afyastatErrorsService.save(ae);
                    System.out.println("test here");
                }

            }
            //List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getAllErrors();
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getPendingErrorsrrors();
            System.out.println("Size yake ndo hii " + afyastatErrorsList.size() );
            modelAndView.addObject("errors", afyastatErrorsList);
            modelAndView.setViewName("pm/afyastatnew");
            return "Done";
        } else {
            return "Login is required";// ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/list_errors", method = RequestMethod.GET)
    public ModelAndView list_errors(HttpSession session) throws IOException, JSONException, SQLException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getPendingErrorsrrors();
            System.out.println("Size yake ndo hii " + afyastatErrorsList.size() );
            modelAndView.addObject("errors", afyastatErrorsList);
            modelAndView.setViewName("pm/afyastatnew");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/regerrorqueue", method = RequestMethod.GET)
    public ModelAndView postss(HttpSession session) throws IOException, JSONException, SQLException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            System.out.println("url " + server + "pass " + username + "Password " + password);
            Connection con = DriverManager.getConnection(server, username, password);
            int x = 0;
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT e.id,e.uuid,e.location,provider,discriminator,data_source,payload,form_name,patient_uuid,form_data_uuid,m.message\n" +
                    " FROM afyastat.medic_error_data e\n" +
                    " inner join afyastat.medic_error_message m on e.id=m.medic_error_data_id\n" +
                    " where discriminator='json-registration' order by e.id desc ");
            rs.last();
            x = rs.getRow();
            rs.beforeFirst();
            while (rs.next()) {
                AfyastatErrors afyastatErrors = afyastatErrorsService.getByID(Integer.parseInt(rs.getString(1)));
                if (afyastatErrors == null) {
                    AfyastatErrors ae = new AfyastatErrors();
                    ae.setId(Integer.valueOf(rs.getString(1)));
                    ae.setUuid(rs.getString(2));
                    ae.setLocation(rs.getString(3));
                    ae.setProvider(rs.getString(4));
                    ae.setDiscriminator(rs.getString(5));
                    ae.setDataSource(rs.getString(6));
                    ae.setPayload(rs.getString(7));
                    ae.setFormName(rs.getString(8));
                    ae.setPatientUuid(rs.getString(9));
                    ae.setFormDataUuid(rs.getString(10));
                    ae.setErrormessage(rs.getString(11));
                    afyastatErrorsService.save(ae);
                }

            }
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getAllErrors();
            System.out.println("Size yake ndo hii " + afyastatErrorsList.size() + x);
            modelAndView.addObject("errors", afyastatErrorsList);
            modelAndView.setViewName("pm/afyastatnew");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }

    @RequestMapping(value = "/processs", method = RequestMethod.GET)
    public String checkuuid(HttpSession session) throws IOException, JSONException, SQLException {
        if (session.getAttribute("user") != null) {

            //List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getTop25Errors("json-registration");
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getErrors("json-registration");

            String originalInput = openmrs_username+":"+openmrs_password;
            String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());

            OkHttpClient sessionclient = new OkHttpClient();
            Request sessionrequest = new Request.Builder()
                    .url( url+"/session")
                    .method("GET", null)
                    .addHeader("Authorization", "Basic " + authcode)
                    .build();
            Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

            String json = sessionresponse.body().string();
            System.out.println("Session "+json);
            JSONObject jsonObject = new JSONObject(json);
            String sessionID = (String) jsonObject.get("sessionId");
            String cookie="JSESSIONID="+sessionID;
            session.setAttribute("cookie", cookie);

            for(int x=0;x<afyastatErrorsList.size();x++) {
                String puuid= afyastatErrorsList.get(x).getPatientUuid();
                System.out.println("Patient UUID "+puuid +"Session "+ sessionID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url( url+"/patient/"+puuid)
                        .method("GET", null)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Cookie", cookie)
                        .build();
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
/*
                int resposecode=0;
                String uri = url+"patient/"+puuid;
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpGet = new HttpPost(uri);
                httpGet.setHeader("Authorization", "Basic " + authcode);
                httpGet.setHeader("Content-Type", "application/json");
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Connection", "close");
               // StringEntity params = new StringEntity(JsonString);
               // httpGet.setEntity(params);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine statusLine = httpResponse.getStatusLine();
                System.out.println(statusLine.getStatusCode());
                resposecode = statusLine.getStatusCode();*/
                System.out.println("Ndo Hii"+puuid+" Response "+response.code()); //+"Json format "+jsonData

            }

              /*AfyastatErrors afyastatErrors = afyastatErrorsService.getByID(Integer.parseInt(rs.getString(1)));
                if(afyastatErrors==null){
                    AfyastatErrors ae = new AfyastatErrors();
                    ae.setId(Integer.valueOf(rs.getString(1)));
                    ae.setUuid(rs.getString(2));
                    ae.setLocation(rs.getString(3));
                    ae.setProvider(rs.getString(4));
                    ae.setDiscriminator(rs.getString(5));
                    ae.setDataSource(rs.getString(6));
                    ae.setPayload(rs.getString(7));
                    ae.setFormName(rs.getString(8));
                    ae.setPatientUuid(rs.getString(9));
                    ae.setFormDataUuid(rs.getString(10));
                    afyastatErrorsService.save(ae);
                }*/


           // modelAndView.addObject("errors", afyastatErrorsList);
           // modelAndView.setViewName("pm/afyastatnew");
            return "DOne";
        } else {
            return "Login is required";// new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/processreg", method = RequestMethod.GET)
    public ModelAndView processreg( HttpSession session) throws IOException, JSONException, SQLException, ParseException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            String discripter = "";
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getErrorsnullcode("json-registration");
            int xx = afyastatErrorsList.size();
            for(int y=0;y<xx;y++){
                AfyastatErrors afyastatErrors = afyastatErrorsList.get(y);
                JSONObject jsonPayload = new JSONObject(afyastatErrors.getPayload());
                String originalInput = openmrs_username+":"+openmrs_password;
                String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());
                if(afyastatErrors.getDiscriminator().equals("json-registration")) {
                    //Generate OpenMRS ID
                    OkHttpClient sessionclient = new OkHttpClient();
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("user", "1")
                            .build();
                    Request sessionrequest = new Request.Builder()
                            .url("http://10.50.80.44:8016/generateidentifier")
                            .method("POST",  formBody)
                            .build();
                    Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

                    String json = sessionresponse.body().string();
                    JSONObject jsonObject = new JSONObject(json);
                    String AMRSID =  (String) jsonObject.get("identifier");
                    System.out.println(json +" AMRSID "+ AMRSID);
                    //End Of OpenMRSID

                    //identifier
                    JSONArray Identifierarray = new JSONArray();
                    for (int x = 0; x < 2; x++) {
                        if (x == 0) {
                            JSONObject jsonIdentifier = new JSONObject();
                            jsonIdentifier.put("identifier", AMRSID);
                            jsonIdentifier.put("identifierType", "58a46e2e-1359-11df-a1f1-0026b9348838");
                            jsonIdentifier.put("location", "0900559c-1352-11df-a1f1-0026b9348838");
                            jsonIdentifier.put("preferred", "true");
                            Identifierarray.put(jsonIdentifier);
                        } else {
                            JSONObject jsonIdentifier = new JSONObject();
                            jsonIdentifier.put("identifier", jsonPayload.getJSONObject("patient").getString("patient.uuid"));
                            jsonIdentifier.put("identifierType", "c6552b22-f191-4557-a432-1f4df872d473");
                            jsonIdentifier.put("location", "0900559c-1352-11df-a1f1-0026b9348838");
                            jsonIdentifier.put("preferred", "false");
                            Identifierarray.put(jsonIdentifier);
                        }
                    }
                    // End of dentifier
                    JSONArray jsonAddressesArray = new JSONArray();
                    JSONObject jsonAddresses = new JSONObject();
                    jsonAddresses.put("address1", jsonPayload.getJSONObject("patient").getString("patient.county"));
                    jsonAddresses.put("address2", jsonPayload.getJSONObject("patient").getString("patient.sub_county"));
                    jsonAddresses.put("address3", jsonPayload.getJSONObject("patient").getString("patient.ward"));
                    jsonAddresses.put("address4", jsonPayload.getJSONObject("patient").getString("patient.village"));
                    jsonAddressesArray.put(jsonAddresses);

                    JSONObject jsonPerson = new JSONObject();
                    String startDateString = jsonPayload.getJSONObject("patient").getString("patient.birth_date");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    String dobestimates="";
                    try{
                        dobestimates = jsonPayload.getJSONObject("patient").getString("patient.birthdate_estimated");
                        if(dobestimates.isEmpty()){
                            dobestimates="true";
                        }else{
                            dobestimates=dobestimates;
                        }} catch (Exception e){
                        dobestimates="true";
                    }

                    jsonPerson.put("gender", jsonPayload.getJSONObject("patient").getString("patient.sex"));
                    jsonPerson.put("birthdate", sdf2.format(sdf.parse(startDateString)));
                    // jsonPerson.put("birthdateEstimated", jsonPayload.getJSONObject("patient").getString("patient.birthdate_estimated"));
                    jsonPerson.put("birthdateEstimated", dobestimates);
                    jsonPerson.put("dead", "false");
                    //Start of Names
                    JSONArray namearray = new JSONArray();
                    JSONObject jsonNames = new JSONObject();
                    jsonNames.put("givenName", jsonPayload.getJSONObject("patient").getString("patient.given_name"));
                    jsonNames.put("familyName", jsonPayload.getJSONObject("patient").getString("patient.family_name"));
                    jsonNames.put("middleName", jsonPayload.getJSONObject("patient").getString("patient.middle_name"));
                    namearray.put(jsonNames);
                    jsonPerson.put("names", namearray);
                    jsonPerson.put("addresses", jsonAddressesArray);
                    //End of Names
                    JSONObject patientObject = new JSONObject();
                    patientObject.put("identifiers", Identifierarray);
                    patientObject.put("person", jsonPerson);

                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, patientObject.toString());
                    Request request = new Request.Builder()
                            .url(url + "patient")
                            .method("POST", body)
                            .addHeader("Authorization", "Basic " + authcode)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Cookie", (String) session.getAttribute("cookie"))
                            .build();
                    Response response = client.newCall(request).execute();

                    System.out.println("Response ndo hii " + patientObject.toString());
                    System.out.println("Response ndo hii " + response.body().string());

                    int code = response.code();
                    String status ="";
                    System.out.println("Response Code "+code);
                    if(code==400){
                        status = "Patient Exists";
                    }else{
                        status = "Processed Successfully";
                    }
                    afyastatErrors.setStatus(status);
                    afyastatErrors.setResponsecode(String.valueOf(code));
                    afyastatErrorsService.save(afyastatErrors);
                }
            }
            List<AfyastatErrors> afyastatErrorsListt = afyastatErrorsService.getErrors("json-registration");
            modelAndView.addObject("errors", afyastatErrorsListt);
            modelAndView.setViewName("pm/afyastatnew");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }
    }


    @RequestMapping(value = "/processencounters", method = RequestMethod.GET)
    public ModelAndView processencounters( HttpSession session) throws IOException, JSONException, SQLException, ParseException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            String discripter = "";
            List<AfyastatErrors> afyastatErrorsList = afyastatErrorsService.getByDiscriminatorAndResponsecode("json-encounter","500");
            int xxx = afyastatErrorsList.size();
            for (int y = 0; y < xxx; y++) {
                AfyastatErrors afyastatErrors = afyastatErrorsList.get(y);
                JSONObject jsonPayload = new JSONObject(afyastatErrors.getPayload());
                String originalInput = openmrs_username + ":" + openmrs_password;
                String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());

                if(afyastatErrors.getDiscriminator().equals("json-encounter")){
                    String xx =   jsonPayload.getJSONObject("observation").toString();
                    System.out.println(" Payload "+xx);
                    JSONArray jsonObservations = new JSONArray();
                    String edate="";
                    String dtime = jsonPayload.getJSONObject("encounter").getString("encounter.encounter_datetime");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    edate =sdf2.format(sdf.parse(dtime));

                    JSONObject jsonObs = new JSONObject(jsonPayload.getJSONObject("observation").toString());
                    JSONObject obsJsonObject = (JSONObject) jsonObs;
                    for (Iterator it = obsJsonObject.keys(); it.hasNext(); ) {
                        String conceptQuestion = String.valueOf(it.next());
                        Object valueObject = obsJsonObject.get(conceptQuestion);
                        String rquotes = conceptQuestion.substring(1, conceptQuestion.length() - 1);
                        String ovalues = valueObject.toString();
                        String occupationConAns = handleEditObsValues(rquotes.replace("^", "_"));
                        String[] ObsQuestion = occupationConAns.split("_");
                        String values="";
                        //  String puuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                        String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                        String puuid = getpersonuuid(cuuid);
                        String obvaluesAns = ovalues.replace("^", "_");
                        String[] ObsValues = obvaluesAns.split("_");

                        if(ObsValues.length>1){
                            values = ObsValues[1].toString();
                        }else{
                            values= ObsValues[0].toString();
                        }
                        if (ObsQuestion.length >0){
                            try{
                                int conceptId = Integer.parseInt(ObsQuestion[0]);
                                if(obvaluesAns.contains("{")){
                                    //Obs Set
                                    // System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue { } "+valueObject.toString() +"size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues iko na { } "+values ); //+" value "+ObsValues[1].toString()
                                    JSONObject jsonObsSet = new JSONObject(obvaluesAns);
                                    String parentuuid="";
                                    JSONArray jsonchildObservation = new JSONArray();


                                    for (Iterator itset = jsonObsSet.keys(); itset.hasNext(); ) {
                                        String conceptSetQuestion = String.valueOf(itset.next());
                                        // System.out.println("Set concempt "+ conceptSetQuestion);
                                        Object valueSetObject = jsonObsSet.get(conceptSetQuestion);
                                        String rsetquotes = conceptSetQuestion.substring(1, conceptSetQuestion.length() - 1);
                                        String obsSetConAns = handleEditObsValues(rsetquotes.replace("^", "_"));
                                        String[] ObsSetQuestion = obsSetConAns.split("_");
                                        String obsetvaluesAns = valueSetObject.toString();
                                        String[] ObsSetValues = obsetvaluesAns.split("_");
                                        String setvalues="";
                                        if(ObsSetValues.length>1){
                                            setvalues = ObsSetValues[1].toString();
                                        }else{
                                            setvalues= ObsSetValues[0].toString();
                                        }
                                        if (ObsSetQuestion.length >0){
                                            int conceptsetId = Integer.parseInt(ObsSetQuestion[0]);
                                            String conceptuuid =getconceptuuid(String.valueOf(conceptsetId))[0];
                                            String dtype =getconceptuuid(String.valueOf(conceptsetId))[1];
                                            parentuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                            //    System.out.println("SetParentkey delimiter : "+ conceptsetId +" SetParentvalue "+valueSetObject.toString() +" Setsize "+ObsSetValues.length+" SetobvaluesAns "+obsetvaluesAns +" Setfvalues "+setvalues );
                                            System.out.println("Parent concept "+conceptId +"SetParentkey delimiter : "+ conceptsetId +" Setfvalues "+setvalues +" dtype "+dtype);

                                            String value ="";
                                            if(dtype.equals("2")){
                                                value =getconceptuuid(setvalues)[0];
                                            }else if(dtype.equals("3") && values.equals("")){
                                                value="None";
                                            }
                                            else{
                                                value = setvalues;
                                            }
                                            JSONObject jsonObservation = new JSONObject();
                                            jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                            jsonObservation.put("concept",conceptuuid);///String.valueOf(conceptsetId));
                                            jsonObservation.put("value", value);
                                            jsonObservation.put("obsDatetime",edate );
                                            jsonchildObservation.put(jsonObservation);

                                        }
                                    }
                                    // Add ObsGroping here
                                    JSONObject jsonObservation = new JSONObject();
                                    // jsonObservation.put("person","60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                    jsonObservation.put("concept",parentuuid);///String.valueOf(conceptsetId));
                                    jsonObservation.put("groupMembers", jsonchildObservation);
                                    jsonObservations.put(jsonObservation);

                                    //End of Obs Set
                                }else{
                                    //System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue "+valueObject.toString() +" size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues "+values );
                                    //    select uuid from concept where concept_id= 11838
                                    String conceptuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                    String dtype =getconceptuuid(String.valueOf(conceptId))[1];
                                    System.out.println("Parentkey delimiter : "+ conceptId +" fvalues "+values +" dtype "+dtype );

                                    String value ="";
                                    if(dtype.equals("2")){
                                        value =getconceptuuid(values)[0];
                                    }else if(dtype.equals("3") && values.equals("")){
                                        value="None";
                                    }else if(conceptuuid.equals("64807fb6-0ca3-4def-a62a-2d5f66c4328a") && values.contains("")){
                                        value =edate;
                                    }
                                    else{
                                        value = values;
                                    }
                                    JSONObject jsonObservation = new JSONObject();
                                    jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                    jsonObservation.put("concept",conceptuuid);//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                    jsonObservation.put("value", value);
                                    jsonObservation.put("obsDatetime",edate );//+"T06:08:25.000+0000"
                                    jsonObservations.put(jsonObservation);
                                }
                            } catch (Exception ex){
                                // System.out.println("key delimiter NaN : "+ ex.getMessage() +" kdkdkdk "+ObsQuestion[0]);
                            }
                        }else{
                            //int conceptId = Integer.parseInt(ObsQuestion[0]);
                            //System.out.println("key delimiter : "+ conceptId );
                        }
                    }
                    //Encounter Details
                    String euuid = jsonPayload.getJSONObject("encounter").getString("encounter.setup_config_uuid");
                    String location = jsonPayload.getJSONObject("encounter").getString("encounter.location_id");
                    String provider = jsonPayload.getJSONObject("encounter").getString("encounter.provider_id_select");
                    String form = jsonPayload.getJSONObject("encounter").getString("encounter.form_uuid");
                    String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                    String puuid = getpersonuuid(cuuid);
                    Connection con = DriverManager.getConnection(server, username, password);
                    String luuid="";
                    String pruuid="";

                    int x = 0;
                    Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = stmt.executeQuery("select uuid from afyastat.location where location_id="+ location+"");
                    rs.last();
                    x = rs.getRow();
                    rs.beforeFirst();
                    while (rs.next()) {
                        luuid = rs.getString(1).toString();
                    }
                    ResultSet prs = stmt.executeQuery("select uuid from afyastat.provider where identifier="+ provider+"");
                    prs.last();
                    x = prs.getRow();
                    prs.beforeFirst();
                    while (prs.next()) {
                        pruuid = prs.getString(1).toString();
                    }

                    con.close();

                    JSONObject jsonVisit = new JSONObject();
                    jsonVisit.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                    jsonVisit.put("visitType","9865c08a-97a4-4571-b873-dd422583b3a7");
                    jsonVisit.put("startDatetime",edate); //+"T06:08:25.000+0000"
                    jsonVisit.put("stopDatetime",edate); //+"T06:09:25.000+0000"


                    JSONArray jsonProviders = new JSONArray();
                    JSONObject jsonProvider = new JSONObject();
                    jsonProvider.put("provider", pruuid);
                    jsonProvider.put("encounterRole", "a0b03050-c99b-11e0-9572-0800200c9a66");
                    jsonProviders.put(jsonProvider);

                    JSONObject jsonEncounter = new JSONObject();
                    jsonEncounter.put("encounterProviders",jsonProviders);
                    jsonEncounter.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                    jsonEncounter.put("encounterDatetime",edate); //+"T06:09:00.000+0000"
                    jsonEncounter.put("encounterType",euuid);
                    jsonEncounter.put("form",form);
                    jsonEncounter.put("location",luuid);
                    jsonEncounter.put("visit",jsonVisit);
                    jsonEncounter.put("obs",jsonObservations);

                    // System.out.println("Patient "+ puuid+" Encounter uuid "+euuid+" Etime "+ edate+" location "+luuid+" provider "+pruuid+" form "+form);
                    System.out.println("Index "+ y + " Of "+xxx + " Payload "+ jsonEncounter.toString());

                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType,jsonEncounter.toString());
                    Request request = new Request.Builder()
                            .url( url +"encounter")
                            .method("POST", body)
                            .addHeader("Authorization", "Basic " + authcode)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Cookie",  (String) session.getAttribute("cookie"))
                            .build();
                    Response response = client.newCall(request).execute();
                    String bb = response.body().string();
                    int code = response.code();
                    String status ="";
                    System.out.println("Response Code Imesave "+code);
                    if(code==400){
                        status = "Patient Exists";
                    }else{
                        status = "Processed Successfully";
                    }
                    afyastatErrors.setStatus(status);
                    afyastatErrors.setResponsecode(String.valueOf(code));
                    afyastatErrorsService.save(afyastatErrors);

                    System.out.println(bb);

                }


            }
            List<AfyastatErrors> afyastatErrorsListt = afyastatErrorsService.getErrors("json-encounter");
            modelAndView.addObject("errors", afyastatErrorsListt);
            modelAndView.setViewName("pm/afyastatnew");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }
    }
    @RequestMapping(value = "/processregerror/{id}", method = RequestMethod.GET)
    public ModelAndView processregistartion(@PathVariable(name = "id") int id, HttpSession session) throws IOException, JSONException, SQLException, ParseException {
        if (session.getAttribute("user") != null) {

            String discripter ="";

            AfyastatErrors afyastatErrors = afyastatErrorsService.getByID(id);
            //String payload = afyastatErrors.getPayload();
            JSONObject jsonPayload = new JSONObject(afyastatErrors.getPayload());

            //https://talk.openmrs.org/t/how-to-create-a-person-using-rest-api/33678
            String originalInput = openmrs_username+":"+openmrs_password;
            String authcode = Base64.getEncoder().encodeToString(originalInput.getBytes());
            ModelAndView modelAndView = new ModelAndView();

            if(afyastatErrors.getDiscriminator().equals("json-registration")) {
                //Generate OpenMRS ID
                OkHttpClient sessionclient = new OkHttpClient();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("user", "1")
                        .build();
                Request sessionrequest = new Request.Builder()
                        .url("http://10.50.80.44:8016/generateidentifier")
                        .method("POST",  formBody)
                        .build();
                Response sessionresponse = sessionclient.newCall(sessionrequest).execute();

                String json = sessionresponse.body().string();
                JSONObject jsonObject = new JSONObject(json);
                String AMRSID =  (String) jsonObject.get("identifier");
                System.out.println(json +" AMRSID "+ AMRSID);
                //End Of OpenMRSID

                //identifier
                JSONArray Identifierarray = new JSONArray();
                for (int x = 0; x < 2; x++) {
                    if (x == 0) {
                        JSONObject jsonIdentifier = new JSONObject();
                        jsonIdentifier.put("identifier", AMRSID);
                        jsonIdentifier.put("identifierType", "58a46e2e-1359-11df-a1f1-0026b9348838");
                        jsonIdentifier.put("location", "0900559c-1352-11df-a1f1-0026b9348838");
                        jsonIdentifier.put("preferred", "true");
                        Identifierarray.put(jsonIdentifier);
                    } else {
                        JSONObject jsonIdentifier = new JSONObject();
                        jsonIdentifier.put("identifier", jsonPayload.getJSONObject("patient").getString("patient.uuid"));
                        jsonIdentifier.put("identifierType", "c6552b22-f191-4557-a432-1f4df872d473");
                        jsonIdentifier.put("location", "0900559c-1352-11df-a1f1-0026b9348838");
                        jsonIdentifier.put("preferred", "false");
                        Identifierarray.put(jsonIdentifier);
                    }
                }
                // End of dentifier
                JSONArray jsonAddressesArray = new JSONArray();
                JSONObject jsonAddresses = new JSONObject();
                jsonAddresses.put("address1", jsonPayload.getJSONObject("patient").getString("patient.county"));
                jsonAddresses.put("address2", jsonPayload.getJSONObject("patient").getString("patient.sub_county"));
                jsonAddresses.put("address3", jsonPayload.getJSONObject("patient").getString("patient.ward"));
                jsonAddresses.put("address4", jsonPayload.getJSONObject("patient").getString("patient.village"));
                jsonAddressesArray.put(jsonAddresses);

                JSONObject jsonPerson = new JSONObject();
                String startDateString = jsonPayload.getJSONObject("patient").getString("patient.birth_date");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String dobestimates="";
                try{
                    dobestimates = jsonPayload.getJSONObject("patient").getString("patient.birthdate_estimated");
                    if(dobestimates.isEmpty()){
                        dobestimates="true";
                    }else{
                        dobestimates=dobestimates;
                    }} catch (Exception e){
                    dobestimates="true";
                }

                jsonPerson.put("gender", jsonPayload.getJSONObject("patient").getString("patient.sex"));
                jsonPerson.put("birthdate", sdf2.format(sdf.parse(startDateString)));
                // jsonPerson.put("birthdateEstimated", jsonPayload.getJSONObject("patient").getString("patient.birthdate_estimated"));
                jsonPerson.put("birthdateEstimated", dobestimates);
                jsonPerson.put("dead", "false");
                //Start of Names
                JSONArray namearray = new JSONArray();
                JSONObject jsonNames = new JSONObject();
                jsonNames.put("givenName", jsonPayload.getJSONObject("patient").getString("patient.given_name"));
                jsonNames.put("familyName", jsonPayload.getJSONObject("patient").getString("patient.family_name"));
                jsonNames.put("middleName", jsonPayload.getJSONObject("patient").getString("patient.middle_name"));
                namearray.put(jsonNames);
                jsonPerson.put("names", namearray);
                jsonPerson.put("addresses", jsonAddressesArray);
                //End of Names
                JSONObject patientObject = new JSONObject();
                patientObject.put("identifiers", Identifierarray);
                patientObject.put("person", jsonPerson);

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, patientObject.toString());
                Request request = new Request.Builder()
                        .url(url + "patient")
                        .method("POST", body)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie", (String) session.getAttribute("cookie"))
                        .build();
                Response response = client.newCall(request).execute();

                //System.out.println("Payload "+payload);
                System.out.println("Response ndo hii " + patientObject.toString());
                System.out.println("Response ndo hii " + response.body().string());

                int code = response.code();
                String status ="";
                System.out.println("Response Code "+code);
                if(code==400){
                    status = "Patient Exists";
                }else{
                    status = "Processed Successfully";
                }
                afyastatErrors.setStatus(status);
                afyastatErrors.setResponsecode(String.valueOf(code));
                afyastatErrorsService.save(afyastatErrors);

            }
            else if(afyastatErrors.getDiscriminator().equals("json-encounter")){
                String xx =   jsonPayload.getJSONObject("observation").toString();
                System.out.println(" Payload "+xx);
                JSONArray jsonObservations = new JSONArray();
                String edate="";
                String dtime = jsonPayload.getJSONObject("encounter").getString("encounter.encounter_datetime");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                edate =sdf2.format(sdf.parse(dtime));

                JSONObject jsonObs = new JSONObject(jsonPayload.getJSONObject("observation").toString());
                JSONObject obsJsonObject = (JSONObject) jsonObs;
                for (Iterator it = obsJsonObject.keys(); it.hasNext(); ) {
                    String conceptQuestion = String.valueOf(it.next());
                    Object valueObject = obsJsonObject.get(conceptQuestion);
                    String rquotes = conceptQuestion.substring(1, conceptQuestion.length() - 1);
                    String ovalues = valueObject.toString();
                    String occupationConAns = handleEditObsValues(rquotes.replace("^", "_"));
                    String[] ObsQuestion = occupationConAns.split("_");
                    String values="";
                    //  String puuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                    String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                    String puuid = getpersonuuid(cuuid);
                    String obvaluesAns = ovalues.replace("^", "_");
                    String[] ObsValues = obvaluesAns.split("_");

                    if(ObsValues.length>1){
                        values = ObsValues[1].toString();
                    }else{
                        values= ObsValues[0].toString();
                    }
                    if (ObsQuestion.length >0){
                        try{
                            int conceptId = Integer.parseInt(ObsQuestion[0]);
                            if(obvaluesAns.contains("{")){
                                //Obs Set
                                // System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue { } "+valueObject.toString() +"size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues iko na { } "+values ); //+" value "+ObsValues[1].toString()
                                JSONObject jsonObsSet = new JSONObject(obvaluesAns);
                                String parentuuid="";
                                JSONArray jsonchildObservation = new JSONArray();


                                for (Iterator itset = jsonObsSet.keys(); itset.hasNext(); ) {
                                    String conceptSetQuestion = String.valueOf(itset.next());
                                    // System.out.println("Set concempt "+ conceptSetQuestion);
                                    Object valueSetObject = jsonObsSet.get(conceptSetQuestion);
                                    String rsetquotes = conceptSetQuestion.substring(1, conceptSetQuestion.length() - 1);
                                    String obsSetConAns = handleEditObsValues(rsetquotes.replace("^", "_"));
                                    String[] ObsSetQuestion = obsSetConAns.split("_");
                                    String obsetvaluesAns = valueSetObject.toString();
                                    String[] ObsSetValues = obsetvaluesAns.split("_");
                                    String setvalues="";
                                    if(ObsSetValues.length>1){
                                        setvalues = ObsSetValues[1].toString();
                                    }else{
                                        setvalues= ObsSetValues[0].toString();
                                    }
                                    if (ObsSetQuestion.length >0){
                                        int conceptsetId = Integer.parseInt(ObsSetQuestion[0]);
                                        String conceptuuid =getconceptuuid(String.valueOf(conceptsetId))[0];
                                        String dtype =getconceptuuid(String.valueOf(conceptsetId))[1];
                                        parentuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                        //    System.out.println("SetParentkey delimiter : "+ conceptsetId +" SetParentvalue "+valueSetObject.toString() +" Setsize "+ObsSetValues.length+" SetobvaluesAns "+obsetvaluesAns +" Setfvalues "+setvalues );
                                        System.out.println("Parent concept "+conceptId +"SetParentkey delimiter : "+ conceptsetId +" Setfvalues "+setvalues +" dtype "+dtype);

                                        String value ="";
                                        if(dtype.equals("2")){
                                            value =getconceptuuid(setvalues)[0];
                                        }else if(dtype.equals("3") && values.equals("")){
                                            value="None";
                                        }
                                        else{
                                            value = setvalues;
                                        }
                                        JSONObject jsonObservation = new JSONObject();
                                        jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                        jsonObservation.put("concept",conceptuuid);///String.valueOf(conceptsetId));
                                        jsonObservation.put("value", value);
                                        jsonObservation.put("obsDatetime",edate );
                                        jsonchildObservation.put(jsonObservation);

                                    }
                                }
                                // Add ObsGroping here
                                JSONObject jsonObservation = new JSONObject();
                                // jsonObservation.put("person","60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                jsonObservation.put("concept",parentuuid);///String.valueOf(conceptsetId));
                                jsonObservation.put("groupMembers", jsonchildObservation);
                                jsonObservations.put(jsonObservation);

                                //End of Obs Set
                            }else{
                                //System.out.println("Parentkey delimiter : "+ conceptId +" Parentvalue "+valueObject.toString() +" size "+ObsValues.length+" obvaluesAns "+obvaluesAns +" fvalues "+values );
                                //  System.out.println("Parentkey delimiter : "+ conceptId +" fvalues "+values );
                                //    select uuid from concept where concept_id= 11838
                                String conceptuuid =getconceptuuid(String.valueOf(conceptId))[0];
                                String dtype =getconceptuuid(String.valueOf(conceptId))[1];
                                System.out.println("Parentkey delimiter : "+ conceptId +" fvalues "+values +" dtype "+dtype );

                                String value ="";
                                if(dtype.equals("2")){
                                    value =getconceptuuid(values)[0];
                                }else if(dtype.equals("3") && values.equals("")){
                                    value="None";
                                }else if(conceptuuid.equals("64807fb6-0ca3-4def-a62a-2d5f66c4328a") && values.contains("")){
                                    value =edate;
                                }
                                else{
                                    value = values;
                                }
                                JSONObject jsonObservation = new JSONObject();
                                jsonObservation.put("person",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                                jsonObservation.put("concept",conceptuuid);//String.valueOf(conceptId));//+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                                jsonObservation.put("value", value);
                                jsonObservation.put("obsDatetime",edate );//+"T06:08:25.000+0000"
                                jsonObservations.put(jsonObservation);
                                /*OkHttpClient client = new OkHttpClient();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType,jsonObservation.toString());
                                Request request = new Request.Builder()
                                        .url(url+"obs")
                                        .method("POST", body)
                                        .addHeader("Authorization", "Basic " + authcode)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Cookie",  (String) session.getAttribute("cookie"))
                                        .build();
                                Response response = client.newCall(request).execute();
                                System.out.println(" Rugute Response "+ jsonObservations.toString());
                                System.out.println(" Rugute Response "+ response.body().string());*/
                            }
                        } catch (Exception ex){
                            // System.out.println("key delimiter NaN : "+ ex.getMessage() +" kdkdkdk "+ObsQuestion[0]);
                        }
                    }else{
                        //int conceptId = Integer.parseInt(ObsQuestion[0]);
                        //System.out.println("key delimiter : "+ conceptId );
                    }
                }
                //Encounter Details
                String euuid = jsonPayload.getJSONObject("encounter").getString("encounter.setup_config_uuid");
                String location = jsonPayload.getJSONObject("encounter").getString("encounter.location_id");
                String provider = jsonPayload.getJSONObject("encounter").getString("encounter.provider_id_select");
                String form = jsonPayload.getJSONObject("encounter").getString("encounter.form_uuid");
                String cuuid = jsonPayload.getJSONObject("patient").getString("patient.uuid");
                String puuid = getpersonuuid(cuuid);
                Connection con = DriverManager.getConnection(server, username, password);
                String luuid="";
                String pruuid="";

                int x = 0;
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery("select uuid from location where location_id="+ location+"");
                rs.last();
                x = rs.getRow();
                rs.beforeFirst();
                while (rs.next()) {
                    luuid = rs.getString(1).toString();
                }
                ResultSet prs = stmt.executeQuery("select uuid from provider where identifier="+ provider+"");
                prs.last();
                x = prs.getRow();
                prs.beforeFirst();
                while (prs.next()) {
                    pruuid = prs.getString(1).toString();
                }

                con.close();

                JSONObject jsonVisit = new JSONObject();
                jsonVisit.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                jsonVisit.put("visitType","9865c08a-97a4-4571-b873-dd422583b3a7");
                jsonVisit.put("startDatetime",edate); //+"T06:08:25.000+0000"
                jsonVisit.put("stopDatetime",edate); //+"T06:09:25.000+0000"


                JSONArray jsonProviders = new JSONArray();
                JSONObject jsonProvider = new JSONObject();
                jsonProvider.put("provider", pruuid);
                jsonProvider.put("encounterRole", "a0b03050-c99b-11e0-9572-0800200c9a66");
                jsonProviders.put(jsonProvider);

                JSONObject jsonEncounter = new JSONObject();
                jsonEncounter.put("encounterProviders",jsonProviders);
                jsonEncounter.put("patient",puuid);//"60168b73-60f1-4044-9dc6-84fdcbc1962c");
                jsonEncounter.put("encounterDatetime",edate); //+"T06:09:00.000+0000"
                jsonEncounter.put("encounterType",euuid);
                jsonEncounter.put("form",form);
                jsonEncounter.put("location",luuid);
                jsonEncounter.put("visit",jsonVisit);
                jsonEncounter.put("obs",jsonObservations);

                // System.out.println("Patient "+ puuid+" Encounter uuid "+euuid+" Etime "+ edate+" location "+luuid+" provider "+pruuid+" form "+form);
                System.out.println("Payload "+ jsonEncounter.toString());

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType,jsonEncounter.toString());
                Request request = new Request.Builder()
                        .url( url +"encounter")
                        .method("POST", body)
                        .addHeader("Authorization", "Basic " + authcode)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie",  (String) session.getAttribute("cookie"))
                        .build();
                Response response = client.newCall(request).execute();
                System.out.println(response.body().string());
                int code = response.code();
                String status ="";
                System.out.println("Response Code "+code);
                if(code==400){
                    status = "Patient Exists";
                }else{
                    status = "Processed Successfully";
                }
                afyastatErrors.setStatus(status);
                afyastatErrors.setResponsecode(String.valueOf(code));
                afyastatErrorsService.save(afyastatErrors);

            }

            modelAndView.setViewName("pm/afyastatnew");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    public String[] getconceptuuid(String conceptId) throws SQLException {
        String conceptuuid="";
        String dtype="";
        Connection con = DriverManager.getConnection(server, username, password);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ResultSet rss = stmt.executeQuery("select c.concept_id,c.datatype_id,c.uuid,cd.name from afyastat.concept c\n" +
                "inner join afyastat.concept_datatype cd on c.datatype_id=cd.concept_datatype_id \n" +
                " where c.concept_id in ("+ conceptId +");");
        rss.last();
        rss.beforeFirst();
        while (rss.next()) {
            conceptuuid = rss.getString(3).toString();
            dtype = rss.getString(2).toString();
        }
        if(conceptuuid.equals("")) {

            ResultSet rs = stmt.executeQuery("select c.concept_id,c.datatype_id,c.uuid,cr.code,cd.name from afyastat.concept c \n" +
                    "inner join afyastat.concept_reference_map cm  on c.concept_id=cm.concept_id\n" +
                    "inner join afyastat.concept_reference_term cr on cr.concept_reference_term_id=cm.concept_reference_term_id\n" +
                    "inner join afyastat.concept_datatype cd on c.datatype_id=cd.concept_datatype_id \n" +
                    "where cr.code in ("+ conceptId +"); ");
            rs.last();
            rs.beforeFirst();
            while (rs.next()) {
                conceptuuid = rs.getString(3).toString();
                dtype = rs.getString(2).toString();
            }
        }
        con.close();

        return new String[] { conceptuuid, dtype };
    }

    public String getpersonuuid(String chtuuid) throws SQLException {
        String puuid="";

        Connection con = DriverManager.getConnection(server, username, password);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ResultSet rss = stmt.executeQuery("select p.uuid from afyastat.patient_identifier pii\n" +
                "    inner join afyastat.person p on pii.patient_id=p.person_id\n" +
                "    where pii.identifier='"+ chtuuid +"';");
        rss.last();
        rss.beforeFirst();
        while (rss.next()) {
            puuid = rss.getString(1).toString();
        }
        con.close();
        return puuid;
    }
    private String handleEditObsValues(String obsValue) {

        ArrayNode arrNodeValues = JsonNodeFactory.instance.arrayNode();
        String conceptValue = null;
        if (obsValue != null && !obsValue.equalsIgnoreCase("")) {
            for (String s : obsValue.split("_")) {
                arrNodeValues.add(s);
            }
            if (arrNodeValues != null) {
                conceptValue = (arrNodeValues.get(0).textValue());
            }
        }
        return conceptValue;
    }
    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public ModelAndView processregistartion( HttpSession session) throws IOException, JSONException, SQLException, ParseException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("afyastatlinelist");
           // try {

                AfyaStatData.pullAllHTSFromDatabase(server, username, password, afyastatClientLineListService);
         //   }catch (Exception e){

           // }
            // List<AfyastatClientLineList> afyastatClientLineListList =  afyastatClientLineListService.getAllTests();
            List<AfyastatClientLineList> afyastatClientLineListList =  afyastatClientLineListService.getFirst1000Tests();


            modelAndView.addObject("htslinelist",afyastatClientLineListList);

            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }
    }

}

