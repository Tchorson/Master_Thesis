package com.tchorek.routes_collector.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
public class Validator {

    DeviceType deviceType;
    Locations locations;

    private final String PHONE_PATTERN = "^[0-9]{9}";
    private final String DEVICE_PATTERN = "^[A-Z]{3,}_[A-Z0-9]{3,}_[0-9]{2}$";
    private final String SPLITTER = "_";
    private final byte DEVICE_TYPE_INDEX = 0;
    private final byte LOCATION_INDEX = 1;

    Pattern registrationPattern = Pattern.compile(PHONE_PATTERN);
    Pattern devicePattern = Pattern.compile(DEVICE_PATTERN, Pattern.CASE_INSENSITIVE);
    Matcher patternMatcher;

    public boolean isNumberValid(String number){ //not mandatory: add verification if user's number is allowed to register.
        patternMatcher = registrationPattern.matcher(number);
        if(patternMatcher.matches()){
            return true;
        }
        log.warn("PHONE NUMBER {} VALIDATION FAILED", number);
        return false;
    }

    public boolean isDeviceValid(String deviceName) {
        patternMatcher = devicePattern.matcher(deviceName);
        if (patternMatcher.matches()) {
            return isDeviceFake(deviceName);
        } else {
            log.warn("DEVICE {} DOES NOT MATCH THE PATTERN", deviceName);
            return false;
        }
    }

    private boolean isDeviceFake(String device) {
        String[] deviceLocationNumber = device.split(SPLITTER);
        boolean isDeviceTypeValid = validateDeviceType(deviceLocationNumber[DEVICE_TYPE_INDEX]);
        boolean isLocationValid = validateLocation(deviceLocationNumber[LOCATION_INDEX]);
        if (isDeviceTypeValid && isLocationValid) {
            log.info("VALID DEVICE {}", device);
            return true;
        } else {
            prepareWarningStatement(device, isLocationValid, isDeviceTypeValid);
            return false;
        }
    }

    private void prepareWarningStatement(String device, boolean isLocationValid, boolean isDeviceTypeValid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("THE DEVICE {} IS INVALID DUE TO: \n");
        if (!isLocationValid)
            stringBuilder.append(" LOCATION \n");
        if (!isDeviceTypeValid)
            stringBuilder.append(" DEVICE TYPE \n");

        log.warn(stringBuilder.toString(), device);
    }

    private boolean validateDeviceType(String deviceTypePart) {
        return deviceType.isValid(deviceTypePart);
    }

    private boolean validateLocation(String deviceLocationPart) {
        return locations.isValid(deviceLocationPart);
    }

}
