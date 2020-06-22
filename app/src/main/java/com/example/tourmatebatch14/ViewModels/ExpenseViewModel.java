package com.example.tourmatebatch14.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmatebatch14.POJOS.Expense;
import com.example.tourmatebatch14.Repositories.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends ViewModel {
    private ExpenseRepository expenseRepository;
    public MutableLiveData<List<Expense>> expenseListLD = new MutableLiveData<>();

    public ExpenseViewModel(){
        expenseRepository = new ExpenseRepository();
        expenseListLD = expenseRepository.expenseListLD;
    }

    public void saveExpense(Expense expense){
        expenseRepository.saveNewExpensetoRTDB(expense);
    }

    public void getExpenses(String eventId){
        expenseListLD = expenseRepository.getAllExpensesbyEventId(eventId);
    }

}
