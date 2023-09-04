import java.time.LocalDateTime;

public interface IGenerator extends ISandbox {
    LocalDateTime getStartTime();
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
