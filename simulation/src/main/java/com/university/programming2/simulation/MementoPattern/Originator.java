package com.university.programming2.simulation.MementoPattern;

import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class Originator {

    private List<Machine> machineState;
    private List<Integer> queueState;
    long timeOfState;
    public Originator(){
        machineState=new ArrayList<Machine>();
        queueState=new ArrayList<Integer>();
        timeOfState=System.currentTimeMillis();
    }
    public Memento saveStateToMemento(){
        return new Memento(new ArrayList<>(machineState),new ArrayList<>(queueState),timeOfState);
    }

    public void getStateFromMemento(Memento memento){
        machineState= memento.getMachineState();
        queueState =memento.getQueueState();
        timeOfState=getTimeOfState();

    }

}
