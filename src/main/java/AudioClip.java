import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioClip implements LineListener {
	
	private final static String SOUND_DIR = "Sounds/";
	private static Mixer mixer;
	public String name, filename;
	private Clip clip = null;
	private boolean isLooping = false;
	private boolean isStopped = false;
	//private SoundsWatcher watcher = null;
	private DecimalFormat df; 
	
	public AudioClip(String fnm){
		loadClip(fnm);
	}
	
	private void loadClip(String fnm) {
		/*
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		for(Mixer.Info info : mixInfos) {
			System.out.println(info.getName() + " --- " + info.getDescription());
		}
		
		DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
		try {
			clip = (Clip)mixer.getLine(dataInfo);
		} catch(LineUnavailableException e) {
			e.printStackTrace();
		}
		
		try {
			URL soundURL = AudioSystem.getAudioInputStream(getClass().getResource("Sounds/" + fnm) );
		*/
		
		
		filename = fnm;
		AudioInputStream stream;
		try {
			//stream = AudioSystem.getAudioInputStream(getClass().getResource("boo.wav") );
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL soundURL = ClassLoader.getSystemResource("Sounds/" + fnm);
			stream = AudioSystem.getAudioInputStream(soundURL);
			AudioFormat format = stream.getFormat();

			 if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
					   (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
					AudioFormat newFormat =
					   new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
											format.getSampleRate(),
											format.getSampleSizeInBits()*2,
											format.getChannels(),
											format.getFrameSize()*2,
											format.getFrameRate(), true);  // big endian
					// update stream and format details
					stream = AudioSystem.getAudioInputStream(newFormat, stream);
					System.out.println("Converted Audio format: " + newFormat);
					format = newFormat;
				  }

			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.addLineListener(this);
			clip.open(stream);
			stream.close();
			//`System.out.println("Audio actually loaded");
		} catch (UnsupportedAudioFileException e) {
			System.out.println(".wav not supported");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Io error");
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			System.out.println("line unavaliable");
			e.printStackTrace();
		}
	}
	
	public boolean isActive() {
		return clip.isActive();
	}
	
	public void close() {
		if (clip != null) {
	      clip.stop();
	      clip.close();
	    }
	}


	public void play(boolean toLoop) {
		if (clip != null) {
	    	isLooping = toLoop;
	    	isStopped = false;
	    	clip.start(); // start playing
			System.out.println("Playing " + filename);
	    }
	}

	public void stop()
	  // stop and reset clip to its start
	{ if (clip != null) {
			isStopped = true;
	    	clip.stop();
	    	clip.setFramePosition(0);
			System.out.println("Stopping " + filename);
	    }
	}

	public void pause() {
	  // stop the clip at its current playing position
		if (clip != null) {
			isStopped = true;
			clip.stop();
			System.out.println("Pausing " + filename);
		}
	}

	public void update(LineEvent lineEvent) {
	  /* Called when the clip's line detects open, close, start, or
	     stop events. The watcher (if one exists) is notified.
	  */
	    // when clip is stopped / reaches its end
		if (lineEvent.getType() == LineEvent.Type.STOP & !isStopped) {
	      // System.out.println("update() STOP for " + name);
	    	clip.stop();
	    	clip.setFramePosition(0);  // NEW
			System.out.println("End of Line for " + filename);
	    	if (isLooping) {
	    		System.out.println("Restarting " + filename);
	        	clip.start();
	    	}
	    }
	}
}
