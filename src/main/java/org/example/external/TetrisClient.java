package org.example.external;

import com.google.gson.Gson;
import org.example.model.OpMove;
import org.example.model.PureGame;

import java.io.*;
import java.net.Socket;

public class TetrisClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;
    private static final Gson gson = new Gson();

    public OpMove requestMove(PureGame game) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Serialize game state
            String jsonGameState = gson.toJson(game);
            out.println(jsonGameState);

            // Receive response
            String response = in.readLine();
            return gson.fromJson(response, OpMove.class);
        }
    }
}