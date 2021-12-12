package Sockets;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;


public class Server {
    public static final String SERVER_RECEIVED_MESSAGE_S = "[Server] Received message %s\n";
    private Socket _socket;
    private ServerSocket _server;
    private final int _port;
    private DataInputStream _input;
    private DataOutputStream _output;
    private Timer _timer;
    public static ArrayList<String> BrokerIps= new ArrayList();

    boolean SendHeartbeat()
    {
        return true;
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
        BrokerIps.add("1.2.3.4");
        BrokerIps.add("5.6.3.4");
        boolean flag=false;
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
                Sockets.LeaderInstance.setLeaderIp(clientIpAddress);
                flag=true;
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
//            server.ReceiveData();
            socket.close();
        }

    }

}
