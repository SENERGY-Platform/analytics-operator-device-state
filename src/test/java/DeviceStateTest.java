import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.device_state.DeviceState;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import java.util.List;
import org.junit.contrib.java.lang.system.EnvironmentVariables;


public class DeviceStateTest {

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();


    @Test
    public void testDeviceState() throws Exception {
        environmentVariables.set("CONFIG", TestMessageProvider.getConfig());

        DeviceState deviceState = new DeviceState();
        List<Message> messages = TestMessageProvider.getTestMesssagesSet();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            deviceState.configMessage(m);
            deviceState.run(m);

            boolean valueActual = Boolean.parseBoolean(m.getMessageString().split("isOn\":")[1].split("}")[0]);
            boolean valueExpected = Boolean.parseBoolean(m.getMessageString().split("expectIsOn\":")[1].split(",")[0]);
            String timestampExpected = m.getMessageString().split("timestamp\":")[1].split(",")[0];
            String timestampActual = m.getMessageString().split("expectTS\":")[1].split(",")[0];
            Assert.assertEquals(valueExpected, valueActual);
            Assert.assertEquals(timestampExpected, timestampActual);
        }
    }
}
