//Sakina Master
//Date: July 2018

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Client extends Application
{
    NetworkConnection connection;
    TextField portNumber, IPAddress, name, opponentName;
    Label Enterport, EnterIPAdd, EnterName;
    Label opponentPlayed, oppName, winner, playerName, choice, EnterOpponentName, question, answer;
    Button connectServer;
    Button rock, paper, scissors, lizard, spock;
    Button challenge, accept, decline, quit;
    TextArea communication;
    BorderPane root;
    int portNum;
    String hostName;
    String clientName;
    Boolean isSet = false;

    public static void main(String[] args)
    {
        launch(args);
    }

    private Parent createContent()
    {
        Enterport = new Label("Enter the port no:");
        EnterIPAdd = new Label("Enter the IP Address:");
        EnterName = new Label("Enter your name:");
        portNumber = new TextField("5555");
        IPAddress = new TextField("127.0.0.1");
        name = new TextField();
        communication = new TextArea();
        communication.setPrefHeight(90);
        connectServer = new Button("Connect to Server");

        playerName = new Label("Player: ");
        choice = new Label("Select your choice: ");
        choice.setFont(new Font("Ariel", 20));

        opponentPlayed = new Label("Opponent Played: ");
        oppName = new Label("Opponent Name: ");
        winner = new Label("Winner: ");

        answer = new Label();

        EnterOpponentName = new Label("Enter the player name you want to challenge");
        opponentName = new TextField();

        question = new Label("Do u want to accept ***** challenge: ");
        accept = new Button("Accept");
        decline = new Button("Decline");
        rock = new Button();
        paper = new Button();
        scissors = new Button();
        lizard = new Button();
        spock = new Button();
        challenge = new Button("Challenge");

        Image rockPic = new Image("Rock.jpg");
        ImageView r = new ImageView(rockPic);
        r.setFitHeight(90);
        r.setFitWidth(90);
        r.setPreserveRatio(true);
        rock.setGraphic(r);

        Image paperPic = new Image("Paper.png");
        ImageView p = new ImageView(paperPic);
        p.setFitHeight(90);
        p.setFitWidth(90);
        p.setPreserveRatio(true);
        paper.setGraphic(p);

        Image scissorsPic = new Image("Scissors.jpg");
        ImageView scissor = new ImageView(scissorsPic);
        scissor.setFitHeight(90);
        scissor.setFitWidth(90);
        scissor.setPreserveRatio(true);
        scissors.setGraphic(scissor);

        Image lizardPic = new Image("Lizard.png");
        ImageView l = new ImageView(lizardPic);
        l.setFitHeight(90);
        l.setFitWidth(90);
        l.setPreserveRatio(true);
        lizard.setGraphic(l);

        Image spockPic = new Image("Spock.jpg");
        ImageView s = new ImageView(spockPic);
        s.setFitHeight(90);
        s.setFitWidth(90);
        s.setPreserveRatio(true);
        spock.setGraphic(s);

        quit = new Button("Quit");

        root = new BorderPane();
        root.setPadding(new Insets(40));

        VBox setPort = new VBox(5, Enterport, portNumber);
        VBox setIPAdd = new VBox(5, EnterIPAdd, IPAddress);
        VBox setName = new VBox(5, EnterName, name);

        HBox align= new HBox(80, setPort, setIPAdd, setName);
        root.setTop(align);

        HBox game = new HBox(10, rock, paper, scissors, lizard, spock);
        HBox info = new HBox(60, opponentPlayed, oppName, winner);

        VBox v = new VBox(13, connectServer, playerName, choice, game, info, answer);
        root.setCenter(v);
        v.setAlignment(Pos.TOP_CENTER);

        HBox challengeInfo = new HBox(20, EnterOpponentName, opponentName, challenge);
        HBox acceptChallenge = new HBox(20, question, accept, decline);
        HBox play = new HBox(20, quit);
        VBox messages = new VBox(13, communication, challengeInfo, acceptChallenge, play);
        root.setBottom(messages);
        play.setAlignment(Pos.BOTTOM_RIGHT);

        question.setDisable(true);
        accept.setDisable(true);
        decline.setDisable(true);

        name.setOnAction(event -> {
            clientName = name.getText();
            playerName.setText(clientName);
            playerName.setFont(new Font("Ariel", 20));
            connection.send(clientName);
        });

        connectServer.setOnAction(event -> {
            try{
                portNum = Integer.parseInt(portNumber.getText());
                hostName = IPAddress.getText();

                connection = new NetworkConnection(hostName, portNum, data->{
                    Platform.runLater(()-> {

                        if((data.toString().equals("Rock")) || (data.toString().equals("Paper")) || (data.toString().equals("Scissors")) ||
                                (data.toString().equals("Lizard")) || (data.toString().equals("Spock"))) {
                            opponentPlayed.setText("Opponent Played: " + data);
                        }

                        if(data.toString().startsWith("Opponent")) {
                            oppName.setText(data.toString());
                        }

                        if(data.toString().startsWith("Winner:")) {
                            winner.setText(data.toString());
                            rock.setDisable(true);
                            paper.setDisable(true);
                            scissors.setDisable(true);
                            lizard.setDisable(true);
                            spock.setDisable(true);
                            answer.setText("Game Finished!!! CHALLENGE again to play else select QUIT to exit.");
                            connection.send("REMOVE me from currently in play array(Pairs)");
                        }

                        if(data.toString().startsWith("Players Online: \n")) {
                            communication.setText(data.toString());
                        }

                        if(data.toString().startsWith("Do")) {
                            question.setText(data.toString());
                            question.setDisable(false);
                            accept.setDisable(false);
                            decline.setDisable(false);
                        }

                        if(data.toString().startsWith("***")) {
                            answer.setText(data.toString());
                        }
                        if(data.toString().startsWith("###")) {
                            answer.setText(data.toString());
                            rock.setDisable(false);
                            paper.setDisable(false);
                            scissors.setDisable(false);
                            lizard.setDisable(false);
                            spock.setDisable(false);
                        }
                    });

                });

                portNumber.clear();
                IPAddress.clear();
                connection.startConn();
            }
            catch(Exception e) {
                System.out.println("Enter the correct port and ip address.");
            }
        });

        rock.setOnAction(event-> {
            connection.send("Rock");
            paper.setDisable(true);
            scissors.setDisable(true);
            lizard.setDisable(true);
            spock.setDisable(true);
        });

        paper.setOnAction(event-> {
            connection.send("Paper");
            rock.setDisable(true);
            scissors.setDisable(true);
            lizard.setDisable(true);
            spock.setDisable(true);
        });

        scissors.setOnAction(event-> {
            connection.send("Scissors");
            rock.setDisable(true);
            paper.setDisable(true);
            lizard.setDisable(true);
            spock.setDisable(true);
        });

        lizard.setOnAction(event-> {
            connection.send("Lizard");
            rock.setDisable(true);
            paper.setDisable(true);
            scissors.setDisable(true);
            spock.setDisable(true);
        });

        spock.setOnAction(event-> {
            connection.send("Spock");
            rock.setDisable(true);
            paper.setDisable(true);
            scissors.setDisable(true);
            lizard.setDisable(true);
        });

        challenge.setOnAction(event-> {
            if(opponentName.getText().equals(playerName.getText())){
                answer.setText("*** You cannot challenge yourself. Enter another player name. ***");
            }
            else {
                connection.send(opponentName.getText());
            }
        });

        accept.setOnAction(event-> {
            connection.send("Accepted");
            question.setDisable(true);
            accept.setDisable(true);
            decline.setDisable(true);
            rock.setDisable(false);
            paper.setDisable(false);
            scissors.setDisable(false);
            lizard.setDisable(false);
            spock.setDisable(false);
        });

        decline.setOnAction(event -> {
            connection.send("Declined");
            question.setDisable(true);
            accept.setDisable(true);
            decline.setDisable(true);
        });

        quit.setOnAction(event-> {
            connection.send("quit");
            isSet = false;
            connection.closeConn();         //Closes connection and ends client GUI.
            System.exit(0);
        });

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(createContent(), 650, 585));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception{
        connection.send("quit");
        connection.closeConn();
    }
}

