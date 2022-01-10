package com.university.programming2.simulation.model;

import java.util.ArrayList;
import java.util.Queue;

public class SyncronizedQueue {
    private ArrayList<Element> queue;

    public SyncronizedQueue(){
        queue = new ArrayList<>();
    }

    public synchronized void add(Element element){
        queue.add(element);
    }

    public synchronized Element poll(){
        Element ret = queue.get(0);
        queue.remove(ret);
        return ret;
    }

    public synchronized Element update(){
        return poll();
    }
}
