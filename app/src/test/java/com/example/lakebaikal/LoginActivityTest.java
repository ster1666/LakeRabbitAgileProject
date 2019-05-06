package com.example.lakebaikal;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    Context context;
    @Test
    public void btaddress_regex() {
        String correcttest="38:54:32:84:22:f4";
        String errortest="xx:xx.xx:xx:xx:xxz";
        boolean result;
        result = LoginActivity.btaddress_regex( correcttest,context );

        assertTrue( result );

        result = LoginActivity.btaddress_regex( errortest,context );

        assertFalse( result );
    }

    @Test
    public void btexist() {

    }

    @Test
    public void BTactive() {
    }
}