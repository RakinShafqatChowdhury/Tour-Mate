package com.example.tourmatebatch14.POJOS;

public class Expense {
    private String expenseId;
    private String eventid;
    private String expenseName;
    private int expenseAmount;
    private String expenseDateandTime;

    public Expense() {
    }

    public Expense(String expenseId, String eventid, String expenseName, int expenseAmount, String expenseDateandTime) {
        this.expenseId = expenseId;
        this.eventid = eventid;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.expenseDateandTime = expenseDateandTime;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public int getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(int expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseDateandTime() {
        return expenseDateandTime;
    }

    public void setExpenseDateandTime(String expenseDateandTime) {
        this.expenseDateandTime = expenseDateandTime;
    }
}
