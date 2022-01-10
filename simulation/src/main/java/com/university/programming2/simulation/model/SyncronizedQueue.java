package com.university.programming2.simulation.model;

import java.util.ArrayList;

public class SyncronizedQueue {
    private ArrayList<Element> queue;
    private ArrayList<Machine> readyMachines;

    public SyncronizedQueue(){
        queue = new ArrayList<>();
        readyMachines = new ArrayList<>();
    }

    public synchronized boolean isEmpty(){
        return queue.size() == 0;
    }
    public synchronized void pushToReadyMachine(Element element){
        Machine readyMachine = readyMachines.get(0);
        readyMachine.setCurrentElement(element);
        readyMachine.run();
    }
    public synchronized void markAsReady(Machine machine){
        readyMachines.add(machine);
        if(!isEmpty()) {
            pushToReadyMachine(poll());
        }
    }

    public synchronized void unmarkAsReady(Machine machine){
        readyMachines.remove(machine);
    }

    public synchronized void add(Element element){
        if(!readyMachines.isEmpty())
            pushToReadyMachine(element);
        else
            queue.add(element);
    }

    public synchronized Element poll(){
        Element ret = queue.get(0);
        queue.remove(0);
        return ret;
    }

    public synchronized Element update(){
        return poll();
    }
}
