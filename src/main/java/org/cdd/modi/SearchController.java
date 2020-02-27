package org.cdd.modi;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

@RestController
@CrossOrigin(origins="*")
public class SearchController {
    private static String url = "http://www.omdbapi.com/?apikey=";
    private static String apiKey = null;

    static {
        String resource = "src/config/server.properties";
        Properties properties = new Properties();

        try {
            properties.load(new FileReader(resource));
            apiKey = properties.get("OMDB_API_KEY").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String request(String requestUrl) throws IOException {
        HttpURLConnection httpClient =
                (HttpURLConnection) new URL(requestUrl).openConnection();

        httpClient.setRequestMethod("GET");

        StringBuffer response = new StringBuffer();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return response.toString();
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam HashMap paramMap) throws IOException {
        StringBuilder requestUrlBuilder = new StringBuilder();
        requestUrlBuilder.append(url + apiKey);
        for(Object key : paramMap.keySet()) {
            requestUrlBuilder.append("&");
            requestUrlBuilder.append(key.toString());
            requestUrlBuilder.append("=");
            requestUrlBuilder.append(paramMap.get(key).toString());
        }
        return request(requestUrlBuilder.toString().replaceAll(" ", "%20"));
    }
}
