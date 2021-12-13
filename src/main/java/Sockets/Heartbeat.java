package Sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static Sockets.Server.triggerRelection;


public class Heartbeat extends Thread {
    private Socket _socket = null;
    private DataOutputStream _output = null;
    private DataInputStream _input = null;
    private float _lastReceived = 0.0f;
    private float _nextHeartbeat = 0.0f;
    private boolean _isAlive = true;
    private Timer _timer = null;
    String offset=null;

    static final int MINUTE = 5 * 1000; // 1 second in ms * 60


    public void Receive()
    {

        InetSocketAddress socketAddress = (InetSocketAddress) _socket.getRemoteSocketAddress();
        String clientIpAddress = socketAddress.getAddress().getHostAddress();
        if(_input != null) {
            try {
                String in = "";

                    in = this._input.readUTF();
                    System.out.println(in);
                    if(clientIpAddress.equals(Server.leaderIp)) {
                        offset = in;
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TimerTask Send()
    {
        if(_output != null)
        {
            try {
                System.out.print("Sending heartbeat\n");
                _output.writeUTF("false"+" "+null);
                Receive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void run()
    {
        try {
            _timer = new Timer();
            TimerTask task = Send();
            InetSocketAddress socketAddress = (InetSocketAddress) _socket.getRemoteSocketAddress();
            String clientIpAddress = socketAddress.getAddress().getHostAddress();
            boolean flag=false;

            _timer.schedule(new TimerTask() {
                int c=-1;
                @Override
                public void run() {
                    if(_output != null)
                    {
                        try {
                            System.out.print("Sending heartbeat\n");
                            Boolean isLeader= Server.leaderIp.equals(clientIpAddress);
                            if(isLeader){
                                String flag = "true";
                            }
                            else{
                                String flag = "false";
                            }
                            _output.writeUTF(flag+" "+offset);
                            Receive();
                        } catch (IOException e) {
                            c+=1;
                            boolean flag=true;
                            System.out.println(clientIpAddress + " Did not respond to heart beat.");
                            if(c>2){

                                System.out.print("[Server] Connection Closed\n");
                                try {
                                    _socket.close();
                                    _input.close();
                                    _timer.cancel();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                System.out.println(clientIpAddress + " "+c+ " Hasn't responded to 3 consecutive hearbeats.Closing connection and declaring the Broker is dead.");
                                //removing the server from broker list
                                Sockets.Server.BrokerIps.remove(clientIpAddress);
                                //checking if the server that has gone down is the leader

                                if(Sockets.Server.leaderIp.equals(clientIpAddress)) {
                                    System.out.println("Re-election triggered");
                                    try {
                                        triggerRelection();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                else{
                                    System.out.println("Broker was not leader.Re-election not required");
                                }
                            }
                        }
                    }
                }
            }, 10*1000, 5*1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Heartbeat(Socket socket, DataOutputStream output, DataInputStream input )
    {
        _socket = socket;
        _output = output;
        _input = input;


        _nextHeartbeat = System.currentTimeMillis() + MINUTE;

    }

    public boolean IsAlive() {
        return _isAlive;
    }
}