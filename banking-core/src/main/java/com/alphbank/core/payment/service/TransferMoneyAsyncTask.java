package com.alphbank.core.payment.service;

public class TransferMoneyAsyncTask implements Runnable {

    private final Runnable transferAction;

    public TransferMoneyAsyncTask(Runnable transferAction) {
        this.transferAction = transferAction;
    }

    @Override
    public void run() {
        transferAction.run();
    }
}
