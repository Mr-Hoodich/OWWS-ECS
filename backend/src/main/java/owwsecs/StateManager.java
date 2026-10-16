package owwsecs;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class StateManager {

    private static final int MAX_HISTORY = 10;

    // Latest packet received from each worker.
    private final Map<Integer, SensorPacket> workerStates =
            new HashMap<>();

    // Recent packets, newest first.
    private final Deque<SensorPacket> packetHistory =
            new ArrayDeque<>();

    /**
     * Store a newly parsed packet.
     */
    public synchronized void updateState(SensorPacket packet) {
        if (packet == null) {
            return;
        }

        workerStates.put(packet.getWorkerId(), packet);

        // Keep the newest packet at the front.
        packetHistory.addFirst(packet);

        // Discard packets beyond the history limit.
        while (packetHistory.size() > MAX_HISTORY) {
            packetHistory.removeLast();
        }
    }

    /**
     * Get the latest packet for a specific worker.
     */
    public synchronized SensorPacket getWorkerState(int workerId) {
        return workerStates.get(workerId);
    }

    /**
     * Get the latest packet from every known worker.
     */
    public synchronized Map<Integer, SensorPacket> getAllWorkerStates() {
        return new HashMap<>(workerStates);
    }

    /**
     * Get recent packets, newest first.
     */
    public synchronized List<SensorPacket> getPacketHistory() {
        return new ArrayList<>(packetHistory);
    }

    /**
     * Get the number of workers whose packets have been received.
     */
    public synchronized int getWorkerCount() {
        return workerStates.size();
    }

    /**
     * Clear stored state and packet history.
     */
    public synchronized void clear() {
        workerStates.clear();
        packetHistory.clear();
    }
}