package com.alphbank.coreloanservice.service;

public class PayOutLoanAsyncTask implements Runnable {

    private final Runnable transferAction;

    public PayOutLoanAsyncTask(Runnable transferAction) {
        this.transferAction = transferAction;
    }

    @Override
    public void run() {
        transferAction.run();
    }
}
