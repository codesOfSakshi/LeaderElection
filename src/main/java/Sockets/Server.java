package Sockets;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;


public class Server {
    public static final String SERVER_RECEIVED_MESSAGE_S = "[Server] Received message %s\n";
    private Socket _socket;
    private ServerSocket _server;
    private final int _port;
    private DataInputStream _input;
    private DataOutputStream _output;
    private Timer _timer;
    public static ArrayList BrokerIps= new ArrayList();
    public static String leaderIp;
    boolean flag=false;

    boolean SendHeartbeat()
    {
        return true;
    }


    public static void triggerRelection() throws IOException {
        leaderIp=(String) BrokerIps.get(0);
        System.out.println("----------------------");
        System.out.println("Leader Broker : "+ leaderIp);
        System.out.println("----------------------");
        updatePropertyFile();
    }

    public static void writeLeader(String leader){
        leaderIp = leader;
    }

    public static void updatePropertyFile() throws IOException {
        String mycontent = leaderIp +" pop "+ BrokerIps.toString();
        //Specify the file name and path here
        File file = new File("zookeeper.txt");
        System.out.println("creating new file");

        if (!file.exists()) {
            file.createNewFile();
            System.out.println("creating new file");
        }

        System.out.println("creating new file");

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(mycontent);
        bw.close();
    }

    void ReceiveData() throws IOException {
        String in = "";
        InetSocketAddress socketAddress = (InetSocketAddress) this._socket.getRemoteSocketAddress();
        String clientIpAddress = socketAddress.getAddress().getHostAddress();
        while(!"end".equals(in))
        {
            in = this._input.readUTF();
            System.out.printf(SERVER_RECEIVED_MESSAGE_S, in);
        }
        System.out.print("[Server] Connection Closed\n");
        this._socket.close();
        this._input.close();
    }



    public Server(int port) throws IOException {
        this._server = new ServerSocket(port);
        System.out.print("[Server] Waiting for client...\n");
        while(true) {
            this._socket = this._server.accept();
            //Broker gets itself registered with the server
            InetSocketAddress socketAddress = (InetSocketAddress) this._socket.getRemoteSocketAddress();
            String clientIpAddress = socketAddress.getAddress().getHostAddress();
            BrokerIps.add(clientIpAddress);
            System.out.println("----------------------");
            System.out.println("New broker has registered itself : "+ BrokerIps);
            System.out.println("----------------------");
            if(flag==false){
                leaderIp=clientIpAddress;
                System.out.println("----------------------");
                System.out.println("Leader Broker : "+ leaderIp);
                System.out.println("----------------------");
                flag=true;
                updatePropertyFile();
            }
            new Thread(new Task(this._socket)).start();

        }


    }


    @Override
    public String toString() {
        return "Server{" +
                "_socket=" + _socket +
                ", _server=" + _server +
                ", _port=" + _port +
                ", _input=" + _input +
                ", _output=" + _output +
                '}';
    }
    static class Task implements Runnable {

        private Socket socket;
        private DataInputStream _input;
        private DataOutputStream _output;
        Server server;
        /**
         * constructor
         */
        public Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                handlerSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         *
         *communicate with client socket
         * @throws IOException
         */
        private void handlerSocket() throws Exception {
            this._input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this._output = new DataOutputStream(socket.getOutputStream());
            Heartbeat heartbeat = new Heartbeat(socket, _output, _input);
            heartbeat.start();
            server.ReceiveData();
            socket.close();
        }

    }

}
