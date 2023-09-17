import java.util.ArrayList;
import java.util.List;


public class Action
{
    // for multicast delegation
    private List<Callback> callbacks = new ArrayList<>();

    public void register(Callback callback) {
        callbacks.add(callback);
    }

    public void invoke() {
        System.out.println("Event occurred!");
        callbacks.forEach(Callback::callback);
    }
}