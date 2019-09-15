//Sakina Master
//Date: July 2018

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.Serializable;


public class Server extends Application {

    NetworkConnection connection;
    TextField portNumber;
    TextArea displayClient;
    Label port;
    Label numClients, player1, player2, player1Points, player2Points, winner;
    Button ServerOn, ServerOff;
    BorderPane root;
    int portNum;
    int index = -1;
    public static void main(String[] args) {
        launch(args);
    }

    private Parent createContent()
    {
        ServerOn = new Button("Server On");
        ServerOff = new Button("Server Off");

        port = new Label("Enter the port to connect to clients, then press SERVER ON:");
        portNumber = new TextField("5555");
        portNumber.setMinWidth(80);
        displayClient = new TextArea();
        displayClient.setPrefSize(100, 100);
        root = new BorderPane();
        root.setPadding(new Insets(40));

        HBox h = new HBox(30, portNumber, ServerOn, ServerOff);
        root.setCenter(h);
        h.setAlignment(Pos.TOP_LEFT);



        VBox portLabel = new VBox(10, port, h);
        root.setTop(portLabel);
        portLabel.setAlignment(Pos.TOP_LEFT);

        ServerOn.setOnAction(event -> {
            portNum = Integer.parseInt(portNumber.getText());
            portNumber.clear();
            numClients = new Label();
            player1 = new Label();
            player2 = new Label();
            player1Points = new Label();
            player2Points = new Label();
            winner = new Label("Winner: -");

            numClients.setText("Number of players connected: " + NetworkConnection.numClients);
            VBox clientNo = new VBox(20, numClients, displayClient);
            root.setLeft(clientNo);
            clientNo.setAlignment(Pos.CENTER_LEFT);


            try {
                //data is what we received from the other end.
                connection = new NetworkConnection(portNum, data->{           //data->{ } defines the code for accept()
                    Platform.runLater(()-> {   //gives the control back to the UI thread

                        ServerOff.setOnAction(e ->  connection.closeConn());

                        String playerNames = "Players Online: \n";
                        if(connection.clientList.size() > 0) {
                            playerNames += connection.clientNames.get(0);
                            displayClient.setText(playerNames);
                            for (int i = 1; i < connection.clientNames.size(); i++) {
                                playerNames += "\n" + connection.clientNames.get(i);
                                displayClient.setText(playerNames); //Displaying on server GUI
                            }
                        }
                        if(connection.clientList.size() == 0) {
                            displayClient.setText("No clients connected.");
                        }


                        for (int i = 0; i < connection.clientList.size(); i++) {
                            connection.clientList.get(i).sendMsg(playerNames);
                        }

                        //No of clients gets updated.
                        numClients.setText("Number of players connected: " + NetworkConnection.numClients);

                        synchronized (this) {
                            if ((connection.Pairs.size() >= 1)) {
                                for (int j = 0; j < connection.Pairs.size(); j++) {
                                    //if((connection.Pairs.get(j).get(0).getClientPlay() != "nothing") && (connection.Pairs.get(j).get(1).getClientPlay() != "nothing")) {
                                    if ((!"nothing".equals(connection.Pairs.get(j).get(0).getClientPlay())) && (!"nothing".equals(connection.Pairs.get(j).get(1).getClientPlay()))) {
                                        Serializable msg1 = connection.Pairs.get(j).get(0).getClientPlay();
                                        player1.setText("Player 1 Played: " + msg1);
                                        Serializable msg2 = connection.Pairs.get(j).get(1).getClientPlay();
                                        player2.setText("Player 2 Played: " + msg2);
                                        //isPlayer2Choice = true;
                                        connection.Pairs.get(j).get(1).sendMsg(connection.Pairs.get(j).get(0).getClientPlay());
                                        connection.Pairs.get(j).get(0).sendMsg(connection.Pairs.get(j).get(1).getClientPlay());

                                        int whoWon = connection.playGame(connection.Pairs.get(j).get(0).getClientPlay(), connection.Pairs.get(j).get(1).getClientPlay());
                                        if (whoWon == 1) {
                                            String message1 = "Winner: " + connection.Pairs.get(j).get(0).getClientName();
                                            winner.setText(message1);
                                            connection.Pairs.get(j).get(0).sendMsg(message1);
                                            connection.Pairs.get(j).get(1).sendMsg(message1);
                                        }
                                        if (whoWon == 2) {
                                            String message2 = "Winner: " + connection.Pairs.get(j).get(1).getClientName();
                                            winner.setText(message2);
                                            connection.Pairs.get(j).get(0).sendMsg(message2);
                                            connection.Pairs.get(j).get(1).sendMsg(message2);
                                            //isWinner = true;
                                        }

                                        if (whoWon == 3) {
                                            String message3 = "Winner: DRAW";
                                            winner.setText(message3);
                                            connection.Pairs.get(j).get(0).sendMsg(message3);
                                            connection.Pairs.get(j).get(1).sendMsg(message3);
                                            //isWinner = true;
                                        }
                                        connection.Pairs.get(j).get(0).setClientPlay();
                                        connection.Pairs.get(j).get(1).setClientPlay();

                                        index = j;
                                    }

                                    if (index != -1) {
                                        connection.Pairs.remove(index);
                                        index = -1;
                                    }
                                }
                            }
                        }
                        VBox player = new VBox(20, numClients, displayClient,  player1, player2);
                        root.setLeft(player);
                        player.setAlignment(Pos.CENTER_LEFT);



                        VBox points = new VBox(20, player1Points, player2Points);
                        root.setRight(points);
                        points.setAlignment(Pos.CENTER_RIGHT);


                        VBox play = new VBox(20, winner);
                        root.setBottom(play);
                        play.setAlignment(Pos.BASELINE_CENTER);

                    });
                });
                connection.startConn();
            }
            catch (Exception e) {
                System.out.println("Exception in Server Gui");
            }
        });
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(createContent(), 500, 400));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception{
        connection.closeConn();
    }
}


