package com.tracker.config;

import org.springframework.stereotype.Component;

@Component
public class StockState {

    private boolean lastKnownStatus = false;

    public boolean getLastKnownStatus() {
        return lastKnownStatus;
    }

    public void setLastKnownStatus(boolean status) {
        this.lastKnownStatus = status;
    }
}