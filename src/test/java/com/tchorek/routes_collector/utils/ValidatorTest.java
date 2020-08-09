package com.tchorek.routes_collector.utils;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Profile("Test")
@ExtendWith(MockitoExtension.class)
class ValidatorTest {

    @Mock
    Validator validator;

    private String correctName = "RPI_PARK_01";

    @Test
    @DisplayName("Given correct device name, When validating, Should return true")
    public void validateCorrectDevice(){
        when(validator.isDeviceValid(correctName)).thenReturn(true);

        Assert.assertTrue(validator.isDeviceValid(correctName));

        verify(validator).isDeviceValid(correctName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RPI", "rpi_PARK_01","AUDI_PARK_01","RPI_YODA_01"})
    @DisplayName("Given incorrect device names, When validating, Should return false")
    public void validateIncorrectDevices(String incorrectDeviceName){
        when(validator.isDeviceValid(anyString())).thenReturn(false);

        Assert.assertFalse(validator.isDeviceValid(incorrectDeviceName));

        verify(validator).isDeviceValid(incorrectDeviceName);
    }
}
