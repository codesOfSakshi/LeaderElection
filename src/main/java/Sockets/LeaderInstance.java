package Sockets;

public class LeaderInstance {

    private static LeaderInstance leaderInstance = null;
    private static String LeaderIpAddress;



    public static LeaderInstance getInstance()
    {
        if(leaderInstance==null)
       leaderInstance = new LeaderInstance();

        return leaderInstance;

    }

    public static void triggerRelection() {
        String newLeader=(String) Server.BrokerIps.get(0);
        setLeaderIp((String) Server.BrokerIps.get(0));
    }

    public static void setLeaderIp(String Ip)
    {
        LeaderIpAddress= Ip;
        System.out.println("----------------------");
        System.out.println("The new leader is : "+Ip);
        System.out.println("----------------------");
    }

    public  String getLeaderIp()
    {
        return LeaderIpAddress;
    }

}
