package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LoginActivityTest {
    @Mock
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

}