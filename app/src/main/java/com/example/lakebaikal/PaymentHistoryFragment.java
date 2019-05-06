package com.example.lakebaikal;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.lakebaikal.AccountFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentHistoryFragment extends Fragment {

    View fragment;
    ListView listView1;
    private List<String> fetchedData = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference paymentHistory;

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
        fragment = inflater.inflate(R.layout.fragment_payment_history, container, false);

        database = FirebaseDatabase.getInstance();
        paymentHistory = database.getReference("PaymentHistory/" + LoginActivity.bt_addr);

        listView1 = fragment.findViewById(R.id.historyListView);
        listView1.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, fetchedData));



        fetchPaymentDataFromFirebase(paymentHistory);
        // Inflate the layout for this fragment
        return fragment;
    }

    private void fetchPaymentDataFromFirebase(DatabaseReference paymentHistory){
        paymentHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot post : dataSnapshot.getChildren()){

                        String tempCost = String.valueOf(post.child("paymentCost").getValue());
                        String tempTime = String.valueOf(post.child("paymentTimestamp").getValue());

                        String fullString = "Date: " + tempTime + "\nPrice: " + tempCost;
                        fetchedData.add(fullString);

                        for(int i = 0; i < fetchedData.size(); i++){
                            listView1.setSelection(i);
                        }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getDetails();
                Toast.makeText(getContext(), databaseError.getDetails(), Toast.LENGTH_LONG);
            }
        });
    }
}