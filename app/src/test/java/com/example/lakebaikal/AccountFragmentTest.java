package com.example.lakebaikal;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountFragmentTest {

    Context context;

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