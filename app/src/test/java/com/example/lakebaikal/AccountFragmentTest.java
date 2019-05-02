package com.example.lakebaikal;

import android.content.Context;
import android.test.ServiceTestCase;
import android.test.mock.MockContext;
import android.widget.EditText;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AccountFragmentTest {

    Context context = mock(Context.class);
    @Test
    public void number_regex() {
        String correcttest="1234";
        String errortest="1234,m";
        boolean result;
        result = AccountFragment.number_regex(correcttest, context );

        assertTrue( result );

        result = AccountFragment.number_regex(errortest,context);

        assertFalse( result );

    }
}