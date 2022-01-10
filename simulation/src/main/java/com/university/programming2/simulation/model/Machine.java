package com.university.programming2.simulation.model;

import java.util.ArrayList;

public class Machine {
    private Element currentElement;
    private ArrayList<SyncronizedQueue> observers;

    public Machine(){
        currentElement = null;
        observers = new ArrayList<>();
    }

    public synchronized void subscribe(SyncronizedQueue queue){
        observers.add(queue);
    }

    public synchronized void notifyObservers(){
        if(observers.isEmpty())
            return;
        observers.get(0).update()
    }
}
