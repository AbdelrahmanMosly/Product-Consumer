package com.university.programming2.simulation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Element {
    private int color;

    public Element(){
        color = (int)(Math.random()*255*255*255);
    }
}
