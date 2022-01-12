package com.university.programming2.simulation.controller;

import com.google.gson.Gson;
import com.university.programming2.simulation.MementoPattern.CareTaker;
import com.university.programming2.simulation.MementoPattern.Memento;
import com.university.programming2.simulation.MementoPattern.Originator;
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
    private static Originator originator = new Originator();
    private static CareTaker careTaker = new CareTaker();
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
        originator = new Originator();
        careTaker = new CareTaker();
        int rand = 5;
        System.out.println(rand);
        for(int i=0; i<rand; i++)
            queues.get(0).add(new Element());
        for(int i=0; i<machines.size(); i++)
            machines.get(i).start();
    }
    @PostMapping("/replay")
    public static void replay(){
        redrawToClient();
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
    @GetMapping("/redraw")
    public static void redrawToClient(){

        try {
            int min_duration=Integer.MAX_VALUE;
            for(Memento memento:careTaker.getMemntoList()){
                ArrayList<Integer> counts = new ArrayList<>();
                for(Machine machine:memento.getMachineState()){
                    min_duration=Integer.min(min_duration,machine.getDuration());
                }
                machines=memento.getMachineState();
                queues=memento.getQueueState();
                System.out.println("ii"+min_duration);
                try {
                    Thread.sleep(min_duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("ff"+min_duration);

                ArrayList<Integer> colors = new ArrayList();
                for (Machine machine : machines){
                    if (machine.getCurrentElement() == null)
                        colors.add(0);
                    else
                        colors.add(machine.getCurrentElement().getColor());
                }
                for (SyncronizedQueue queue : queues)
                    counts.add(queue.size());
                System.out.println(parser.toJson(colors));
                System.out.println(parser.toJson(counts));
                emitter.send(SseEmitter.event().name("Redraw").data(parser.toJson(colors)));
                emitter.send(SseEmitter.event().name("Count").data(parser.toJson(counts)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/push")
    public static void pushToClient(){
        try {
            originator.setQueueState(queues);
            originator.setMachineState(machines);
            careTaker.add(originator.saveStateToMemento());

            ArrayList<Integer> colors = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();

            for (Machine machine : machines){
                if (machine.getCurrentElement() == null)
                    colors.add(0);
                else
                    colors.add(machine.getCurrentElement().getColor());
            }

            for (SyncronizedQueue queue : queues)
                counts.add(queue.size());
            //System.out.println(parser.toJson(colors));
            emitter.send(SseEmitter.event().name("Update").data(parser.toJson(colors)));
            emitter.send(SseEmitter.event().name("Count").data(parser.toJson(counts)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
