public class LeaderInstance {

    private static LeaderInstance leaderInstance = null;
    private static String LeaderIpAddress;



    private LeaderInstance(String ip) {
        LeaderIpAddress = ip;
    }

    public static void setInstance(String Ip)
    {

       leaderInstance = new LeaderInstance(Ip);

    }
    public static String getInstance()
    {
        return LeaderIpAddress;
    }

}
