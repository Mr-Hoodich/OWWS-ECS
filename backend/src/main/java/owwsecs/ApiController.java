package owwsecs;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StateManager stateManager;

    public ApiController(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Get the latest packet from every known worker.
     */
    @GetMapping("/workers")
    public Map<Integer, SensorPacket> getWorkers() {
        return stateManager.getAllWorkerStates();
    }

    /**
     * Get the latest packet from a specific worker.
     */
    @GetMapping("/workers/{workerId}")
    public SensorPacket getWorker(
            @PathVariable int workerId) {
        return stateManager.getWorkerState(workerId);
    }

    /**
     * Get the most recent packet from every known worker.
     */
    @GetMapping("/status")
    public Map<Integer, SensorPacket> getStatus() {
        return stateManager.getAllWorkerStates();
    }

    /**
     * Get the last 10 received packets, newest first.
     */
    @GetMapping("/events")
    public List<SensorPacket> getEvents() {
        return stateManager.getPacketHistory();
    }

    /**
     * Get the number of workers currently known to the backend.
     */
    @GetMapping("/workers/count")
    public int getWorkerCount() {
        return stateManager.getWorkerCount();
    }
}