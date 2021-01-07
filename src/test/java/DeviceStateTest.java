import org.infai.ses.senergy.models.DeviceMessageModel;
import org.infai.ses.senergy.models.MessageModel;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.infai.ses.senergy.operators.device_state.DeviceState;
import org.infai.ses.senergy.testing.utils.JSONHelper;
import org.infai.ses.senergy.utils.ConfigProvider;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;


public class DeviceStateTest {

    @Test
    public void testDeviceState() {
        Config config = new Config(new JSONHelper().parseFile("config.json").toString());
        JSONArray messages = new JSONHelper().parseFile("messages.json");
        String topicName = config.getInputTopicsConfigs().get(0).getName();
        ConfigProvider.setConfig(config);
        Message message = new Message();
        MessageModel model = new MessageModel();
        OperatorInterface testOperator = new DeviceState();
        message.addInput("expectIsOn");
        message.addInput("expectTS");
        testOperator.configMessage(message);
        for (Object msg : messages) {
            DeviceMessageModel deviceMessageModel = JSONHelper.getObjectFromJSONString(msg.toString(), DeviceMessageModel.class);
            assert deviceMessageModel != null;
            model.putMessage(topicName, Helper.deviceToInputMessageModel(deviceMessageModel, topicName));
            message.setMessage(model);
            testOperator.run(message);

            boolean valueExpected = (boolean) deviceMessageModel.getValue().get("expectIsOn");
            String timestampExpected = (String) deviceMessageModel.getValue().get("expectTS"); //message.toString().split("expectTS\":")[1].split(",")[0];

            boolean valueActual = (boolean) message.getMessage().getOutputMessage().getAnalytics().get("isOn");
            String timestampActual = (String) message.getMessage().getOutputMessage().getAnalytics().get("timestamp");

            Assert.assertEquals(valueExpected, valueActual);
            Assert.assertEquals(timestampExpected, timestampActual);
        }
    }
}
