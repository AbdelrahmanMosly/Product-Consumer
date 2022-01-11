package com.university.programming2.simulation.model;

import com.university.programming2.simulation.controller.SimulationController;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;

@Getter @Setter
public class Machine extends Thread{
    private Element currentElement;
    private ArrayList<SyncronizedQueue> observers;
    private SyncronizedQueue nextQueue;

    public Machine(){
        currentElement = null;
        observers = new ArrayList<>();
        nextQueue = new SyncronizedQueue();
    }

    public Machine(SyncronizedQueue next){
        currentElement = null;
        observers = new ArrayList<>();
        nextQueue = next;
    }
    public synchronized void setCurrentElement(Element element){
        this.currentElement=element;
    }

    public synchronized void subscribe(SyncronizedQueue queue){
        observers.add(queue);
        if(currentElement == null)
            queue.markAsReady(this);
    }

    public synchronized void notifyObservers(){
        //Case 1: any observer has an item waiting to be processed: poll it and process
       /* for(int i=0; i<observers.size(); i++){
            SyncronizedQueue observer = observers.get(i);
            Element polledElement = observer.update();
            if(polledElement != null){
                currentElement = polledElement;
                this.run();
                return;
            }
        }
*/
        //Case 2: all observers are empty: mark this machine as ready
        for(int i=0; i<observers.size(); i++){
            SyncronizedQueue observer = observers.get(i);
            observer.markAsReady(this);
        }
    }

    @SneakyThrows
    @Override
    public void run(){
        if(currentElement == null)
            notifyObservers();
        else
            process();
    }

    public void  process() throws InterruptedException {
        if(currentElement==null)
            return;
        for(int i=0; i<observers.size(); i++){
            SyncronizedQueue observer = observers.get(i);
            observer.unmarkAsReady(this);
        }
        SimulationController.pushToClient();
        int duration = (int) ((Math.random()) * 1000);
        System.out.printf("Processing element %s on %s for a duration of %d%n", currentElement.getColor(), this, duration);
        if (currentElement != null) {
            Thread.sleep(duration);
            Element currentElementCopy = new Element(currentElement.getColor());
            currentElement = null;
            nextQueue.add(currentElementCopy);
        }
        SimulationController.pushToClient();
        notifyObservers();
    }
}
