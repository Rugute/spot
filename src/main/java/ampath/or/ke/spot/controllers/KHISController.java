package ampath.or.ke.spot.controllers;

import ampath.or.ke.spot.models.KHISMapping;
import ampath.or.ke.spot.models.MOHReports;
import ampath.or.ke.spot.services.FacilitiesService;
import ampath.or.ke.spot.services.KHISMappingService;
import ampath.or.ke.spot.services.MOHReportsService;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/khis")
public class KHISController {
    @Autowired
    public KHISMappingService khisMappingService;
    @Autowired
    public MOHReportsService mohReportsService;

    @Value("${app.dir}")
    public String filepath;

    @Value("${khis.url}")
    public String url;
    @Value("${khis.key}")
    public String auth;
    @RequestMapping(value = "/mapping", method = RequestMethod.GET)
    public ModelAndView mappings(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
             List<KHISMapping> mappingList = khisMappingService.getAllDataset();
             modelAndView.addObject("mappings", mappingList);

            modelAndView.setViewName("khis_mapping");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/dataset", method = RequestMethod.GET)
    public ModelAndView dataset(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<KHISMapping> mappingList = khisMappingService.getAllDataset();
            modelAndView.addObject("mappings", mappingList);

            modelAndView.setViewName("khis_mapping");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public ModelAndView reports(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<MOHReports> mappingList = mohReportsService.getAllDataset();
            modelAndView.addObject("reports", mappingList);

            modelAndView.setViewName("khis_reports");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ResponseEntity<Object> view(HttpSession session) throws IOException, JSONException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        JSONObject obj = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String DateCompleted= dateFormat.format(date);
        obj.put("dataSet", "ptIUGFkE6jn");
        obj.put("completeDate", DateCompleted);
        obj.put("period", "202407");
        obj.put("orgUnit", "EYME90yhk9j");
        //obj.put("attributeOptionCombo","KOoyurqK0Uf");
       String header=obj.toString();
       String finalDataset="";
       String finalHeader = header.substring(0, header.length() - 1);
       List<KHISMapping> khisMappingList = khisMappingService.getAllDataset();
       for(int f=0;f<khisMappingList.size();f++){
           String dataset="";
           JSONObject obj2 = new JSONObject();
           obj2.put("dataElement",khisMappingList.get(f).getDataElement());
          // obj2.put("categoryOptionCombo",khisMappingList.get(f).getCategoryCombo());
           obj2.put("value",f);
           dataset=obj2.toString();
           finalDataset =finalDataset+dataset+",";
       }
        String cleanDataset = finalDataset.substring(0, finalDataset.length() - 1);
       String  finaldatavalues =finalHeader+", \"dataValues\": [ "+cleanDataset+" ]}";

      /*  oString = jStringTwo.substring(0, jStringTwo.length() - 1);
       String concatString2=concatString.substring(0, concatString.length() - 1);
        Finaldatavalues =oString+", \"dataValues\": [ "+concatString2+" ]}";
        System.out.println(Finaldatavalues); */
        pushToAPI(finaldatavalues);
        return new ResponseEntity<Object>(finaldatavalues, HttpStatus.OK);
    }
    @RequestMapping(value = "/pull", method = RequestMethod.GET)
    public String pulldata() throws IOException, JSONException {
        String urll = "https://ngx.ampath.or.ke/etl-latest/etl/MOH-731-report?locationUuids=60cd8ba2-c4a5-44e5-b7cf-7cc3192756f6&startDate=2024-03-01T00:00:00+03:00&endDate=2024-03-31T23:59:59+03:00&reportName=MOH-731-report-2017&isAggregated=false";
        URL url = new URL(urll);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        String data = "";

        // Set Basic Authentication header
        String username = ""; // read for .env
        String password = ""; // read from .env
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeaderValue = "Basic " + new String(encodedAuth);
        http.setRequestProperty("Authorization", authHeaderValue);

        int statusCode = http.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            // Read the response body
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            data = response.toString();
            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(data);
            // Accessing the desired array using JSONObject and JSONArray
            JSONArray specificArray = jsonObject.getJSONObject("queriesAndSchemas").getJSONArray("results");
            data = specificArray.toString();

            // Print the response as a string
            System.out.println("Response: " + response.toString());
        } else {
            data = String.valueOf(statusCode);
            System.out.println("Failed to get response. Status code: " + statusCode);
        }
        return  data;
    }
     @RequestMapping(value = "/import", method = RequestMethod.GET)
        public ModelAndView umportdata(HttpSession session) throws IOException, JSONException {
            if (session.getAttribute("user") != null) {
                ModelAndView modelAndView = new ModelAndView();
                List<KHISMapping> mappingList = khisMappingService.getAllDataset();
                modelAndView.addObject("mappings", mappingList);
                Date nowDate = new Date();

                InputStream fis = new FileInputStream(filepath + "KHIS_elements.xlsx");
                Workbook workbook = null;
                Sheet sheet;
                try {
                    workbook = new XSSFWorkbook(fis);
                } catch (Exception ex) {
                    workbook = new HSSFWorkbook(fis);
                }
                for (int s = 0; s < workbook.getNumberOfSheets(); s++) {

                    sheet = workbook.getSheetAt(s);
                    int totalRowNum = sheet.getLastRowNum();
                    System.out.println("System "+totalRowNum);
                    for(int x=1;x<=totalRowNum;x++){
                        String name =  sheet.getRow(x).getCell(0).getStringCellValue();
                        String code =  sheet.getRow(x).getCell(1).getStringCellValue();
                        String du =  sheet.getRow(x).getCell(2).getStringCellValue();
                        String cu =  sheet.getRow(x).getCell(3).getStringCellValue();
//                        String version = String.valueOf(sheet.getRow(x).getCell(4).getStringCellValue());
                        System.out.println("Name "+name + " Version "+cu);

                        List<KHISMapping> khisMappingList = khisMappingService.getByDataElement(du);

                        if(khisMappingList.size()>0){

                        }else{

                            KHISMapping khisMapping = new KHISMapping();
                            khisMapping.setDataElement(du);
                            khisMapping.setCategoryCombo(cu);
                            khisMapping.setElementName(name);
                            khisMapping.setCreated_by(1);
                            khisMapping.setCode(code);
                            khisMapping.setStatus("Active");
                            khisMapping.setDateCreated(nowDate);

                            khisMappingService.save(khisMapping);
                        }
                    }
                }
                    modelAndView.setViewName("khis_mapping");
                    return modelAndView;
                } else {
                    return new ModelAndView("redirect:/auth/login");
                }

            }
    public void pushToAPI(String JsonString) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        int resposecode=0;
        String uri = url+"dataValueSets.json";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpGet = new HttpPost(uri);
        httpGet.setHeader("Authorization", "Basic " + auth);
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Connection", "close");
        StringEntity params = new StringEntity(JsonString);
        httpGet.setEntity(params);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        StatusLine statusLine = httpResponse.getStatusLine();
        System.out.println(statusLine.getStatusCode());
        resposecode = statusLine.getStatusCode();
        System.out.println(resposecode);
        System.out.println(httpResponse);
        System.out.println(url);

    }

}
