import java.util.Random;
import java.util.function.Function;

public class Generator extends Sandbox<Generator.Statics> implements IGenerator {

    public static class Statics implements IAssets {
        @Override
        public String getId() {
            return getClass().getSimpleName();
        }

        public Function<Random, TimeSpan> getInterArrivalTime() {
            return interArrivalTime;
        }

        public void setInterArrivalTime(Function<Random, TimeSpan> interArrivalTime) {
            this.interArrivalTime = interArrivalTime;
        }

        public Generator sandbox(int seed) {
            return new Generator(this, seed);
        }
    }

    // Dynamic Properties
    private DateTime startTime;
    private boolean isOn;
    private int count;

    // Constructor
    public Generator(Statics assets, int seed, String id) {
        super(assets, seed, id);
        isOn = false;
        count = 0;
    }

    // Getters for Dynamic Properties
    public DateTime getStartTime() {
        return startTime;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getCount() {
        return count;
    }

    // Events
    public void start() {
        if (!isOn) {
            log("Start");
            if (isDebugMode()) {
                System.out.println(getClockTime() + ":\t" + this + "\tStart");
            }
            if (getAssets().getInterArrivalTime() == null) {
                throw new Exception("Inter-arrival time is null");
            }
            isOn = true;
            startTime = getClockTime();
            count = 0;
            scheduleToArrive();
        }
    }

    public void end() {
        if (isOn) {
            log("End");
            if (isDebugMode()) {
                System.out.println(getClockTime() + ":\t" + this + "\tEnd");
            }
            isOn = false;
        }
    }

    private void scheduleToArrive() {
        schedule(this::arrive, getAssets().getInterArrivalTime().apply(getDefaultRS()));
    }

    private void arrive() {
        if (isOn) {
            log("Arrive");
            if (isDebugMode()) {
                System.out.println(getClockTime() + ":\t" + this + "\tArrive");
            }

            count++;
            scheduleToArrive();
            onArrive.invoke();
        }
    }

    // Event Listener
    private Action onArrive = () -> {};

    public Generator(Statics assets, int seed) {
        this(assets, seed, null);
    }

    @Override
    protected void warmedUpHandler() {
        count = 0;
    }

    @Override
    public void dispose() {
        for (Action action : onArrive.getInvocationList()) {
            onArrive.remove(action);
        }
    }
}
