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

    public   void setLeaderIp(String Ip)
    {
        LeaderIpAddress= Ip;
    }

    public  String getLeaderIp()
    {
        return LeaderIpAddress;
    }

}
