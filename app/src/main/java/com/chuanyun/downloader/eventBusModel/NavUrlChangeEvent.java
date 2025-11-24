package com.chuanyun.downloader.eventBusModel;

public class NavUrlChangeEvent {
    private int index;
    public NavUrlChangeEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
