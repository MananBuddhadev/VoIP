import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

/**
 *
 * serverReceiving.java
 *
 * Version: $Id$ : serverReceiving.java, v1.0
 *
 * Revision:
 * 			Initial Revision
 *
 */

/**
 * The end system that accepts the connection request
 *
 * @author Manan C Buddhadev
 * @author Hema Bahirwani
 */

public class serverReceivng {
	ByteArrayOutputStream audioOut;
	AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, false);
	DatagramSocket serverCall;

	TargetDataLine targetDataLine;
	AudioInputStream InputStream;
	SourceDataLine sourceLine;
	DatagramPacket incomingCall;
	static boolean hangup = false;
	boolean pickedUp = false;

	/**
	 * Setting up the server
	 */
	public serverReceivng() {
		try {
			serverCall = new DatagramSocket(4000);
			waitForTheCall();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Waiting for Caller to request connection
	 */
	void waitForTheCall() {
		// wait for the call
		try {
			byte[] incomingData = new byte[1000];
			incomingCall = new DatagramPacket(incomingData, incomingData.length);
			serverCall.receive(incomingCall);
			ReceivingMainPage receivingMainPage = new ReceivingMainPage(String.valueOf(incomingCall.getAddress()));
			receivingMainPage.setText("Incoming Call from: " + String.valueOf(incomingCall.getAddress()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Establish communication
	 */
	void pickUp() {
		// pick up the call
		try {
			String answer = "answered";
			byte[] pickup = answer.getBytes();
			DatagramPacket pickedup = new DatagramPacket(pickup, pickup.length, incomingCall.getAddress(),
					incomingCall.getPort());
			serverCall.send(pickedup);

			ReceivingMainPage receivingMainPage = new ReceivingMainPage(String.valueOf(incomingCall.getAddress()));
			receivingMainPage.setText("Connected");

			// start the target line
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			// start the sourcedataline
			DataLine.Info sourcedataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceLine = (SourceDataLine) AudioSystem.getLine(sourcedataLineInfo);
			sourceLine.open(audioFormat);
			sourceLine.start();
			pickedUp = true;

			// start the listen thread
			new listen().start();

			// start the talk thread
			new talk(incomingCall).start();
		}catch (Exception e) {
		}
	}

	/**
	 * Terminate all connections
	 *
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
     */
	public void disconnect() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (pickedUp == true) {
			hangup = true;
			clientCalling.hangup = true;
			pickedUp = false;
			serverCall.close();
			System.exit(0);
		}
		else{
			byte[] disconnect = new byte[10];
			DatagramPacket pickedup = new DatagramPacket(disconnect, disconnect.length, incomingCall.getAddress(),
					incomingCall.getPort());
			serverCall.send(pickedup);
			serverCall.close();
			System.exit(0);
		}

	}

	class talk extends Thread {

		DatagramPacket answer;

		public talk(DatagramPacket answer) {
			this.answer = answer;
		}

		public void run() {
			audioOut = new ByteArrayOutputStream();
			byte[] audioBuffer = new byte[1000];
			try {
				while (hangup == false) {
					int cnt = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
					if (cnt > 0) {
						DatagramPacket sendPacket = new DatagramPacket(audioBuffer, audioBuffer.length,
								answer.getAddress(), answer.getPort());
						serverCall.send(sendPacket);
						audioOut.write(audioBuffer, 0, cnt);
					}
				}
				// audioOut.close();
			} catch (Exception e) {
				System.exit(0);
			}
		}
	}

	class listen extends Thread {
		public void run() {
			while (hangup == false) {
				try {
					byte[] incomingAudio = new byte[1000];
					DatagramPacket incomingAudioPacket = new DatagramPacket(incomingAudio, incomingAudio.length);
					serverCall.receive(incomingAudioPacket);
					incomingAudio = incomingAudioPacket.getData();
					InputStream byteInputStream = new ByteArrayInputStream(incomingAudio);

					InputStream = new AudioInputStream(byteInputStream, audioFormat,
							incomingAudio.length / audioFormat.getFrameSize());
					int cnt;
					byte[] audioBuffer = new byte[1000];
					while ((cnt = InputStream.read(audioBuffer, 0, audioBuffer.length)) != -1) {
						if (cnt > 0) {
							sourceLine.write(audioBuffer, 0, cnt);
						}
					}
				} catch (Exception e) {
				}
			}
		}
	}
}