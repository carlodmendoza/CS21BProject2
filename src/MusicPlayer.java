import java.io.*;
import java.net.*;
import java.util.*;
import javazoom.jlgui.basicplayer.*;

/**
  * This class implements a simple player based on BasicPlayer.
  * BasicPlayer is a threaded class providing most features
  * of a music player. BasicPlayer works with underlying JavaSound
  * SPIs to support multiple audio formats. Basically JavaSound supports
  * WAV, AU, AIFF audio formats. Add MP3 SPI and Vorbis
  * SPI in your CLASSPATH to play MP3 and Ogg Vorbis file.
  * @author Gio Lopez/Carlo Mendoza; BasicPlayer by javazoom; based on work by Carlos Gomez Rodriguez
  * @version 2016-05-19 (v7.7)
  */
 // see here: http://www.programcreek.com/java-api-examples/index.php?source_dir=aetheria-master/age/src/eu/irreality/age/AGESoundClient.java#

/**
 * Constructor for objects of class MusicPlayer
 */
public class MusicPlayer {
	private String songName, pathToFile;
	private PrintStream out = null;
	final BasicPlayer bp = new BasicPlayer();

	/**
	 * Plays music without looping. Not actually used, but included for testing purposes.
	 * @param  name   filename with extension of sound file to play
	 */
	public void play(String name) {
		songName = name;
		pathToFile = System.getProperty("user.dir") + "/" + songName;
		BasicPlayer player = new BasicPlayer();
		BasicController control = (BasicController) player;
		try {
			control.open(new URL("file:///" + pathToFile));
			control.play();
			control.setGain(0.025);
			control.setPan(0.0);
		} catch (BasicPlayerException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	* Plays the sound file located at the given URL, looping it loopTimes times (i.e., playing it loopTimes+1 times in total).
	* @param u URL to play
	* @param loopTimes number of times to loop playback (negative means infinite)
	* @throws IOException if file not found
	*/
	public void audioStartUnpreloaded ( final URL u , final int loopTimes ) throws IOException {
		try {
			InputStream theStream = u.openStream();
			//if this if-else structure gives any problem, we can also directly open the URL with the basicplayer. This avoids the mark/reset problem.
			if ( theStream.markSupported() ) {
				bp.open(theStream);
			} else { //in applets that read remote URLs, mark is not supported so we need to add an extra layer.
				BufferedInputStream bib = new BufferedInputStream(theStream);
				bp.open(bib);
			}
		} catch ( BasicPlayerException bpe ) {
			// bpe.printStackTrace();
			throw new IOException(bpe);
		}
		bp.addBasicPlayerListener(new BasicPlayerListener() {
			private int loopCount = loopTimes;
			public void opened(Object arg0, Map arg1) {}
			public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {}
			public void setController(BasicController arg0) {}
			public void stateUpdated(BasicPlayerEvent arg0) {
				if ( arg0.getCode() == BasicPlayerEvent.EOM ) {
					if ( loopCount != 0 ) {
						//if a stop method has been called, the basic player will have been unregistered from the table, then we have to stop playing
						if ( loopCount < 0 ) { //infinite loop
							restartSound(bp, u);
						} else if ( loopCount > 0 ) {
							loopCount--;
							restartSound(bp, u);
						}
					}
				}
			}
		}
		                         );
		try {
			bp.play();
		} catch ( BasicPlayerException bpe ) {
			// bpe.printStackTrace();
			throw new IOException(bpe);
		}
	}

	/**
	 * Does the actual looping of the song.
	 * @param bp the BasicPlayer currently playing the song
	 * @param u  the URL to the song
	 */
	private void restartSound ( BasicPlayer bp , URL u ) {
		try {
			bp.stop();
			bp.open(u); //to fix mark/reset not supported? if not, just wrap open with bufferedinputstream
			bp.play();
			bp.setGain(0.75);
		} catch ( BasicPlayerException bpe ) {
			// bpe.printStackTrace();
		}
	}

	/**
	 * Stops the music being played completely.
	 */
	public void stopMusic() {
		try {
			bp.stop();
		} catch (BasicPlayerException bpe) {
			// bpe.printStackTrace();
		}
	}
}