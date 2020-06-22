package com.example.tourmatebatch14.Repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tourmatebatch14.POJOS.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private FirebaseUser firebaseUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference expenseRef;
    public MutableLiveData<List<Expense>> expenseListLD=new MutableLiveData<>();

    public ExpenseRepository(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(firebaseUser.getUid());
        expenseRef = userRef.child("Expenses");

    }

    public void saveNewExpensetoRTDB(Expense expense){
        String expenseId = expenseRef.push().getKey();
        expense.setExpenseId(expenseId);
        expenseRef.child(expense.getEventid()).child(expenseId).setValue(expense);
    }

    public MutableLiveData<List<Expense>> getAllExpensesbyEventId(String eventId){
        expenseRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Expense> expenses = new ArrayList<>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    expenses.add(d.getValue(Expense.class));
                }
                expenseListLD.postValue(expenses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return expenseListLD;
    }
}
