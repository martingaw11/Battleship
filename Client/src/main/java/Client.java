import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


public class Client extends Thread{


	Socket socketClient;
	String clientID;
	String opponent;

	Set<String> userNames = new HashSet<>();
	ObjectOutputStream out;
	ObjectInputStream in;

	GameMessage ReceivedMessage;
	GameController gameCtr;
	SearchingForPlayerController searchingCtr;

	// Will be:
	// 0 if Easy AI
	// 1 if Medium AI
	// 2 if Hard AI
	// 3 if vs. Player
	int difficulty;

	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call){
		callback = call;
	}

	public void run() {

		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
		System.out.println("opening");
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}


		while(true) {

			try {
				// clients receives message
			ReceivedMessage = (GameMessage)in.readObject();

			callback.accept(ReceivedMessage);
			}
			catch(Exception e) {
				//callback.accept("Error in client stream");
			}
		}

    }

	// clients sends a message
	public void send(GameMessage newMessage) {

		try {
			out.writeObject(newMessage);
		} catch (IOException e) {
			// Auto-generated catch block
			System.out.println("Error in sending message");
			e.printStackTrace();
		}
	}


}
