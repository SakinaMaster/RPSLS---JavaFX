
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

class NetworkConnection {
    public static int numClients;
    private int port;
    public ArrayList<ClientSocket> clientList = new ArrayList<>(); //Stores all clients
    public ArrayList<String> clientNames = new ArrayList<>();       //Holds all the client names that are connected to server.
    private ConnThread connThread = new ConnThread();
    public ArrayList<ArrayList<ClientSocket>> Pairs = new ArrayList<>();  //Holds the players that challenged each other.
    public ArrayList<ClientSocket> twoPlayers; //= new ArrayList<>();
    //public ArrayList<twoPlayers[]> Pairs = new ArrayList<>();
    //Consumer<T> is an in-built functional interface. It can be used when an object is taken as
    //an input and some operation is to be performed on the object without returning
    //any result. Example: PRINTING where an object is taken as an input to the printing function and the
    //value of the object is printed.
    //accept() is the primary abstract method of the Consumer functional interface.

    //Function that allows us to pass in a function that is going to be called when
    //we receive message from the other end.
    private Consumer<Serializable> callback;

    NetworkConnection(int port, Consumer<Serializable> callback) {
        this.port = port;
        this.callback = callback;
        numClients = 0;
        connThread.setDaemon(true);
    }

    public int getPort() {
        return this.port;
    }


    public void startConn() {
        connThread.start();
    }

    public void closeConn() {
        try {
            for (ClientSocket clients : clientList) {
                clients.getClientSocket().close();
            }
        }
        catch (IOException e)
        {
            System.out.println("Error in closing client sockets.");
        }
    }

    //Decides the winner and returns a value accordingly.
    public int playGame(String player1, String player2)
    {
        //Returns 1 if player1 wins, returns 2 if player2 wins, returns 3 if its a draw.

              if ((player1.equals("Scissors") ) && (player2.equals("Paper")) ) {
                    return 1;
                } else if ((player1.equals("Paper") ) && (player2.equals("Rock") )) {
                    return 1;
                } else if ((player1.equals("Rock") ) && (player2.equals("Lizard") )) {
                    return 1;
                } else if ((player1.equals("Lizard") ) && (player2.equals("Spock") )) {
                    return 1;
                } else if ((player1.equals("Spock") ) && (player2.equals("Scissors") )) {
                    return 1;
                } else if ((player1.equals("Scissors") ) && (player2.equals("Lizard") )) {
                    return 1;
                } else if ((player1.equals("Lizard") ) && (player2.equals("Paper") )) {
                    return 1;
                } else if ((player1.equals("Paper") ) && (player2.equals("Spock") )) {
                    return 1;
                } else if ((player1.equals("Spock") ) && (player2.equals("Rock") )) {
                    return 1;
                } else if ((player1.equals("Rock") ) && (player2.equals("Scissors") )) {
                    return 1;
                } else if ((player1.equals("Paper") ) && (player2.equals("Scissors") )) {
                    return 2;
                } else if ((player1.equals("Rock") ) && (player2.equals("Paper") )) {
                    return 2;
                } else if ((player1.equals("Lizard") ) && (player2.equals("Rock") )) {
                    return 2;
                } else if ((player1.equals("Spock") ) && (player2.equals("Lizard") )) {
                    return 2;
                } else if ((player1.equals("Scissors") ) && (player2.equals("Spock") )) {
                    return 2;
                } else if ((player1.equals("Lizard") ) && (player2.equals("Scissors") )) {
                    return 2;
                } else if ((player1.equals("Paper") ) && (player2.equals("Lizard") )) {
                    return 2;
                } else if ((player1.equals("Spock") ) && (player2.equals("Paper") )) {
                    return 2;
                } else if ((player1.equals("Rock") ) && (player2.equals("Spock") )) {
                    return 2;
                } else if ((player1.equals("Scissors") ) && (player2.equals("Rock") )) {
                    return 2;
                }

           // }
      //  }
        return 3;
    }

    //Inner class which extends Thread.
    class ConnThread extends Thread {

        private ObjectOutputStream out;
        private Socket socket;
        private ObjectInputStream in;

        public void run() {

            try (
                    ServerSocket listener = new ServerSocket(getPort()))    //listener always keeps listening to see if client wants to connect.
            {
                while (true) {
                    Socket socket = listener.accept();
                    this.socket = socket;

                    ClientSocket secondaryThread = new ClientSocket(socket);
                    clientList.add(secondaryThread);    //The client connected is added to the ArrayList clientList.
                    secondaryThread.start();
                    this.out = secondaryThread.getOut();
                    this.in = secondaryThread.getIn();
                }
            }
            catch (IOException e) {
                callback.accept("Connection Closed");
            }
        }
    }

    class ClientSocket extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Serializable data;
        private String clientName;
        private Boolean isName = false;
        private String clientPlay = "nothing";
        private Boolean nameIsUnique = false;

        ClientSocket( Socket clientSocket) {
            this.clientSocket = clientSocket;
            numClients++;
        }

        public synchronized void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());    //send objects
                    //ObjectInputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()))       //receive objects
            {
                this.in = in;
                this.out = out;

                //This is what we receive from the other end.
                while (true) {
                    data = (Serializable) in.readObject();  //Reads data sent from client.

                    if(isName) {
                        Boolean inPlay1 = false;
                        Boolean inPlay2 = false;
                        //If a player challenges, both the player who challenged and the player who was challenged are added in twoPlayers ArrayList.
                        for (int i = 0; i < clientList.size(); i++) {
                            if (data.toString().equals(clientList.get(i).getClientName())) {
                                for(int j =0; j < Pairs.size(); j++) {
                                    for (int k = 0; k < Pairs.get(j).size(); k++) {
                                        if(Pairs.get(j).get(k).getClientName().equals(clientList.get(i).getClientName())) {
                                            this.sendMsg("*** Oops, " + clientList.get(i).getClientName() + " is currently playing. Enter another player name. ***");
                                            inPlay1 = true;
                                        }
                                        if(Pairs.get(j).get(k).getClientName().equals(this.getClientName())) {
                                            this.sendMsg("*** Oops, you are currently in a game. Challenge later. ***");
                                            inPlay2 = true;
                                        }

                                    }
                                }
                                if((!inPlay1) && (!inPlay2)) {
                                    twoPlayers = new ArrayList<>();
                                    twoPlayers.add(0, this);
                                    clientList.get(i).sendMsg("Do you want to accept " + this.getClientName() + "'s challenge? ");
                                }
                            }
                        }
                    }

                    if(data.toString().equals("Accepted")) {
                        twoPlayers.add(1, this);
                        Pairs.add(twoPlayers);
                        twoPlayers.get(0).sendMsg("### " + twoPlayers.get(1).getClientName() + " accepted your challenge. ###");
                        twoPlayers.get(1).sendMsg("*** You accepted to play against " + twoPlayers.get(0).getClientName());
                        twoPlayers.get(0).sendMsg("Opponent Name: " + twoPlayers.get(1).getClientName());
                        twoPlayers.get(1).sendMsg("Opponent Name: " + twoPlayers.get(0).getClientName());
                    }
                    if(data.toString().equals("Declined")) {
                        twoPlayers.get(0).sendMsg("*** " + this.getClientName() + " denied your challenge. ***");
                        twoPlayers.remove(0);
                    }

                    //the client name is added to clientNames ArrayList.
                    if(!isName)
                   {
                /*     do{
                         if(clientNames.size() == 0) {
                             nameIsUnique = true;
                         }

                         outer:
                         for (int i = 0; i < clientNames.size(); i++) {
                             System.out.println("The value of i" + i);
                             if (clientNames.get(i).equals(data.toString())) {
                                 this.sendMsg("*** The name is already taken. Enter a unique name. ***");
                                 //break;

                                 data = (Serializable) in.readObject();
                                 i=-1;
                                 continue outer;
                             }
                             nameIsUnique = true;
                             System.out.println("!1");
                         }
                     }while(!nameIsUnique);
*/
                     System.out.println("!2");
                     this.clientName = data.toString();
                        clientNames.add(data.toString());
                        callback.accept(clientNames);
                        isName = true;

                    }


                    if(data.toString().equals("quit"))
                    {
                        clientNames.remove(this.clientName);
                        twoPlayers.remove(this);
                        clientList.remove(this);
                        callback.accept(clientNames);
                        closeSocket();  //If client send "quit", that client socket is closed and removed from ArrayList.
                        setData();
                    }

                    if((data.toString().equals("Rock")) || (data.toString().equals("Paper")) || (data.toString().equals("Scissors")) ||
                            (data.toString().equals("Lizard")) || (data.toString().equals("Spock"))) {

                        this.clientPlay = data.toString();
                    }
                    callback.accept(data);

                }
            }

            catch(ClassNotFoundException e)
            {
                System.out.println("Class not found exception.");
            }
            catch (IOException e) {
                System.out.println("Connection closed");
            }
            catch (NullPointerException e)
            {
                System.out.println("Connection closed.");
            }
        }

        public void closeSocket() {
            try {
                //Closes all streams, socket and removes the clientSocket from ArrayList.
                this.out.close();
                this.in.close();
                this.getClientSocket().close();
                numClients--;
            }
            catch (IOException e) {
                System.out.println("In close socket");
            }
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public void setData() {
            this.data = null;
        }

        public void setClientPlay() {
            this.clientPlay = "nothing";
        }
        public Serializable getData()
        {
            return data;
        }

        public String getClientName()
        {
            return this.clientName;
        }
        public String getClientPlay()
        {
            return this.clientPlay;
        }

        public ObjectOutputStream getOut()
        {
            return this.out;
        }

        public ObjectInputStream getIn()
        {
            return this.in;
        }

        public void sendMsg(Serializable msg) {
            try {
                out.writeObject(msg);   //Sends msg to client through "out" stream.
            }
            catch (IOException e) {
                System.out.println("Error in sending Server's message.");
            }
        }
    }

}

