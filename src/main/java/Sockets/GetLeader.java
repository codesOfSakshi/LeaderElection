package Sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GetLeader {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("ServerSocket awaiting connections...");
        Socket socket = ss.accept();
        System.out.println("Connection from " + socket + "!");
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        System.out.println("Sending string to the ServerSocket");
        String leaderIp = Sockets.LeaderInstance.getInstance().getLeaderIp();
        if(leaderIp==null)
              leaderIp="No Ip Set";
        dataOutputStream.writeUTF(leaderIp);
        dataOutputStream.flush();
        dataOutputStream.close();
        System.out.println("Closing socket and terminating program.");
        ss.close();
        socket.close();

    }
}
