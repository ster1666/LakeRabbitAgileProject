package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.transition.Transition;
import android.test.mock.MockContext;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class bluetoothActivityTest {

    @InjectMocks
    bluetoothActivity mtest = new bluetoothActivity();
    @Mock
    Context mcontext = mock( MockContext.class);
    @Mock
    BluetoothManager bluman = mock(BluetoothManager.class);
    @Mock
    BluetoothAdapter adapter= mock(BluetoothAdapter.class);

    @Test
    public void t_btexist() {
        when(bluman.getAdapter()).thenReturn( adapter );
        assertTrue(mtest.btexist( mcontext,bluman ) );

        when( bluman.getAdapter() ).thenReturn(null);
        assertFalse(mtest.btexist( mcontext,bluman ) );

    }

    @Test
    public void t_BTactive() {
        when(adapter.isEnabled()).thenReturn( true );
        assertTrue( mtest.BTactive( adapter ) );

        when(adapter.isEnabled()).thenReturn(false );
        assertFalse( mtest.BTactive( adapter ) );
    }
}