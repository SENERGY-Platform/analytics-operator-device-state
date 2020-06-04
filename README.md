# operator-device-state

Reads power consumption of a device with a complex consumption cycle and tries to estimate the state of the device. It was primarly developed to check the state of a washing machine.

## Inputs

* value (float): power consumption of device
* timestamp (string): timestamp of value

## Outputs

* isOn (boolean): estimated state of the device
* timestamp (string): timestamp as read

## Configs

* standbyPower (float): How much power consumes the device in standby? Power consumption above this threshold will be considered as the start of a cycle.
* minDurationOffMillis (long): How long does the device need to consume power below the standby power to be considered off? Provide value in miliseconds.
* minDurationCycleMillis (long): How long (in miliseconds) is a cycle at minimum? This is used to prevent estimating an off state to early, when the device is reducing power consumption within a cycle for periods longer than minDurationOffMillis.

Configuration of these config values are highly dependent on the specific device and must be set accordingly.
