import java.util.Date;

public interface IGenerator extends ISandbox {
    Date getStartTime();
    boolean isOn();
    int getCount();
    void start();
    void end();
    interface OnArriveListener {
        void onArrive();
    }
    void addOnArriveListener(OnArriveListener listener);
    void removeOnArriveListener(OnArriveListener listener);
}
