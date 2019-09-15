import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkConnection {
    private String ip;
    private int port;
    private ConnThread connthread = new ConnThread();
    private Consumer<Serializable> callback;

    public NetworkConnection(String ip, int port, Consumer<Serializable> callback) {
        this.ip = ip;
        this.port = port;
        this.callback = callback;
        connthread.setDaemon(true);
    }

    public String getIP()
    {
        return this.ip;
    }

    public int getPort()
    {
        return this.port;
    }

    public void startConn() throws Exception{
        connthread.start();
    }

    public void send(Serializable data){
        try {
            connthread.out.writeObject(data);
        }
        catch (IOException e){
            System.out.println("Client's message not sent.");
        }
    }

    public void closeConn(){
        try {
            connthread.socket.close();
        }
        catch(IOException e) {
            System.out.println("Exception in closing connection.");
        }
    }

    class ConnThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;
        private Serializable data;

        public synchronized void run() {
            try(
                    Socket socket = new Socket(getIP(), getPort());
                    ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream( socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while(true) {
                    Serializable data = (Serializable) in.readObject();
                    this.data = data;
                    callback.accept(data);
                }
            }
            catch(Exception e) {
                callback.accept("connection Closed");
            }

        }
    }
}

