package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class homeActivityTest {

    @InjectMocks
    homeActivity mtest = new homeActivity();
    @Mock
    Context mcontext = mock( MockContext.class);
    @Mock
    BluetoothManager bluman = mock(BluetoothManager.class);
    @Mock
    BluetoothAdapter adapter= mock(BluetoothAdapter.class);

    @Test
    public void get_userBtaddr() {

    }

    @Test
    public void compareTimestamps() {
    }
}