import java.util.ArrayList;

public class TimeManager {
    static boolean instantiated = false;

    public float real_delta_time;
    public float real_time;
    public float game_delta_time;
    public float game_time;
    /**
     * Multipler for time (>= 0). Used to slowdown or speedup gameplay.
     */
    public float time_scale;

    private ArrayList<Timer> timers;

    public TimeManager() {
        if (instantiated) {
            System.err.println("TimeManager should only be initialized once.");
        }
        instantiated = true;
        real_delta_time = 0;
        real_time = System.currentTimeMillis();
        game_delta_time = 0;
        game_time = 0;
        time_scale = 1;
        timers = new ArrayList<Timer>();
    }

    public void update() {
        float old_real_time = real_time;
        real_time = System.currentTimeMillis();
        real_delta_time = real_time - old_real_time;

        game_delta_time  = real_delta_time * time_scale;
        game_time += game_delta_time;

        for(Timer timer: timers) {
            timer.update();
        }
    }

    public void addTimer(Timer timer) {
        timers.add(timer);
    }

    public void removeTimer(Timer timer_to_remove) {
        if(!timers.remove(timer_to_remove)) {
            System.err.println("Timer not in TimerManager's list of timers.");
        }
    }
}
