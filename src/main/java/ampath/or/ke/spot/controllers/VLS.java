package ampath.or.ke.spot.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

@Controller
@Transactional
@RequestMapping("/vls_New")
public class VLS {

    @RequestMapping(value = "/CleanAMRSVLsOrders", method = RequestMethod.GET)
    public String encounters(HttpSession session) throws IOException, JSONException, SQLException {
        {
            String OPENMRS_URL = "https://ngx.ampath.or.ke/amrs/ws/rest/v1/obs";
            String USERNAME = "erugut";
            String PASSWORD = "nNoel@2019";

            try {
                // Create a URL object
                URL url = new URL(OPENMRS_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                // Basic Authentication
                String auth = USERNAME + ":" + PASSWORD;
                String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

                // Enable output for the connection
                conn.setDoOutput(true);

                // Create JSON object with patient data
                JSONObject patientData = new JSONObject();
                patientData.put("encounter", "18a285a4-900e-4a6d-bb1c-6e6fd4110c96");
                patientData.put("encounter", "ORD-597651");
                patientData.put("value", "0");


                // Write JSON data to the connection output stream
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = patientData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response code
                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);
                System.out.println("Response "+ conn.getResponseMessage());

                // Handle the response
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("Patient created successfully.");
                } else {
                    System.out.println("Failed to create patient.");
                }

                // Close the connection
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "IKo sawa Kabisa";
    }
}
