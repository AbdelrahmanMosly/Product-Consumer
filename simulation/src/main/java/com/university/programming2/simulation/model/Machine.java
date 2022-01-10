package com.university.programming2.simulation.model;

import com.university.programming2.simulation.controller.SimulationController;
import lombok.SneakyThrows;

import java.util.ArrayList;


public class Machine implements Runnable{
    private Element currentElement;
    private ArrayList<SyncronizedQueue> observers;
    private SyncronizedQueue nextQueue;

    public Machine(SyncronizedQueue next){
        currentElement = null;
        observers = new ArrayList<>();
        nextQueue = next;
    }

    public void setCurrentElement(Element element){
        currentElement = element;
    }

    public Element getCurrentElement(){
        return currentElement;
    }

    public synchronized void subscribe(SyncronizedQueue queue){
        observers.add(queue);
        if(currentElement == null)
            queue.markAsReady(this);
    }

    public synchronized void notifyObservers(){
        //Case 1: any observer has an item waiting to be processed: poll it and process
        for(SyncronizedQueue observer: observers){
            if(!observer.isEmpty()){
                currentElement = observer.update();
                this.run();
                return;
            }
        }

        //Case 2: all observers are empty: mark this machine as ready
        for(SyncronizedQueue observer: observers){
            observer.markAsReady(this);
        }
    }

    @SneakyThrows
    @Override
    public void run(){
        if(currentElement != null)
            process();
    }

    public void process() throws InterruptedException {
        for(SyncronizedQueue observer: observers){
            observer.unmarkAsReady(this);
        }
        SimulationController.pushToClient();
        int duration = (int) (Math.random()*5000);
        System.out.printf("Processing element %s on %s for a duration of %d%n", currentElement.getColor() , this, duration);
        Thread.sleep(duration);
        nextQueue.add(currentElement);
        currentElement = null;
        SimulationController.pushToClient();
        notifyObservers();
    }
}
