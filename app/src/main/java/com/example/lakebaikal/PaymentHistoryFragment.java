package com.example.lakebaikal;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentHistoryFragment extends Fragment {


    public static PaymentHistoryFragment newInstance() {
        PaymentHistoryFragment paymentHistoryFragment = new PaymentHistoryFragment();
        return paymentHistoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize views here

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }

}
