package ml.sakii.factoryisland;

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioThread extends Thread{
	
	private boolean running=true;

	
	@Override
	public void run() {
		while(running) {
			play();
		}
	}
	
	private void play() {
		try (@SuppressWarnings("resource")
		AudioInputStream mp3In = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/Zongora.mp3")))){
			// AudioFormat describing the compressed stream
	        final AudioFormat mp3Format = mp3In.getFormat();
	        // AudioFormat describing the desired decompressed stream 
	        final AudioFormat pcmFormat = new AudioFormat(
	            AudioFormat.Encoding.PCM_SIGNED,
	            mp3Format.getSampleRate(),
	            16,
	            mp3Format.getChannels(),
	            16 * mp3Format.getChannels() / 8,
	            mp3Format.getSampleRate(),
	            mp3Format.isBigEndian()
	            );
	        // actually decompressed stream (signed PCM)
	        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmFormat,
	                (int)(pcmFormat.getSampleRate() * pcmFormat.getFrameSize()));
	        		//(int)(pcmFormat.getFrameRate() * pcmFormat.getFrameSize()));
	        try(AudioInputStream stream = AudioSystem.getAudioInputStream(pcmFormat, mp3In); Line line2 = AudioSystem.getLine(info)){
	        	
	        	@SuppressWarnings("resource")
				SourceDataLine line = (SourceDataLine)line2;
	        	line.open(pcmFormat,256);
	            byte[] data = new byte[256];
	            // Start
	            line.start();

	            int nBytesRead;
	            while (running && (nBytesRead = stream.read(data, 0, data.length)) != -1) {
	                line.write(data, 0, nBytesRead);
	            }
	            // Stop
	            line.drain();
	            line.stop();
	            //line.close();
	            //st.close();
	            System.out.println("stopped");
			} catch (LineUnavailableException | IOException e) {
				e.printStackTrace();
				running=false;
			}
		}catch(UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			running=false;
		}
	}
	
	public void kill() {
		running=false;
	}

}
