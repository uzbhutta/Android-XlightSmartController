package com.umarbhutta.xlightcompanion.SDK;

/**
 * Created by sunboss on 2016-12-16.
 */

// MySensors Serial Protocol, refer to https://www.mysensors.org/download/serial_api_20
// Message format:
// node-id;child-sensor-id;message-type;ack;sub-type;payload\n
@SuppressWarnings({"UnusedDeclaration"})
public class SerialMessage {

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------
    // Message types
    public static final int C_PRESENTATION = 0;
    public static final int C_SET = 1;
    public static final int C_REQ = 2;
    public static final int C_INTERNAL = 3;
    public static final int C_STREAM = 4;  // For Firmware and other larger chunks of data that need to be divided into pieces.

    // Type of sensor (used when presenting sensors)
    public static final int S_DOOR = 0;                         // Door sensor, V_TRIPPED, V_ARMED
    public static final int S_MOTION = 1;                       // Motion sensor, V_TRIPPED, V_ARMED
    public static final int S_SMOKE = 2;                        // Smoke sensor, V_TRIPPED, V_ARMED
    public static final int S_LIGHT = 3;                        // Binary light or relay, V_STATUS (or V_LIGHT), V_WATT
    public static final int S_BINARY = 3;                       // Binary light or relay, V_STATUS (or V_LIGHT), V_WATT (same as S_LIGHT)
    public static final int S_DIMMER = 4;                       // Dimmable light or fan device, V_STATUS (on/off), V_DIMMER (dimmer level 0-100), V_WATT
    public static final int S_COVER = 5;                        // Blinds or window cover, V_UP, V_DOWN, V_STOP, V_DIMMER (open/close to a percentage)
    public static final int S_TEMP = 6;                         // Temperature sensor, V_TEMP
    public static final int S_HUM = 7;                          // Humidity sensor, V_HUM
    public static final int S_BARO = 8;                         // Barometer sensor, V_PRESSURE, V_FORECAST
    public static final int S_WIND = 9;                         // Wind sensor, V_WIND, V_GUST
    public static final int S_RAIN = 10;                        // Rain sensor, V_RAIN, V_RAINRATE
    public static final int S_UV = 11;                          // Uv sensor, V_UV
    public static final int S_WEIGHT = 12;                      // Personal scale sensor, V_WEIGHT, V_IMPEDANCE
    public static final int S_POWER = 13;                       // Power meter, V_WATT, V_KWH
    public static final int S_HEATER = 14;                      // Header device, V_HVAC_SETPOINT_HEAT, V_HVAC_FLOW_STATE, V_TEMP
    public static final int S_DISTANCE = 15;                    // Distance sensor, V_DISTANCE
    public static final int S_LIGHT_LEVEL = 16;                 // Light level sensor, V_LIGHT_LEVEL (uncalibrated in percentage),  V_LEVEL (light level in lux)
    public static final int S_ARDUINO_NODE = 17;                // Used (internally) for presenting a non-repeating Arduino node
    public static final int S_ARDUINO_REPEATER_NODE = 18;       // Used (internally) for presenting a repeating Arduino node
    public static final int S_LOCK = 19;                        // Lock device, V_LOCK_STATUS
    public static final int S_IR = 20;                          // Ir device, V_IR_SEND, V_IR_RECEIVE
    public static final int S_WATER = 21;                       // Water meter, V_FLOW, V_VOLUME
    public static final int S_AIR_QUALITY = 22;                 // Air quality sensor, V_LEVEL
    public static final int S_CUSTOM = 23;                      // Custom sensor
    public static final int S_DUST = 24;                        // Dust sensor, V_LEVEL
    public static final int S_SCENE_CONTROLLER = 25;            // Scene controller device, V_SCENE_ON, V_SCENE_OFF.
    public static final int S_RGB_LIGHT = 26;                   // RGB light. Send color component data using V_RGB. Also supports V_WATT
    public static final int S_RGBW_LIGHT = 27;                  // RGB light with an additional White component. Send data using V_RGBW. Also supports V_WATT
    public static final int S_COLOR_SENSOR = 28;                // Color sensor, send color information using V_RGB
    public static final int S_HVAC = 29;                        // Thermostat/HVAC device. V_HVAC_SETPOINT_HEAT, V_HVAC_SETPOINT_COLD, V_HVAC_FLOW_STATE, V_HVAC_FLOW_MODE, V_TEMP
    public static final int S_MULTIMETER = 30;                  // Multimeter device, V_VOLTAGE, V_CURRENT, V_IMPEDANCE
    public static final int S_SPRINKLER = 31;                   // Sprinkler, V_STATUS (turn on/off), V_TRIPPED (if fire detecting device)
    public static final int S_WATER_LEAK = 32;                  // Water leak sensor, V_TRIPPED, V_ARMED
    public static final int S_SOUND = 33;                       // Sound sensor, V_TRIPPED, V_ARMED, V_LEVEL (sound level in dB)
    public static final int S_VIBRATION = 34;                   // Vibration sensor, V_TRIPPED, V_ARMED, V_LEVEL (vibration in Hz)
    public static final int S_MOISTURE = 35;                    // Moisture sensor, V_TRIPPED, V_ARMED, V_LEVEL (water content or moisture in percentage?)

    // Type of sensor data (for set/req/ack messages)
    public static final int V_TEMP = 0;                         // S_TEMP. Temperature S_TEMP, S_HEATER, S_HVAC
    public static final int V_HUM = 1;                          // S_HUM. Humidity
    public static final int V_STATUS = 2;                       //  S_LIGHT, S_DIMMER, S_SPRINKLER, S_HVAC, S_HEATER. Used for setting/reporting binary (on/off) status. 1=on, 0=off
    public static final int V_LIGHT = 2;                        // Same as V_STATUS
    public static final int V_PERCENTAGE = 3;                   // S_DIMMER. Used for sending a percentage value 0-100 (%).
    public static final int V_DIMMER = 3;                       // S_DIMMER. Same as V_PERCENTAGE.
    public static final int V_PRESSURE = 4;                     // S_BARO. Atmospheric Pressure
    public static final int V_FORECAST = 5;                     // S_BARO. Whether forecast. string of "stable", "sunny", "cloudy", "unstable", "thunderstorm" or "unknown"
    public static final int V_RAIN = 6;                         // S_RAIN. Amount of rain
    public static final int V_RAINRATE = 7;                     // S_RAIN. Rate of rain
    public static final int V_WIND = 8;                         // S_WIND. Wind speed
    public static final int V_GUST = 9;                         // S_WIND. Gust
    public static final int V_DIRECTION = 10;                   // S_WIND. Wind direction 0-360 (degrees)
    public static final int V_UV = 11;                          // S_UV. UV light level
    public static final int V_WEIGHT = 12;                      // S_WEIGHT. Weight(for scales etc)
    public static final int V_DISTANCE = 13;                    // S_DISTANCE. Distance
    public static final int V_IMPEDANCE = 14;                   // S_MULTIMETER, S_WEIGHT. Impedance value
    public static final int V_ARMED = 15;                       // S_DOOR, S_MOTION, S_SMOKE, S_SPRINKLER. Armed status of a security sensor. 1 = Armed, 0 = Bypassed
    public static final int V_TRIPPED = 16;                     // S_DOOR, S_MOTION, S_SMOKE, S_SPRINKLER, S_WATER_LEAK, S_SOUND, S_VIBRATION, S_MOISTURE. Tripped status of a security sensor. 1 = Tripped, 0
    public static final int V_WATT = 17;                        // S_POWER, S_LIGHT, S_DIMMER, S_RGB, S_RGBW. Watt value for power meters
    public static final int V_KWH = 18;                         // S_POWER. Accumulated number of KWH for a power meter
    public static final int V_SCENE_ON = 19;                    // S_SCENE_CONTROLLER. Turn on a scene
    public static final int V_SCENE_OFF = 20;                   // S_SCENE_CONTROLLER. Turn of a scene
    public static final int V_HEATER = 21;                      // Deprecated. Use V_HVAC_FLOW_STATE instead.
    public static final int V_HVAC_FLOW_STATE = 21;             // S_HEATER, S_HVAC. HVAC flow state ("Off", "HeatOn", "CoolOn", or "AutoChangeOver")
    public static final int V_HVAC_SPEED = 22;                  // S_HVAC, S_HEATER. HVAC/Heater fan speed ("Min", "Normal", "Max", "Auto")
    public static final int V_LIGHT_LEVEL = 23;                 // S_LIGHT_LEVEL. Uncalibrated light level. 0-100%. Use V_LEVEL for light level in lux
    public static final int V_VAR1 = 24;
    public static final int V_VAR2 = 25;
    public static final int V_VAR3 = 26;
    public static final int V_VAR4 = 27;
    public static final int V_VAR5 = 28;
    public static final int V_UP = 29;                          // S_COVER. Window covering. Up
    public static final int V_DOWN = 30;                        // S_COVER. Window covering. Down
    public static final int V_STOP = 31;                        // S_COVER. Window covering. Stop
    public static final int V_IR_SEND = 32;                     // S_IR. Send out an IR-command
    public static final int V_IR_RECEIVE = 33;                  // S_IR. This message contains a received IR-command
    public static final int V_FLOW = 34;                        // S_WATER. Flow of water (in meter)
    public static final int V_VOLUME = 35;                      // S_WATER. Water volume
    public static final int V_LOCK_STATUS = 36;                 // S_LOCK. Set or get lock status. 1=Locked, 0=Unlocked
    public static final int V_LEVEL = 37;                       // S_DUST, S_AIR_QUALITY, S_SOUND (dB), S_VIBRATION (hz), S_LIGHT_LEVEL (lux)
    public static final int V_VOLTAGE = 38;                     // S_MULTIMETER
    public static final int V_CURRENT = 39;                     // S_MULTIMETER
    public static final int V_RGB = 40;                         // S_RGB_LIGHT, S_COLOR_SENSOR.
    // Used for sending color information for multi color LED lighting or color sensors.
    // Sent as ASCII hex: RRGGBB (RR=red, GG=green, BB=blue component)
    public static final int V_RGBW = 41;                        // S_RGBW_LIGHT
    // Used for sending color information to multi color LED lighting.
    // Sent as ASCII hex: RRGGBBWW (WW=white component)
    public static final int V_ID = 42;                          // S_TEMP
    // Used for sending in sensors hardware ids (i.e. OneWire DS1820b).
    public static final int V_UNIT_PREFIX = 43;                 // S_DUST, S_AIR_QUALITY
    // Allows sensors to send in a string representing the
    // unit prefix to be displayed in GUI, not parsed by controller! E.g. cm, m, km, inch.
    // Can be used for S_DISTANCE or gas concentration
    public static final int V_HVAC_SETPOINT_COOL = 44;          // S_HVAC. HVAC cool setpoint (Integer between 0-100)
    public static final int V_HVAC_SETPOINT_HEAT = 45;          // S_HEATER, S_HVAC. HVAC/Heater setpoint (Integer between 0-100)
    public static final int V_HVAC_FLOW_MODE = 46;              // S_HVAC. Flow mode for HVAC ("Auto", "ContinuousOn", "PeriodicOn")

    // Type of internal messages (for internal messages)
    public static final int I_BATTERY_LEVEL = 0;
    public static final int I_TIME = 1;
    public static final int I_VERSION = 2;
    public static final int I_ID_REQUEST = 3;
    public static final int I_ID_RESPONSE = 4;
    public static final int I_INCLUSION_MODE = 5;
    public static final int I_CONFIG = 6;
    public static final int I_FIND_PARENT = 7;
    public static final int I_FIND_PARENT_RESPONSE = 8;
    public static final int I_LOG_MESSAGE = 9;
    public static final int I_CHILDREN = 10;
    public static final int I_SKETCH_NAME = 11;
    public static final int I_SKETCH_VERSION = 12;
    public static final int I_REBOOT = 13;
    public static final int I_GATEWAY_READY = 14;
    public static final int I_REQUEST_SIGNING = 15;
    public static final int I_GET_NONCE = 16;
    public static final int I_GET_NONCE_RESPONSE = 17;

    // Type of data stream  (for streamed message)
    public static final int ST_FIRMWARE_CONFIG_REQUEST = 0;
    public static final int ST_FIRMWARE_CONFIG_RESPONSE = 1;
    public static final int ST_FIRMWARE_REQUEST = 2;
    public static final int ST_FIRMWARE_RESPONSE = 3;
    public static final int ST_SOUND = 4;
    public static final int ST_IMAGE = 5;
}
