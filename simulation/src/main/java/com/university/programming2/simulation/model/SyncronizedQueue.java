package com.university.programming2.simulation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter @AllArgsConstructor @Getter
public class SyncronizedQueue {
    private ArrayList<Element> queue;
    private ArrayList<Machine> readyMachines;

    public SyncronizedQueue(){
        queue = new ArrayList<>();
        readyMachines = new ArrayList<>();
    }

    public synchronized int size(){
        return queue.size();
    }

    public synchronized boolean isEmpty(){
        return queue.size() == 0;
    }
    public void pushToReadyMachine(Element element){
        Machine readyMachine = readyMachines.get(0);
        readyMachine.setCurrentElement(element);
        readyMachine.getConsumer().run();
    }
    public void markAsReady(Machine machine){
        readyMachines.add(machine);
        if(!isEmpty()) {
            //pushToReadyMachine(poll());
        }
    }

    public void unmarkAsReady(Machine machine){
        readyMachines.remove(machine);
    }

    public void add(Element element){
        queue.add(element);
    }

    public synchronized Element poll(){
        if(queue.isEmpty())
            return null;
        Element ret = queue.get(0);
        queue.remove(0);
        return ret;
    }

    public synchronized Element update(){
        return poll();
    }
    public void clear(){
        queue = new ArrayList<>();
        readyMachines = new ArrayList<>();
    }
}

