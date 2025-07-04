package me.itsnathang.placeholders;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class LocationInfo {
    private JSONObject data;

    LocationInfo(InetSocketAddress address) {
        String ip = address.getAddress().getHostAddress();

        try {
            JSONObject json = getJSON(ip);

            // ip-api returns {"status":"success", ...} when the lookup succeeds
            if (json != null && "success".equalsIgnoreCase((String) json.get("status")))
                this.data = json;
            else
                this.data = null;
        } catch (Exception e) {
            this.data = null;
        }
    }

    private JSONObject getJSON(String ip) throws Exception {
        StringBuilder response = new StringBuilder();

        URL url = new URL("http://ip-api.com/json/" + ip);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;

        while ((line = rd.readLine()) != null) {
            response.append(line);
        }

        rd.close();

        return (JSONObject) new JSONParser().parse(response.toString());
    }

    public String getData(String key) {
        if (data == null) return "API Down";

        // ip-api keys are case sensitive while PlaceholderAPI provides
        // identifiers in lower case, so map the requested identifier to the
        // correct JSON key
        String actualKey = mapKey(key);

        return data.containsKey(actualKey)
                ? data.get(actualKey).toString()
                : "Invalid Identifier";
    }

    private String mapKey(String key) {
        switch (key.toLowerCase()) {
            case "countrycode":
                return "countryCode";
            case "regionname":
                return "regionName";
            default:
                // most keys from ip-api are already lower case
                return key.toLowerCase();
        }
    }

    public boolean isValid() {
        return data != null;
    }
}
