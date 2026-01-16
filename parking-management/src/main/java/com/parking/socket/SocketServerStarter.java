package com.parking.socket;

import com.parking.*;
import com.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Requirement 4: Socket Server (runs alongside Spring Boot REST API)
 * This starts automatically when Spring Boot starts
 */
@Component
public class SocketServerStarter implements CommandLineRunner {

    @Value("${socket.server.port}")
    private int socketPort;

    @Value("${socket.server.enabled}")
    private boolean socketEnabled;

    @Autowired
    private ParkingLotRepository lotRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public void run(String... args) {
        if (socketEnabled) {
            new Thread(() -> startSocketServer()).start();
        }
    }

    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            System.out.println("Socket Server started on port " + socketPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Socket client connected: " + clientSocket.getInetAddress());

                // Handle each client in separate thread (Requirement 3)
                new Thread(new SocketClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Socket server error: " + e.getMessage());
        }
    }

    // Inner class to handle socket clients
    private class SocketClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public SocketClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Welcome to Parking Management Socket Server");
                out.println("Commands: LIST_LOTS|LIST_SPOTS|CREATE_RESERVATION|EXIT");

                String request;
                while ((request = in.readLine()) != null) {
                    String response = processRequest(request);
                    out.println(response);

                    if (request.equals("EXIT")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Socket client error: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        private String processRequest(String request) {
            String[] parts = request.split("\\|");
            String command = parts[0];

            try {
                switch (command) {
                    case "LIST_LOTS":
                        return handleListLots();

                    case "LIST_SPOTS":
                        if (parts.length < 2) return "ERROR: Usage: LIST_SPOTS|lotId";
                        return handleListSpots(parts[1]);

                    case "CREATE_RESERVATION":
                        if (parts.length < 7) {
                            return "ERROR: Usage: CREATE_RESERVATION|resId|spotNum|plate|name|startDate|endDate|price";
                        }
                        return handleCreateReservation(parts);

                    case "EXIT":
                        return "Goodbye!";

                    default:
                        return "ERROR: Unknown command";
                }
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        private String handleListLots() {
            List<ParkingLot> lots = lotRepository.findAll();
            if (lots.isEmpty()) {
                return "No parking lots available";
            }

            StringBuilder sb = new StringBuilder("Parking Lots:\n");
            for (ParkingLot lot : lots) {
                sb.append(lot.getLotId()).append(" - ").append(lot.getName())
                        .append(" (").append(lot.getLocation()).append(")\n");
            }
            return sb.toString();
        }

        private String handleListSpots(String lotId) {
            List<ParkingSpot> spots = spotRepository.findByLotId(lotId);
            StringBuilder sb = new StringBuilder("Spots in " + lotId + ":\n");

            for (ParkingSpot spot : spots) {
                sb.append(spot.getSpotNumber()).append(" - ")
                        .append(spot.getType()).append(" - ")
                        .append(spot.getStatus()).append("\n");
            }
            return sb.toString();
        }

        private String handleCreateReservation(String[] parts) {
            try {
                ParkingSpot spot = spotRepository.findById(parts[2])
                        .orElseThrow(() -> new EntityNotFoundException("Spot not found"));

                Reservation res = new Reservation(
                        parts[1], parts[2], parts[3], parts[4],
                        parts[5], parts[6], Double.parseDouble(parts[7])
                );

                spot.occupy(res.getReservationId());
                spotRepository.save(spot);
                reservationRepository.save(res);

                return "SUCCESS: Reservation created - Total: $" + res.calculateTotal();
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                System.out.println("Socket client disconnected");
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}