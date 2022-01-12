package com.university.programming2.simulation.MementoPattern;

import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @Getter @NoArgsConstructor
public class Memento {
    private List<Machine> machineState;
    private ArrayList<Integer> queueState;
    long timeOfState;

}
