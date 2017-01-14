package urlshortener.common.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 14/01/17.
 */
@Service
public class MetricsViewConfig {

    HashMap<String, String> metrics;
    HashMap<String, Boolean> flags;

    public MetricsViewConfig(){
        metrics = new HashMap<>();
        metrics.put("uptime", "");
        metrics.put("totalMemory", "");
        metrics.put("usedMemory", "");
        metrics.put("averageLoad", "");
        metrics.put("shortedURLs", "");
        metrics.put("redirectedURLs", "");
        metrics.put("average_RedirectionsPerURL", "");
        metrics.put("responseTimeToTheLastRedirection", "");
        metrics.put("activeUsers", "");

        flags = new HashMap<>();
        flags.put("uptime", true);
        flags.put("totalMemory", true);
        flags.put("usedMemory", true);
        flags.put("averageLoad", true);
        flags.put("shortedURLs", true);
        flags.put("redirectedURLs", true);
        flags.put("average_RedirectionsPerURL", true);
        flags.put("responseTimeToTheLastRedirection", true);
        flags.put("activeUsers", false);
    }

    public Map<String, String> getMetrics() {

        HashMap<String, String> result = (HashMap) metrics.clone();

        //Cleans forbidden fields
        for(String s: result.keySet()) {
            if(!flags.get(s)){
                result.put(s,"Forbidden");
            }
        }

        return result;
    }

    public void switchFlag(String metric){
        if(flags.containsKey(metric)){
            flags.put(metric, !flags.get(metric));
        }
    }

    @Scheduled(fixedRate = 5000)
    public void updateMetrics(){
        try {
            JSONObject readedMetrics = readJsonFromUrl("http://localhost:9090/metrics");

            metrics.put("uptime", Integer.toString( (int) readedMetrics.get("uptime") ) );
            metrics.put("totalMemory",Integer.toString( (int) readedMetrics.get("mem")));
            metrics.put("usedMemory", Integer.toString( (int) readedMetrics.get("mem.free")));
            metrics.put("averageLoad", Double.toString( (double) readedMetrics.get("systemload.average")));

            try{ //201 may not be initialized
                metrics.put("shortedURLs", Integer.toString( (int) readedMetrics.get("counter.status.201.link")));
            }catch(JSONException e){
                metrics.put("shortedURLs", Integer.toString(-1));
            }

            try{ //201 may not be initialized
                metrics.put("redirectedURLs", Integer.toString( (int) readedMetrics.get("counter.status.304.star-star")));
            }catch(JSONException e){
                metrics.put("redirectedURLs", Integer.toString(-1));
            }

            double avg = Integer.parseInt(metrics.get("redirectedURLs"))
                        / Integer.parseInt(metrics.get("shortedURLs"));

            metrics.put("average_RedirectionsPerURL", Double.toString(avg));

            try{
                metrics.put("responseTimeToTheLastRedirection",
                        Double.toString( (double) readedMetrics.get("gauge.response.star-star")));
            }catch(JSONException e){
                 metrics.put("responseTimeToTheLastRedirection", Integer.toString(-1));
            }

            metrics.put("activeUsers", "0");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
