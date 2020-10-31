package de.htw.saar.env.sim.device.scheduling;

import de.htw.saar.env.sim.Mocks.ProxyMQTTClientMock;
import de.htw.saar.env.sim.Mocks.SchedulerMock;
import de.htw.saar.env.sim.device.container.DeviceHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.test.util.ReflectionTestUtils;
import testUtil.testUtils;

import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class DistributorTest {

    Distributor distributor;
    DeviceHeader header;

    @BeforeEach
    public void setUp(){
        testUtils.initializeIOManager();
        distributor = new Distributor();
        distributor.proxy = new ProxyMQTTClientMock();
        distributor.scheduler = new SchedulerMock();
        header = new DeviceHeader();
        header.setId((long) 1);
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("SomeTopic");
        tmp.add("SomeOtherTopic");
        header.setSubscribeList(tmp);
    }

    @Test
    void addSubscription() {
        distributor.addSubscription(header);
        assertTrue(distributor.topicMap.get("SomeTopic").contains(header.getId()));
    }

    @Test
    void removeSubscription() {
        addSubscription();
        distributor.removeSubscription(header);
        assertFalse(distributor.topicMap.contains("SomeTopic"));
    }

    @Test
    void removeAll() {
        addSubscription();
        distributor.removeAll();
        assertTrue(distributor.topicMap.isEmpty());
    }

    @Test
    void run(){
        try {
            Thread thread = new Thread(distributor);
            thread.start();
            sleep(100);
        } catch (NullPointerException | InterruptedException exception){fail();}
        assertTrue(((Distributor.MessageBuffer) ReflectionTestUtils.getField(distributor, "buffer")).buffer.isEmpty());
    }
}