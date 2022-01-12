package com.university.programming2.simulation.MementoPattern;

import lombok.Getter;

import java.util.ArrayList;
@Getter
public class CareTaker {
    private ArrayList<Memento> memntoList=new ArrayList<Memento>();
    public void add(Memento state){
        memntoList.add(state);
    }

    public Memento get(int index){
        return memntoList.get(index);
    }
}
