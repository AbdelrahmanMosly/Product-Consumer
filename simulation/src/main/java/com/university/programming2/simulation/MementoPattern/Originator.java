package com.university.programming2.simulation.MementoPattern;

import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter @NoArgsConstructor
public class Originator {

    private List<Machine> machineState;
    private List<Integer> queueState;
    private boolean wait;

    public Memento saveStateToMemento(){
        return new Memento(new ArrayList<>(machineState),new ArrayList<>(queueState),wait);
    }

    public void getStateFromMemento(Memento memento){
        machineState= memento.getMachineState();
        queueState =memento.getQueueState();
        wait=memento.isWait();

    }

}
