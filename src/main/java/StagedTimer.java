import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class StagedTimer extends Timer {

    SortedMap<Float, String> stages;

    /**
     *
     * @param names
     * @param maxTimes The timestamp for when this stage ends (e.g., if 0, stage takes 1 frame)
     * @param absolute_times if the maxTimes are absolute (e.g. 1, 2, 4, 5, 6) or are lengths (e.g. 1, 1, 2, 1, 1)
     */
    public StagedTimer(ArrayList<String> names, ArrayList<Float> maxTimes, boolean absolute_times) {
        super(0);
        if (names.size() != maxTimes.size()) {
            Game.get().print_manager.printlnError("StagedTimer constructor: names and maxTimes are not the same length");
            return;
        }

        stages = new TreeMap<Float, String>();

        if (absolute_times) {
            float largest_timestamp = 0;

            // Add directly
            for (int i = 0; i < names.size(); i++) {
                stages.put(maxTimes.get(i), names.get(i));

                if (maxTimes.get(i) > largest_timestamp) {
                    largest_timestamp = maxTimes.get(i);
                }
            }

        } else {
            float curr_time = 0;

            // Calculate the absolute times and add
            for (int i = 0; i < names.size(); i++) {
                // Add the previous absolute time to this relative time
                curr_time = curr_time + maxTimes.get(i);
                stages.put(curr_time, names.get(i));
            }
            // Set the total time of the timer to get the timestamp of the last one
            setTotalTime(curr_time);
        }
    }

    /**
     * Get the stage the timer is currently within
     * @return
     */
    public String getStage() {
        // Get the first time in the future
        return stages.get(stages.tailMap(timer).firstKey());
    }
}
