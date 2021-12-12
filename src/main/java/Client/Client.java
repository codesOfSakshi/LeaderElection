package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket _socket;
    private String _ip;
    private int _port;
    private DataInputStream _input;
    private DataInputStream _serverinput;
    private DataOutputStream _output;
    String offsets = "34";

    public Client(String ip, int port) {
        this._ip = ip;
        this._port = port;

        try {
            this._socket = new Socket(this._ip, this._port);
            System.out.print("Connected to server.\n");
            this._input = new DataInputStream(System.in);
            this._serverinput = new DataInputStream(this._socket.getInputStream());
            this._output = new DataOutputStream(this._socket.getOutputStream());
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        String in = "";

        while(!in.equals("end")) {
            try {
                String messages= this._serverinput.readUTF();
                System.out.println("messages --------------------------->"+messages);
                String[] message =messages.split(":");
                if (message[0].equals("heartbeat")) {
                    System.out.print("Received heartbeat\n");
                    if(message.length>1 &&message[1] != null)
                        offsets=message[1];
                    this._output.writeUTF(offsets);
                }
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        try {
            this._input.close();
            this._output.close();
            this._socket.close();
        } catch (IOException var5) {
            System.out.println(var5);
        }

    }
}