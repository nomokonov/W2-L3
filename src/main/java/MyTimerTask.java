import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    @Override
    public void run() {
        System.out.println("count_mess = " + Main.count_mess);
    }


    }