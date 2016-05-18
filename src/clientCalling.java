import java.io.*;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaName;
import javax.sound.sampled.*;
import java.net.*;

/**
 *
 * clientCalling.java
 *
 * Version: $Id$ : clientCalling.java, v1.0
 *
 * Revision:
 * 			Initial Revision
 *
 */

/**
 * The host who requests connection.
 *
 * @author Manan C Buddhadev
 * @author Hema Bahirwani
 *
 */

public class clientCalling {
    ByteArrayOutputStream audioOut;
    AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, false);
    TargetDataLine targetDataLine;
    AudioInputStream InputStream;
    SourceDataLine sourceLine;
    static DatagramSocket clientCall;
    static boolean hangup=false;
    String serverAddress;

    /**
     * Default constructor to set up the udp connection on a port and set the socket timeout
     *
     * @throws UnsupportedAudioFileException
     */
    public clientCalling(String serverAddress) throws UnsupportedAudioFileException {
        try {
            clientCall = new DatagramSocket(4001);
            clientCall.setSoTimeout(30000);
            callTheServer(serverAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initiate the session
     *
     * @throws UnsupportedAudioFileException
     */
    private void callTheServer(String serverAddress) throws UnsupportedAudioFileException {
        // call the server
        try {
            byte[] call = new byte[10];
            System.out.println("Address :"+serverAddress);
            InetAddress calleeIP = InetAddress.getByName(serverAddress);
            int port = 4000;

            //Sending request to talk
            DatagramPacket calling = new DatagramPacket(call, call.length, calleeIP, port);
            clientCall.send(calling);
            System.out.println("Called");
            pickUp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to implement the transmission of audio files
     *
     * @throws UnsupportedAudioFileException
     */
    private void pickUp() throws UnsupportedAudioFileException {
        // wait to pick up
        try {
            //Reading the response
            byte[] answered = new byte[1024];
            DatagramPacket answer = new DatagramPacket(answered, answered.length);
            answered = answer.getData();
            System.out.println("Response received");
            clientCall.receive(answer);
            // check if answered
            if (new String(answered).contains("answered")) {
                CallerMainPage callerMainPage=new CallerMainPage("Connected");
                callerMainPage.setText("Connected");

                // start the target line
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat);
                targetDataLine.start();

                // start the source line to listen
                DataLine.Info sourcedataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                sourceLine = (SourceDataLine) AudioSystem.getLine(sourcedataLineInfo);
                sourceLine.open(audioFormat);
                sourceLine.start();

                // start the talk thread
                new talk(answer).start();

                // start the listening thread
                new listen().start();
            } else {
                //If called disconnected by the other side
                File file = new File("Voice_003.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info datInfo = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(datInfo);
                sourceDataLine.open(format);
                sourceDataLine.start();
                byte[] toAudio = new byte[1024];
                int read;
                while ((read = audioInputStream.read(toAudio)) != -1) {
                    sourceDataLine.write(toAudio, 0, read);
                }
                System.exit(0);
            }
        } catch (SocketTimeoutException e) {
            //If the callee does not respond in stipulated time
            try {
                File file = new File("Voice_003.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info datInfo = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(datInfo);
                sourceDataLine.open(format);
                sourceDataLine.start();
                byte[] toAudio = new byte[1024];
                int read;
                while ((read = audioInputStream.read(toAudio)) != -1) {
                    sourceDataLine.write(toAudio, 0, read);
                }
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to end the connections
     *
     */
    public static void disconnect(){
        hangup=true;
        serverReceivng.hangup=true;
        clientCall.close();
        System.exit(0);
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
                while (hangup==false) {
                    int cnt = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
                    if (cnt > 0) {
                        DatagramPacket sendPacket = new DatagramPacket(audioBuffer, audioBuffer.length,
                                answer.getAddress(), answer.getPort());
                        clientCall.send(sendPacket);
                        audioOut.write(audioBuffer, 0, cnt);
                    }
                }
                audioOut.close();
            } catch (Exception e) {
                System.exit(0);
            }
        }
    }

    class listen extends Thread {
        public void run() {
            while (hangup==false) {
                try {
                    byte[] incomingAudio = new byte[1000];
                    DatagramPacket incomingAudioPacket = new DatagramPacket(incomingAudio, incomingAudio.length);
                    clientCall.receive(incomingAudioPacket);
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
