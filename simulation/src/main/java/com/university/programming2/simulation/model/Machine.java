package com.university.programming2.simulation.model;

import com.university.programming2.simulation.controller.SimulationController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;

@Setter @Getter @AllArgsConstructor
public class Machine implements Cloneable{
    private Thread producer;
    private Thread consumer;
    private Element currentElement;
    private ArrayList<SyncronizedQueue> observers;
    private SyncronizedQueue nextQueue;
    private int duration;


    public Machine(){
        currentElement = null;
        observers = new ArrayList<>();
        nextQueue = new SyncronizedQueue();
        duration= (int) (Math.random()*2000) + 500;
        this.start();
    }

    @Override
    public Machine clone() {
        return new Machine(this.getProducer(),
                this.getConsumer(), this.getCurrentElement(),
                this.getObservers(), this.getNextQueue(), this.getDuration());
    }

    public Machine(SyncronizedQueue next){
        currentElement = null;
        observers = new ArrayList<>();
        nextQueue = next;
        this.start();
    }

    public void start(){
        if(producer == null || !producer.isAlive()){
            producer = new Thread( () -> {
                try {
                    this.process();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            producer.start();
        }
        if(consumer == null || !consumer.isAlive()){
            consumer = new Thread( () -> {
                try {
                    this.notifyObservers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            consumer.start();
        }
    }

    public void subscribe(SyncronizedQueue queue){
        observers.add(queue);
        if(currentElement == null)
            queue.markAsReady(this);
    }

    public void notifyObservers() throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (currentElement != null)
                    wait();

                // to retrieve the ifrst job in the list
                for (int i = 0; i < observers.size(); i++) {
                    SyncronizedQueue observer = observers.get(i);
                    Element polledElement = observer.update();
                    if (polledElement != null) {
                        currentElement = polledElement;
                        break;
                    }
                }

                if (currentElement != null) {
                    //System.out.println("Consumer consumed-" + currentElement.getColor());
                    notify();
                }
            }
        }
    }

    public void process() throws InterruptedException {
        while(true){
            synchronized (this){
                while(currentElement == null)
                    wait();
                SimulationController.pushToClient();
                //System.out.printf("Processing element %s on %s for a duration of %d%n", currentElement.getColor() , this, duration);
                Thread.sleep(duration-100);
                Element flashElement=new Element(16579836);
                Element temp=new Element(currentElement.getColor());
                currentElement.setColor(flashElement.getColor());
                SimulationController.pushToClient();
                Thread.sleep(100);
                currentElement.setColor(temp.getColor());
                SimulationController.pushToClient();
                if (currentElement != null) {
                    Element currentElementCopy = new Element(currentElement.getColor());
                    currentElement = null;
                    nextQueue.add(currentElementCopy);
                }
                SimulationController.pushToClient();
                notify();
            }

        }
    }
    public  void clear(){
        currentElement = null;
        duration= (int) (Math.random()*2000) + 500;
    }
}
