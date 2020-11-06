import sun.security.util.ArrayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static boolean instantiated = false;
    Map<String, ArrayList<AudioClip>> filename_to_audio;

    public AudioManager() {
        if (instantiated) {
            System.err.println("AudioManager should only be initialized once.");
        }
        instantiated = true;
        filename_to_audio = new HashMap<String, ArrayList<AudioClip>>();
    }

    /**
     * Adds this audio file
     * @param filename
     * @return boolean whether it was not already added yet (false == already added)
     */
    public void registerAudio(String filename) {
        // If the audiofile is not already registered
        if (!filename_to_audio.containsKey(filename)) {

            // Create the list of AudioClips
            ArrayList<AudioClip> list = new ArrayList<AudioClip>();

            // Add one AudioClip into it
            AudioClip audio = new AudioClip(filename);
            list.add(audio);

            // Put the list into the HashMap
            filename_to_audio.put(filename, list);
        } else {
            System.out.println(filename + " is already registered.");
        }
    }

    /**
     * Finds the first available AudioClip associated with this audio file and plays it.
     * If all AudioClips are busy playing, create a new one and add it to the list.
     * @param filename
     * @param loop Should the AudioClip repeat?
     * @return AudioClip or null (audio file not previously registered)
     */
    public AudioClip playFile(String filename, boolean loop) {
        // Find the audio file in our HashMap
        ArrayList<AudioClip> audio_list = filename_to_audio.get(filename);

        // If the audio file in found in our HashMap
        if (audio_list != null) {

            // For all the AudioClips for this audio file
            for(AudioClip clip: audio_list) {

                // Pick the first one that isn't already playing
                if (!clip.isActive()) {

                    // Play this audio file from the beginning
                    clip.stop();
                    clip.play(loop);
                    return clip;
                }
            }

            // If all AudioClips are already playing, add a new one to the list and start playing it
            AudioClip clip = new AudioClip(filename);
            clip.play(loop);
            audio_list.add(clip);
            return clip;
        } else {
            System.err.println("AudioManager.playFile(): Audio file, " + filename + ", not previously registered.");
            return null;
        }
    }
}
