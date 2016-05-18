Author: Manan Chetan Buddhadev
Author: Hema Bahirwani

How to compile the files:

The given files are all in .java format. The user can compile all the files together using the command 'javac *.java' or 
compile the files one by one using 'javac filename.java'

How to run the application:
You will need two hosts to test this application.
The application runs on Java, inorder to excexute, the .java files must be compiled. After compiling the files .class are created.
Once, that is done use the command 'java CallerMainPage' on one host and 'java ReceivingMainPage' on the other.

Caller:
Once the CallerMainPage is execute a UI will appear with a text box and two buttons. Enter the IP Address to call to and hit the Call Button.
If the call is accepted the text box will show connected, else you will hear a voice saying the caller is unavailable. The window will close
once either of the parties end the call.

Receiver:
On Receiving a call requesting the text box will change to incoming call from a particular IP.
If you want to talk hit Connect, else hit Disconnect or wait for the call to end (30 seconds).

Note:
1.) Run the ReceiverMainPage before trying to set up the call
