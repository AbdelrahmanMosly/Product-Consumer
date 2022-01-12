package com.university.programming2.simulation.MementoPattern;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Getter
public class CareTaker {
    private ArrayList<Memento> memntoList=new ArrayList<Memento>();
    private Memento lastMemento;
    public CareTaker(){
        memntoList=new ArrayList<Memento>();
        lastMemento=new Memento(new ArrayList<>(),new ArrayList<>(),System.currentTimeMillis());
    }
    public void add(Memento state){
        memntoList.add(state);
    }
    public void setLastMemento(Memento memento){
        lastMemento=memento;
    }

    public Memento get(int index){
        return memntoList.get(index);
    }
}
