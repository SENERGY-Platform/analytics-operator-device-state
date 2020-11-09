/*
 * Copyright 2018 InfAI (CC SES)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.infai.ses.senergy.operators.device_state;

import org.infai.ses.senergy.exceptions.NoValueException;
import org.infai.ses.senergy.operators.BaseOperator;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.infai.ses.senergy.util.DateParser;
import org.infai.ses.senergy.utils.ConfigProvider;

public class DeviceState extends BaseOperator {

    private final long INVALID = -1;

    private boolean isOn;
    private long onCycleStartedMillis;
    private long belowStandbyPowerSince;

    private long minDurationOffMillis;
    private long minDurationCycleMillis;
    private double standbyPower;

    public DeviceState() {
        minDurationOffMillis = Long.parseLong(config.getConfigValue("minDurationOffMillis", "0"));
        minDurationCycleMillis = Long.parseLong(config.getConfigValue("minDurationCycleMillis", "0"));
        standbyPower = Double.parseDouble(config.getConfigValue("standbyPower", "0"));

        isOn = false;
        onCycleStartedMillis = INVALID;
        belowStandbyPowerSince = INVALID;
    }

    @Override
    public void run(Message message) {
        double value = 0;
        try {
            value = message.getInput("value").getValue();
        } catch (NoValueException e) {
            e.printStackTrace();
            return;
        }
        String timestamp = message.getInput("timestamp").getString();
        long timestampMillis = DateParser.parseDateMills(timestamp);

        boolean isAboveStandbyPower = value > standbyPower;

        if (isAboveStandbyPower) {
            belowStandbyPowerSince = INVALID;
        }

        if(!isOn && isAboveStandbyPower) {
            isOn = true;
            onCycleStartedMillis = timestampMillis;
        }

        if (isOn && value < standbyPower) {
            if (belowStandbyPowerSince == INVALID) {
                belowStandbyPowerSince = timestampMillis;
            }
            if (timestampMillis - belowStandbyPowerSince >= minDurationOffMillis && timestampMillis - onCycleStartedMillis >= minDurationCycleMillis) {
                isOn = false;
                onCycleStartedMillis = INVALID;
            }
        }

        message.output("timestamp", timestamp);
        message.output("isOn", isOn);
    }

    @Override
    public Message configMessage(Message message) {
        message.addInput("value");
        message.addInput("timestamp");
        return message;
    }
}
