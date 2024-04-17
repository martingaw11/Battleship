import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client extends Thread{


	Socket socketClient;

	// todo: clientID
	String clientID;
	ObjectOutputStream out;
	ObjectInputStream in;

	GameMessage ReceivedMessage;

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
				//todo: update clients did something
			ReceivedMessage = (GameMessage)in.readObject();

			callback.accept(ReceivedMessage);
			}
			catch(Exception e) {
				//callback.accept("Error in client stream");
			}
		}

    }

	//todo:  clients sends a message
	public void send(GameMessage newMessage) {

		try {
			out.writeObject(newMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in sending message");
			e.printStackTrace();
		}
	}

	public boolean joinChat(String userID) {

		try {
			// receive the usernames list from the server
			GameMessage message = ReceivedMessage;

			if (message.userNames.add(userID)){
				message.userID = userID;
				clientID = userID;
				message.newUser = true;
				System.out.println("Success");

				// send message to server
				out.writeObject(message);
				System.out.println("sent Success");
				return true;
			}

		}
		catch (IOException e) {
			e.printStackTrace();
			callback.accept("Error: Could not communicate with the server");
		}
		return false;
	}


}
