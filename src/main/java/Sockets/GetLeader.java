package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GetLeader {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("ServerSocket awaiting connections...");
        while(true) {
            try {
                File f = new File("zookeeper.txt");
                FileReader fr = new FileReader(f);
                BufferedReader br  = new BufferedReader(fr);
                String[] leaderIps = br.readLine().split(" pop ",2);
                Socket socket = ss.accept();
                System.out.println("Connection from " + socket + "!");
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                System.out.println("Sending string to the ServerSocket");
                String leaderIp = leaderIps[0];
                br.close();
                if (leaderIp == null)
                    leaderIp = "No Ip Set";
                dataOutputStream.writeUTF(leaderIp);
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception e) {
                System.out.println("Closing socket and terminating program.");
                ss.close();
            }
        }
    }
}
