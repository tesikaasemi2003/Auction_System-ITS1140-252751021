package lk.ijse.time_auction_system_its1140;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientController {

    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String message = "";
    Socket remoteSocket;
    String username = "Anonymous";
    String currentHighestBidder = "Anonymous";
    String currentBidder = "Anonymous";

    @FXML
    private TextArea txtArea;

    @FXML
    private TextField txtMessage;

    @FXML
    private TextField txtUsername;

    @FXML
    private Label lblStatus;

    @FXML
    void sendOnAction(ActionEvent event) throws IOException {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty()) return;

        dataOutputStream = new DataOutputStream(remoteSocket.getOutputStream());
        dataOutputStream.writeUTF(username + ": " + msg);
        dataOutputStream.flush();

        txtArea.appendText("Me: " + msg + "\n");
        txtMessage.clear();
    }

    @FXML
    void setUsernameAction(ActionEvent event) {
        String name = txtUsername.getText().trim();
        if (!name.isEmpty()) {
            username = name;
            lblStatus.setText("Connected as: " + username);
            txtUsername.setDisable(true);
        }
    }

    public void initialize() {
        new Thread(() -> {
            try {
                remoteSocket = new Socket("127.0.0.1", 6000);

                javafx.application.Platform.runLater(() -> {
                    lblStatus.setText("Connected to server. Enter a username.");
                });

                dataInputStream = new DataInputStream(remoteSocket.getInputStream());

                while (!message.equals("finished")) {
                    message = dataInputStream.readUTF();
                    final String msg = message;

                    if (!msg.startsWith(username + ": ")) {
                        javafx.application.Platform.runLater(() -> {
                            txtArea.appendText(msg + "\n");
                        });
                    }
                }

            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    lblStatus.setText("Disconnected from server.");
                });
            }
        }).start();
    }

    public void sendOnDisconnect(ActionEvent actionEvent) {
        lblStatus.setText("Disconnected from server.");
    }

    public void currentHighestBid(ActionEvent actionEvent) {



        }

    public void setItem(ActionEvent actionEvent) {
    }
}

