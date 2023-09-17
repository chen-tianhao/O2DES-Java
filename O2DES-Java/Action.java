import java.util.ArrayList;
import java.util.List;


public class Action implements Runnable
{
    // for multicast delegation
    private List<Callback> callbacks = new ArrayList<>();

    public void register(Callback callback) {
        callbacks.add(callback);
    }

    public void register(Action action) {
        callbacks.addAll(action.callbacks);
    }

    public void invoke() {
        System.out.println("Event occurred!");
        callbacks.forEach(Callback::callback);
    }

    @Override
    public void run() {

    }
}