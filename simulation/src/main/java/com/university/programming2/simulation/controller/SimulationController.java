package com.university.programming2.simulation.controller;

import com.google.gson.Gson;
import com.university.programming2.simulation.model.Element;
import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController
public class SimulationController {
    private static SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    private static List<Machine> machines = new ArrayList<>();
    public static ArrayList<SyncronizedQueue> queues = new ArrayList<>();
    private static Gson parser = new Gson();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(){
        emitter = new SseEmitter(Long.MAX_VALUE);
        try{
            emitter.send(SseEmitter.event().name("INIT"));
        }catch (Exception e){
            e.printStackTrace();
        }
        emitter.onCompletion(() -> emitter = new SseEmitter(Long.MAX_VALUE));
        return emitter;
    }

    @PostMapping("/start")
    public void start() throws InterruptedException {
        int rand = (int)(Math.random()*50 + 1);
        for(int i=0; i<rand; i++)
            queues.get(0).add(new Element());
        for(int i=0; i<machines.size(); i++){
            machines.get(i).start();
            machines.get(i).join();
        }
    }

    @PostMapping("/makeMachine")
    public void makeMachine() throws InterruptedException {
        Machine machine = new Machine();
        machines.add(machine);
    }

    @PostMapping("/makeQueue")
    public void makeQueue(){
        queues.add(new SyncronizedQueue());
    }

    @PostMapping("/connect/machineToQueue")
    public void connectMachineToQueue(@RequestParam Integer from, @RequestParam Integer to){
        machines.get(from).setNextQueue(queues.get(to));
    }

    @PostMapping("/connect/queueToMachine")
    public void connectQueueToMachine(@RequestParam Integer from, @RequestParam Integer to){
        machines.get(to).subscribe(queues.get(from));
    }

    @GetMapping("/push")
    public static void pushToClient(){
        try {
            ArrayList<Integer> colors = new ArrayList();
            for (Machine machine : machines){
                if (machine.getCurrentElement() == null)
                    colors.add(0);
                else
                    colors.add(machine.getCurrentElement().getColor());
            }
            System.out.println(parser.toJson(colors));
            emitter.send(SseEmitter.event().name("Update").data(parser.toJson(colors)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
