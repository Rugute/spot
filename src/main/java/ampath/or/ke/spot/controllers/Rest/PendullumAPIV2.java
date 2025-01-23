package ampath.or.ke.spot.controllers.Rest;

import ampath.or.ke.spot.models.PendullumData;
import ampath.or.ke.spot.models.PendulumRiskScores;
import ampath.or.ke.spot.repositories.PendullumDataRepositories;
import ampath.or.ke.spot.repositories.PendullumReporitory;
import ampath.or.ke.spot.services.PendullumService;
import ampath.or.ke.spot.services.PendulumRiskScoreService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static ampath.or.ke.spot.utils.GlobalVars._toJson;

@RestController
@RequestMapping("/rest/api/v2/pendulum")
public class PendullumAPIV2 {
    @Autowired
    private PendullumService pendullumService;

    @Autowired
    private PendullumReporitory pendullumReporitory;
    @Autowired
    private PendullumDataRepositories pendullumDataRepositories;
    @Autowired
    private PendulumRiskScoreService pendulumRiskScoreService;

    @GetMapping("/dataset")
    public ResponseEntity<Page<PendullumData>> PendullumData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PendullumData> data = pendullumDataRepositories.findAll(pageable);

        return ResponseEntity.ok(data);
    }
    @GetMapping("/enrolled/{enrolled}")
    public ResponseEntity<Page<PendullumData>> PendullumDataEnrolled(@PathVariable int enrolled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PendullumData> data = pendullumDataRepositories.findByprojectbeyondEnrolled(enrolled,pageable);

        return ResponseEntity.ok(data);
    }
    @GetMapping("/patient/{pid}")
    public ResponseEntity<Page<PendullumData>> PendullumPIDDataEnrolled(@PathVariable String pid,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PendullumData> data = pendullumDataRepositories.findBypatientIdentifier(pid,pageable);

        return ResponseEntity.ok(data);
    }
    @GetMapping("/encounter_date/{startdate}/{enddate}")
    public ResponseEntity<Page<PendullumData>> PendullumDncounter_dateDataEnrolled(@PathVariable String startdate,
                                                                                   @PathVariable String enddate,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PendullumData> data = pendullumDataRepositories.getBystartandendDate(startdate,enddate,pageable);

        return ResponseEntity.ok(data);
    }
    @GetMapping("/dataset/encounter_date")
    public ResponseEntity<Page<PendullumData>> PendullumDataByencounter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam  String encounterDate) {

         //Remove the boolen in the app here
        Pageable pageable = PageRequest.of(page, size);
        Page<?> data = pendullumDataRepositories.getAllsummaries(encounterDate,pageable);

        return ResponseEntity.ok((Page<PendullumData>) data);
    }
    @GetMapping("/alldata")
    public ResponseEntity<Page<?>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<?> data = pendullumReporitory.getAllsummaries(pageable);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/records")
    public List<Tuple> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        return pendullumService.getRecordsWithPagination(page, size);
    }

    /*
    @GetMapping("/clients")
    public ResponseEntity<List<Tuple>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        // Calculate the starting index of the page
        int start = page * size;

        // Retrieve the entire dataset
        List<Tuple> allData = pendullumService.getAllDataset();

        // Check if the starting index is out of bounds
        if (start >= allData.size()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Calculate the ending index of the page
        int end = Math.min(start + size, allData.size());

        // Get the sublist corresponding to the current page
        List<Tuple> users = allData.subList(start, end);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    */

    @ResponseBody
    @RequestMapping(value="/datasettt", method = RequestMethod.GET,produces = "application/json")//, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String HTSlist(HttpSession session) throws Exception {
        JSONArray jsonArray = new JSONArray();
        String json="";
       // if (session.getAttribute("company") != null && session.getAttribute("user") != null) {
            String output = "";
            Date nowDate = new Date();
            String pattern = "yyyy-MM-dd hh:mm:ss";
            String jssons = "";
            List<Tuple> kashaClientsList = pendullumService.getAllDataset();
            List<ObjectNode> jsonn = _toJson(kashaClientsList);
            jssons = jsonn.toString();

           /* try {
                FileWriter file = new FileWriter("F:\myjson.json");
                file.write(jsonObject3.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            // String jstring =jssons.substring(1);
            // String fstring = "["+jstring+"]";
            // System.out.println("Client Line size "+ fstring);
            json =  jssons;
       /* }
        else {
            JsonObject jsonn = new JsonObject();
            jsonn.addProperty("error", "Authetications is Required");
            jsonArray.put(jsonn);
            json=jsonArray.toString();

        } */
        return json;
    }

   // @PostMapping("/riskScores")
    @ResponseBody
    @RequestMapping(value="/riskscores", method = RequestMethod.POST,produces = "application/json")//, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String postRiskScores(@RequestBody String requestBody) {
        // Here you can process the incoming risk score data
        // For this example, let's just print it
        System.out.println("Received risk score: " + requestBody.toString());
        PendulumRiskScores pendulumRiskScores = new PendulumRiskScores();
        pendulumRiskScores.setRisksmg(requestBody.toString());
        pendulumRiskScoreService.save(pendulumRiskScores);
        // You can perform further processing or return a response
        return "Risk score received successfully!";
    }
}

