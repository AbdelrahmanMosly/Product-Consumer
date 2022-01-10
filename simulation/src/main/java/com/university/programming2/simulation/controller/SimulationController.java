package com.university.programming2.simulation.controller;

import com.google.gson.Gson;
import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;

@CrossOrigin("http://localhost:4200")
@RestController
public class SimulationController {
    private static SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    private static ArrayList<Machine> machines = new ArrayList<>();
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
        emitter.onCompletion(() -> emitter = null);
        return emitter;
    }

    @PostMapping("/makeMachine")
    public void makeMachine(@RequestBody Integer num) throws InterruptedException {
        Machine machine = new Machine(queues.get(num.intValue()));
        new Thread(machine).start();
        machines.add(machine);
    }

    @PostMapping("/makeQueue")
    public void makeQueue(){
        queues.add(new SyncronizedQueue());
    }

    @PostMapping("/connect")
    public void connect(@RequestParam int from, @RequestParam int to){
        machines.get(to).subscribe(queues.get(from));
    }

    @GetMapping("/push")
    public static void pushToClient(){
        try {
            emitter.send(SseEmitter.event().name("Update").data(parser.toJson(machines)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
