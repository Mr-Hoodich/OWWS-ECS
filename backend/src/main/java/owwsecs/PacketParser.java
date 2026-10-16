package owwsecs;

public class PacketParser {

    /**
     * Parses the routine 1-second telemetry packet.
     *
     * Expected CSV field order:
     * worker_id,packet_id,status,gas_level,temperature,
     * flags,hop_count,relay_id
     *
     * Example:
     * 1,105,2,420,28,1,0,0
     */
    public static SensorPacket parse1sPacket(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }

        try {
            String[] fields = data.trim().split(",", -1);

            if (fields.length != 8) {
                return null;
            }

            int workerId = Integer.parseInt(fields[0].trim());
            int packetId = Integer.parseInt(fields[1].trim());
            int status = Integer.parseInt(fields[2].trim());
            int gasLevel = Integer.parseInt(fields[3].trim());
            int temperature = Integer.parseInt(fields[4].trim());
            int flags = Integer.parseInt(fields[5].trim());
            int hopCount = Integer.parseInt(fields[6].trim());
            int relayId = Integer.parseInt(fields[7].trim());

            // Validate field ranges against the C++ data types.
            if (workerId < 0 || workerId > 255
                    || packetId < 0 || packetId > 65535
                    || status < 0 || status > 4
                    || gasLevel < 0 || gasLevel > 65535
                    || temperature < -32768 || temperature > 32767
                    || flags < 0 || flags > 255
                    || hopCount < 0 || hopCount > 255
                    || relayId < 0 || relayId > 255) {
                return null;
            }

            SensorPacket packet = new SensorPacket();

            packet.setWorkerId(workerId);
            packet.setPacketId(packetId);
            packet.setStatus(status);
            packet.setGasLevel(gasLevel);
            packet.setTemperature(temperature);
            packet.setFlags(flags);
            packet.setHopCount(hopCount);
            packet.setRelayId(relayId);

            return packet;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses the detailed 3-second telemetry packet.
     * Implementation reserved for later.
     */
    public static SensorPacket parse3sPacket(String data) {
        // TODO: Implement when the 3-second packet format is finalized.
        return null;
    }
}