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

import javax.crypto.Mac;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@CrossOrigin("http://localhost:4200")
@RestController
public class SimulationController {
    private static SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    private static List<Machine> machines = new ArrayList<>();
    public static ArrayList<SyncronizedQueue> queues = new ArrayList<>();
    private static Originator originator = new Originator();
    private static CareTaker careTaker = new CareTaker();
    private static Gson parser = new Gson();

    long timeOfLastState=System.currentTimeMillis();
    boolean replayFlag=false;
    Thread replay=   new Thread("replay thread" ){
        public void run(){
            redrawToClient();
        }
    };
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
     static void clearMachine(){
        for(Machine machine:machines)
            machine.clear();
    }
    static void clearQueue(){
        for(SyncronizedQueue queue:queues){
            queue.clear();
        }
    }
    @PostMapping("/deleteAll")
    static void deleteAll(){
        System.out.println("HereD");
        queues = new ArrayList<>();
        machines = new ArrayList<>();
        originator = new Originator();
        careTaker = new CareTaker();
        pushToClient();
    }

    @PostMapping("/clear")
    static void clear(){
        System.out.println("Clear");
        clearMachine();
        clearQueue();
        originator=new Originator();
        careTaker=new CareTaker();
        pushToClient();
    }

    @PostMapping("/start")
    public void start() throws InterruptedException {
        if(replay.isAlive())
            replay.stop();
        originator=new Originator();
        careTaker=new CareTaker();
        int rand = (int)(Math.random()*30) + 20;;
        System.out.println(rand);
        for(int i=0; i<rand; i++)
            queues.get(0).add(new Element());
        for(int i=0; i<machines.size(); i++)
            machines.get(i).start();
    }
    @PostMapping("/replay")
    public void replay(){
        if(replayFlag){
            resume();
            replayFlag=false;
            replay.stop();
            return;
        }
        pause();
        replayFlag=true;
        System.out.println(replayFlag);

        replay=new Thread("replay thread" ){
            public void run(){
                redrawToClient();
            }
        };
        System.out.println(Thread.currentThread().getName());
        replay.start();
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

    public  void lastReplayShoot(Memento memento2){
        machines=new ArrayList<>(memento2.getMachineState());
        ArrayList<Integer> counts =new ArrayList<>(memento2.getQueueState());
        ArrayList<Integer> colors = new ArrayList();
        for (Machine machine : machines){
            if (machine.getCurrentElement() == null)
                colors.add(0);
            else
                colors.add(machine.getCurrentElement().getColor());
        }
        System.out.println(parser.toJson(colors));
        System.out.println(parser.toJson(counts));
        try {
            emitter.send(SseEmitter.event().name("Redraw").data(parser.toJson(colors)));
            emitter.send(SseEmitter.event().name("Count").data(parser.toJson(counts)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @GetMapping("/redraw")
    public void redrawToClient(){
        Memento memento2=new Memento();
        ArrayList<Memento> mementoList=careTaker.getMemntoList();
        ArrayList<Machine> machines=new ArrayList<>();
        for(int i=0;i<mementoList.size()-1;i++){
            Memento memento=mementoList.get(i);
            memento2=mementoList.get(i+1);
            long duration=memento2.getTimeOfState()-memento.getTimeOfState();
            machines=new ArrayList<>(memento.getMachineState());
            ArrayList<Integer> counts =new ArrayList<>(memento.getQueueState());
            ArrayList<Integer> colors = new ArrayList();
            for (Machine machine : machines){
                if (machine.getCurrentElement() == null)
                    colors.add(0);
                else
                    colors.add(machine.getCurrentElement().getColor());
            }
            try {
                emitter.send(SseEmitter.event().name("Redraw").data(parser.toJson(colors)));
                emitter.send(SseEmitter.event().name("Count").data(parser.toJson(counts)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException("Replay Thread interrupted..."+e);
            }
        }
        lastReplayShoot(memento2);
    }

     static void save(){
        List<Integer> queuesState = new ArrayList<>();
        for (SyncronizedQueue queue : queues)
            queuesState.add(queue.size());

        List<Machine> machineState = new ArrayList<>();
        for (int i = 0; i < machines.size(); i++) {
            machineState.add(machines.get(i).clone());
        }
        originator.setTimeOfState(System.currentTimeMillis());
        originator.setQueueState(queuesState);
        originator.setMachineState(machineState);
        careTaker.add(originator.saveStateToMemento());
    }
    @GetMapping("/push")
    public synchronized static void pushToClient(){
        try {
            System.out.println(machines.size());
            save();
            ArrayList<Integer> colors = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();
            for (Machine machine : machines){
                if (machine.getCurrentElement() == null)
                    colors.add(0);
                else {
                    colors.add(machine.getCurrentElement().getColor());
                }
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

    @PostMapping("/pause")
    public void pause(){
        for(int i=0; i<machines.size(); i++) {
            Machine machine = machines.get(i);
            machine.getProducer().stop();
            machine.getConsumer().stop();
        }
    }

    @PostMapping("/resume")
    public void resume(){
        for(int i=0; i<machines.size(); i++)
            machines.get(i).start();
    }

}
