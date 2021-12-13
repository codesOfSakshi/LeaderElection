package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GetBrokerlist {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(7778);
        System.out.println("ServerSocket awaiting connections...");

        while(true) {
            try {
                Socket socket = ss.accept();
                File f = new File("zookeeper.txt");
                FileReader fr = new FileReader(f);
                BufferedReader br  = new BufferedReader(fr);
                String[] leaderIps = br.readLine().split(" pop ",2);
                System.out.println("Connection from " + socket + "!");
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                System.out.println("Sending string to the ServerSocket");
                String leaderIp = leaderIps[1];
                if (leaderIp == null)
                    leaderIp = "No Ip Set";
                dataOutputStream.writeUTF(leaderIp.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception e) {
                System.out.println("Closing socket and terminating program.");
                ss.close();
            }
        }

    }
}
