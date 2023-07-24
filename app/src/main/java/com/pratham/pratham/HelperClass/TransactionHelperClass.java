package com.pratham.feedin.HelperClass;

public class TransactionHelperClass {
    private String transactionDate, amount;

    public TransactionHelperClass() {
    }

    public TransactionHelperClass(String transactionDate , String amount) {
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
