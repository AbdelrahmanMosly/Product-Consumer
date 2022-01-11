package com.university.programming2.simulation;

import com.university.programming2.simulation.controller.SimulationController;
import com.university.programming2.simulation.model.Element;
import com.university.programming2.simulation.model.Machine;
import com.university.programming2.simulation.model.SyncronizedQueue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimulationApplicationTests {

	SimulationController controller = new SimulationController();

	@Test
	void apiTest() throws InterruptedException {
		controller.makeQueue();
		controller.makeQueue();
		controller.makeQueue();
		controller.makeQueue();

		controller.makeMachine();
		controller.makeMachine();

		controller.connectMachineToQueue(0, 1);
		controller.connectMachineToQueue(1, 2);

		controller.connectQueueToMachine(0, 0);
		controller.connectQueueToMachine(1, 0);
		controller.connectQueueToMachine(2, 1);

		controller.queues.get(2).add(new Element(12345));
		controller.queues.get(0).add(new Element(123));
		controller.queues.get(0).add(new Element(456));
		controller.queues.get(1).add(new Element(1234));
		controller.queues.get(1).add(new Element(5678));
	}

	@Test
	void contextLoads() throws InterruptedException {
		SyncronizedQueue q1 = new SyncronizedQueue();
		SyncronizedQueue q2 = new SyncronizedQueue();
		SyncronizedQueue q3 = new SyncronizedQueue();
		SyncronizedQueue q4 = new SyncronizedQueue();

		Machine m1 = new Machine(q3);
		Machine m2 = new Machine(q4);

		//m1.start();
		//m2.start();
		//m1.join();
		//m2.join();

		m1.subscribe(q1);
		m1.subscribe(q2);
		m2.subscribe(q3);

		q3.add(new Element(12345));
		q1.add(new Element(123));
		q1.add(new Element(456));
		q2.add(new Element(1234));
		q2.add(new Element(5678));
	}

}
