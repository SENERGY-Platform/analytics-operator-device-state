import org.infai.ses.senergy.operators.Builder;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.utils.ConfigProvider;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestMessageProvider {

    public static List<Message> getTestMesssagesSet() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/sample-data-small.json"));
        Builder builder = new Builder("1", "1");
        List<Message> messageSet = new ArrayList<>();
        Config config = new Config(getConfig());
        ConfigProvider.setConfig(config);


        String line;
        Message m;
        JSONObject jsonObjectRead, jsonObject;
        while ((line = br.readLine()) != null) {
            if (line.equals("")) {
                continue;
            }
            jsonObjectRead = new JSONObject(line);
            jsonObject = new JSONObject().put("device_id", "1").put("value", new JSONObject().put("reading", jsonObjectRead));
            m = new Message(builder.formatMessage(jsonObject.toString()));
            messageSet.add(m);
        }
        return messageSet;
    }

    public static String getConfig() {
        return "{\n" +
                "\"config\": {\n" +
                "    \"minDurationOffMillis\": \"300000\",\n" +
                "    \"standbyPower\": \"1.0\",\n" +
                "    \"minDurationCycleMillis\": \"1200000\"\n" +
                "  },\n" +
                "  \"inputTopics\": [\n" +
                "    {\n" +
                "      \"name\": \"iot_bc59400c-405c-4c84-9862-a791daa82b60\",\n" +
                "      \"filterType\": \"DeviceId\",\n" +
                "      \"filterValue\": \"1\",\n" +
                "      \"mappings\": [\n" +
                "        {\n" +
                "          \"dest\": \"value\",\n" +
                "          \"source\": \"value.reading.value\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"dest\": \"timestamp\",\n" +
                "          \"source\": \"value.reading.timestamp\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
