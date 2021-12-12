public class LeaderSingleton {

    private static LeaderSingleton single_instance = null;


    public String s;
    private static String Ip;

    private LeaderSingleton(String ip) {
        s = ip;
    }

    public static LeaderSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new LeaderSingleton(Ip);

        return single_instance;
    }

}
