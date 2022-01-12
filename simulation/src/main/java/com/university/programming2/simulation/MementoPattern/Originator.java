package com.university.programming2.simulation.MementoPattern;

import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
public class Originator {

    private List<Machine> machineState;
    private ArrayList<SyncronizedQueue> queueState;

    public Memento saveStateToMemento(){
        return new Memento(machineState,queueState);
    }

    public void getStateFromMemento(Memento memento){
        machineState= memento.getMachineState();
        queueState =memento.getQueueState();
    }

}
