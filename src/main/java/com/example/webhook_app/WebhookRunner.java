package com.example.webhook_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookRunner implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting webhook process...");


        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Keshika Swetha D");
        requestBody.put("regNo", "22BLC1184");
        requestBody.put("email", "keshikaswetha.d2022@vitstudent.ac.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    generateWebhookUrl, request, ApiResponse.class);

            ApiResponse apiResponse = response.getBody();
            System.out.println("Webhook URL received: " + apiResponse.getWebhook());
            System.out.println("Access Token received: " + apiResponse.getAccessToken());


            String sqlQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                    "(SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e1.DEPARTMENT AND e2.DOB > e1.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID ORDER BY e1.EMP_ID DESC;";

            System.out.println("SQL Solution: " + sqlQuery);


            String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

            Map<String, String> solutionBody = new HashMap<>();
            solutionBody.put("finalQuery", sqlQuery);

            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            submitHeaders.set("Authorization", apiResponse.getAccessToken());

            HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(solutionBody, submitHeaders);
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(
                    submitUrl, submitRequest, String.class);

            System.out.println("Solution submitted! Response: " + submitResponse.getBody());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

