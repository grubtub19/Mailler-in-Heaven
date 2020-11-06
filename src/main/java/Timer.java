/**
 * Setting a timer with time = 0 means the timer will be active for exactly one frame.
 */
public class Timer {
    /**
     * The starting time of the countdown timer.
     */
    float totalTime;
    /**
     * The current time on the timer. Starts at totalTime and counts down to 0.
     */
    float timer;
    /**
     * if the timer is ticking. When not active, it's considered finished. Only 2 states.
     */
    boolean active;
    /**
     * Whether the timer is based on game time or real time.
     */
    boolean real_time;

    public Timer(float totalTime) {
        this.totalTime = totalTime;
        this.real_time = false;
        Game.get().time_manager.addTimer(this);
    }

    public Timer(float totalTime, boolean real_time) {
        this.totalTime = totalTime;
        this.real_time = real_time;
        Game.get().time_manager.addTimer(this);
    }

    public void restart() {
        active = true;
        timer = 0;
    }

    public void stop() {
        active = false;
        timer = 0;
    }

    public void cont() {
        active = true;
    }

    public void pause() {
        active = false;
    }

    /**
     * DO NOT CALL YOURSELF. This is called by the Time Manager at the beginning of every frame and updates the time information.
     */
    public void update() {
        if(active) {
            // Update the time
            if(real_time) {
                timer -= Game.get().time_manager.real_delta_time;
            } else {
                timer -= Game.get().time_manager.game_delta_time;
            }

            // Check for timeouts
            if(timer >= totalTime) {
                active = false;
                timer = totalTime;
            }
        }
    }

    /**
     * The timer is considered finished if it is not active.
     * @return boolean if the timer is no longer active
     */
    public boolean isFinished() {
        return !active;
    }

    /**
     * The timer is ticking if it is active
     * @return boolean if the timer is active
     */
    public boolean isActive() {
        return active;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime =  totalTime;
    }
}
