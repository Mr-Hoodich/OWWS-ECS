
package owwsecs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FakeDataRunner implements CommandLineRunner {

    private final StateManager stateManager;

    public FakeDataRunner(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== FAKE DATA RUNNER STARTED ===");

        String[] fakePackets = {
            "1,105,2,420,28,1,0,0",
            "2,106,1,780,35,0,1,1",
            "3,107,3,350,29,0,2,2"
        };

        for (String data : fakePackets) {
            SensorPacket packet = PacketParser.parse1sPacket(data);

            if (packet != null) {
                stateManager.updateState(packet);
                System.out.println("Fake packet accepted: " + data);
            } else {
                System.out.println("Fake packet rejected: " + data);
            }
        }

        System.out.println("Worker count: " + stateManager.getWorkerCount());
        System.out.println("=== FAKE DATA RUNNER FINISHED ===");
    }
}
