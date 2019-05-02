package com.example.lakebaikal;

import android.content.Context;
import android.test.ServiceTestCase;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LoginActivityTest {

    Context context = mock(Context.class);
    @Test
    public void btaddress_regex() {
        String correcttest="38:54:32:84:22:f4";
        String errortest="xx:xx.xx:xx:xx:xxz";
        boolean result;
        result = LoginActivity.btaddress_regex( errortest,context );

        assertTrue( result );

        result = LoginActivity.btaddress_regex( correcttest,context );

        assertFalse( result );
    }

    @Test
    public void btexist() {

    }

    @Test
    public void BTactive() {
    }
}