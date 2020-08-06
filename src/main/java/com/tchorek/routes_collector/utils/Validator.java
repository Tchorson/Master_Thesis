package com.tchorek.routes_collector.utils;

import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    @Log4j2
    public static class DeviceValidator {
        static DeviceType deviceType;
        static Locations locations;

        private static final String DEVICE_PATTERN = "^[A-Z]_[A-Z0-9]_[0-9]{2}";
        private static final String SPLITTER = "_";
        private static final byte DEVICE_TYPE_INDEX = 0;
        private static final byte LOCATION_INDEX = 1;

        static Pattern pattern = Pattern.compile(DEVICE_PATTERN, Pattern.CASE_INSENSITIVE);
        static Matcher devicePatternMatcher;

        public static boolean isDeviceValid(String deviceName) {
            devicePatternMatcher = pattern.matcher(deviceName);
            if (devicePatternMatcher.matches()) {
                return isDeviceFake(deviceName);
            } else {
                log.warn("DEVICE {} DOES NOT MATCH THE PATTERN", deviceName);
                return false;
            }
        }

        private static boolean isDeviceFake(String device){
            String[] deviceLocationNumber = device.split(SPLITTER);
            boolean isDeviceTypeValid = validateDeviceType(deviceLocationNumber[DEVICE_TYPE_INDEX]);
            boolean isLocationValid = validateLocation(deviceLocationNumber[LOCATION_INDEX]);
            if( isDeviceTypeValid && isLocationValid){
                log.info("VALID DEVICE {}",device);
                return true;
            }
            else{
                prepareWarningStatement(device, isLocationValid, isDeviceTypeValid);
                return false;
            }
        }

        private static void prepareWarningStatement(String device, boolean isLocationValid, boolean isDeviceTypeValid){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("THE DEVICE {} IS INVALID DUE TO: \n");
            if(!isLocationValid)
                stringBuilder.append(" LOCATION \n");
            if(!isDeviceTypeValid)
                stringBuilder.append(" DEVICE TYPE \n");

            log.warn(stringBuilder.toString(), device);
        }

        private static boolean validateDeviceType(String deviceTypePart) {
            return deviceType.isValid(deviceTypePart);
        }

        private static boolean validateLocation(String deviceLocationPart) {
            return locations.isValid(deviceLocationPart);
        }
    }
}
