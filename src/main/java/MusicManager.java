public class MusicManager {
    private AudioClip hell;					//Raining Blood - Slayer
    private AudioClip happy;					//Some upbeat song
    private AudioClip loser;
    private SongTitle currentSong = SongTitle.NONE;

    public MusicManager() {
        hell = new AudioClip("hell.wav");
        happy = new AudioClip("happy.wav");
        loser = new AudioClip("loser.wav");
    }
    public MusicManager(SongTitle initalSong) {
        hell = new AudioClip("hell.wav");
        happy = new AudioClip("happy.wav");
        loser = new AudioClip("loser.wav");
        changeSong(initalSong, true, false);
    }



    /**
     * Pauses the Current Playing Song
     * @return false if no song was playing
     */

    public boolean pauseCurrentSong() {
        switch (currentSong) {
            case HELL:
                hell.pause();
                //System.out.println("Stopped Hell");
                break;
            case HAPPY:
                happy.pause();
                //System.out.println("Stopped Happy");
                break;
            case LOSER:
                loser.pause();
                break;
            case NONE:
                return false;
        }
        currentSong = SongTitle.NONE;
        return true;
    }

    public void changeSong(SongTitle songTitle, boolean loop, boolean resume) {
        //System.out.println("changeSong(" + songTitle + ", " + loop + ", " + resume + ")");
        //System.out.println("currentSong = " + currentSong);
        if(songTitle == currentSong) {
            return;
        } else {
            pauseCurrentSong();
            currentSong = songTitle;
            //System.out.println("Changing currentSong = " + currentSong);
        }
        switch (songTitle) {
            case HELL:
                if(!resume) {
                    hell.stop();
                    //System.out.println("Stopping Hell");
                }
                hell.play(loop);
                //System.out.println("Playing Hell");
                break;
            case HAPPY:
                if(!resume) {
                    happy.stop();
                    //System.out.println("Stopping Happy");
                }
                happy.play(loop);
                //System.out.println("Playing Happy");
                break;
            case LOSER:
                if(!resume) {
                    loser.stop();
                    //System.out.println("Stopping Happy");
                }
                loser.play(loop);
                //System.out.println("Playing Happy");
                break;
        }
    }
}
