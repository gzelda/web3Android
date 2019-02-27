package com.example.web3view.model;

public class RejectTransactionCallback {

    private String error;

    public RejectTransactionCallback setError(final String error) {
        this.error = error;
        return this;
    }


    public String toJsonEncodedString() {
        return String.format(
                "{\\\"error\\\":\\\"%s\\\"}",
                error
        );
    }
}
