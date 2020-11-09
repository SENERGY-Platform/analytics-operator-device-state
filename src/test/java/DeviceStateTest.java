import org.infai.ses.senergy.models.DeviceMessageModel;
import org.infai.ses.senergy.models.MessageModel;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.device_state.DeviceState;
import org.infai.ses.senergy.testing.utils.JSONHelper;
import org.infai.ses.senergy.utils.ConfigProvider;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;


public class DeviceStateTest {



    private JSONArray messages = new JSONHelper().parseFile("sample-data-small.json");

    @Test
    public void testDeviceState() throws Exception {
        Config config = new Config(getConfig());
        String topicName = config.getInputTopicsConfigs().get(0).getName();
        ConfigProvider.setConfig(config);
        Message message = new Message();
        MessageModel model =  new MessageModel();
        DeviceState deviceState = new DeviceState();
        deviceState.configMessage(message);
        for(Object msg : messages){
            DeviceMessageModel deviceMessageModel = JSONHelper.getObjectFromJSONString(msg.toString(), DeviceMessageModel.class);
            assert deviceMessageModel != null;
            model.putMessage(topicName, Helper.deviceToInputMessageModel(deviceMessageModel, topicName));
            message.setMessage(model);
            message.addInput("expectIsOn");
            message.addInput("expectTS");

            deviceState.run(message);



            boolean valueExpected  = (boolean) deviceMessageModel.getValue().get("expectIsOn");
            String timestampExpected = (String) deviceMessageModel.getValue().get("expectTS"); //message.toString().split("expectTS\":")[1].split(",")[0];

            boolean valueActual = (boolean) message.getMessage().getOutputMessage().getAnalytics().get("isOn");
            String timestampActual = (String) message.getMessage().getOutputMessage().getAnalytics().get("timestamp");

            Assert.assertEquals(valueExpected, valueActual);
            Assert.assertEquals(timestampExpected, timestampActual);
       }
    }

    private static String getConfig() {
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
                "      \"filterValue\": \"0\",\n" +
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
