import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AudioClips must be removed in update and then played in draw to avoid the scenario that
 * the clips are not removed because a different instance of AudioComponent played the same clip again.
 * This would be bad because our instance of the playing clip is over, and if we wanted to stop
 * the clip, we would actually be stopping a different AudioComponent's clip.
 * Additionally, we reserve update() for starting and stopping audio clips because if the audio clip
 * is removed after playing and we try to stop it, it will no longer be playing
 */
public class AudioComponent extends Component {
    private Map<String, AudioClip> playing_clips;
    private ArrayList<AudioTuple> audio_queue;

    public AudioComponent(String audio_filename) {
        super();
        playing_clips = new HashMap<String, AudioClip>();
        audio_queue = new ArrayList<AudioTuple>();

        registerAudio(audio_filename);
    }

    public AudioComponent(String audio_filename1, String audio_filename2) {
        super();
        playing_clips = new HashMap<String, AudioClip>();
        audio_queue = new ArrayList<AudioTuple>();

        registerAudio(audio_filename1);
        registerAudio(audio_filename2);
    }

    public AudioComponent(String audio_filename1, String audio_filename2, String audio_filename3) {
        super();
        playing_clips = new HashMap<String, AudioClip>();
        audio_queue = new ArrayList<AudioTuple>();

        registerAudio(audio_filename1);
        registerAudio(audio_filename2);
        registerAudio(audio_filename3);
    }

    public void update(GameObject parent) {
        // Remove AudioClips that are no longer playing (they are reused for other objects)
        for(String filename: playing_clips.keySet()) {
            AudioClip clip = playing_clips.get(filename);
            if (!clip.isActive()) {
                playing_clips.remove(filename);
            }
        }
    }

    public void lateUpdate(GameObject parent) {

    }

    public void draw(GameObject parent, Graphics g) {
        // Play all enqueued audio files.
        for(AudioTuple audio_tuple: audio_queue) {

            // For each enqueued audio file, ask the AudioManager to play the file
            AudioClip clip = Game.get().audio_manager.playFile(audio_tuple.filename, audio_tuple.loop);

            // Add resulting AudioClip to the playing_clips if not null (audio file was not previously registered)
            if(clip != null) {
                playing_clips.put(audio_tuple.filename, clip);
            }
        }
    }

    public void registerAudio(String filename) {
        Game.get().audio_manager.registerAudio(filename);
    }

    public void playAudio(String filename, boolean loop) {
        audio_queue.add(new AudioTuple(filename, loop));
    }

    /**
     * Must be called from lateUpdate()
     * @param filename
     */
    public boolean stopAudio(String filename) {
        AudioClip clip = playing_clips.get(filename);
        if (clip != null) {
            clip.stop();
            playing_clips.remove(filename);
            return true;
        }
        return false;
    }

    /**
     * Must be called from lateUpdate()
     * @param filename
     */
    public boolean pauseAudio(String filename) {
        AudioClip clip = playing_clips.get(filename);
        if (clip != null) {
            clip.pause();
            return true;
        }
        return false;
    }

    /**
     * Must be called from lateUpdate()
     * @param filename
     */
    public boolean resumeAudio(String filename, boolean loop) {
        AudioClip clip = playing_clips.get(filename);
        if (clip != null) {
            clip.play(loop);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AudioComponent{" +
                "playing_clips=" + playing_clips +
                ", audio_queue=" + audio_queue +
                ", containers=" + containers +
                '}';
    }


    private class AudioTuple {
        private String filename;
        private boolean loop;

        private AudioTuple(String filename, boolean loop) {
            this.filename = filename;
            this.loop = loop;
        }
    }
}
