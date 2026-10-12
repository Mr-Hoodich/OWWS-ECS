package owwsecs;

import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialReceiver {

    private SerialPort serialPort;

    public SerialReceiver(String portName, int baudRate) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
    }

    public void start() {

        if (!serialPort.openPort()) {
            System.out.println("Failed to open serial port.");
            return;
        }

        System.out.println("Serial connection established.");

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(serialPort.getInputStream()))) {

            String packet;

            while ((packet = reader.readLine()) != null) {

                System.out.println("Received: " + packet);

                // PacketParser will handle this later.
                // PacketParser.parse(packet);
            }

        } catch (Exception e) {

            System.out.println("Serial connection error: "
                    + e.getMessage());

        } finally {

            serialPort.closePort();
            System.out.println("Serial connection closed.");
        }
    }
}