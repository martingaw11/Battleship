import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;


public class Server{

    int count = 0;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

    Set<String> listOfClientsID;
    TheServer server;
    private Consumer<Serializable> callback;


    Server(Consumer<Serializable> call){

        listOfClientsID = new HashSet<>();
        callback = call;
        server = new TheServer();
        server.start();
    }


    public class TheServer extends Thread{

        public void run() {

            try(ServerSocket mysocket = new ServerSocket(5555);){
                System.out.println("Server is waiting for a client!");

                while(true) {

                    Socket clientSocket = mysocket.accept();

                    ClientThread c = new ClientThread(clientSocket, ++count);  //todo: clientSocket ??
                    callback.accept("new client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();

                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch: " + e.getMessage());
            }
        }//end of run
    }


    class ClientThread extends Thread{


        Socket connection;
        int count;

        String clientID;

        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;			// client who?
        }

        //todo: update clients general
        public void updateClients(Message newMessage) {

            // if message is to 1 user send to user
            if(newMessage.secreteMessage){
                for (ClientThread t : clients) {
                    if(Objects.equals(newMessage.recipient,t.clientID)){
                        try {
                            t.out.writeObject(newMessage);
                        }
                        catch (Exception e) {
                            System.out.println("did not notify clients");
                        }
                    }
                }
            }
            // send to all users
            else {
                for (ClientThread t : clients) {
                    try {
                        t.out.writeObject(newMessage);
                    }
                    catch (Exception e) {
                        System.out.println("did not notify clients");
                    }
                }
            }
        }

        //todo: update clients moves

        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);

                validateUserName();     // todo: allow only unique usernames


            }
            catch (IOException e) {
                // Handle any exceptions related to socket I/O
                System.out.println("Streams not open");
            }


            // Process client messages
            while (true) {
                try {

                    // Read a message from the client
                    Message clientMessage = (Message) in.readObject();

                    // if new client joins, add to clients online
                    if(clientMessage.newUser){
                        listOfClientsID.add(clientMessage.userID);
                        this.clientID = clientMessage.userID;
                        System.out.println("adding " + clientMessage.userID);


                        // todo: Should we notify Client A that Client B has joined party?
                        Message newMessage = new Message();
                        newMessage.MessageInfo = clientID + " joined chat";
                        newMessage.userID = clientID;
                        newMessage.newUser = true;

                        synchronized (listOfClientsID) {
                            newMessage.userNames = new HashSet<>(listOfClientsID);
                        }
                        if(clientID != null) {
                            updateClients(newMessage);  // todo: update this to notify only opponent
                        }
                    }
                    else{
                        // Update server && clients with the received message		//todo: set sender
                        callback.accept("client: " + clientID + " sent: " + clientMessage.MessageInfo);
                        System.out.println("Sending message to: "+ clientMessage.recipient);


                        clientMessage.MessageInfo = clientID + " said: " + clientMessage.MessageInfo;
                        clientMessage.userID = clientID;
                        //todo: here i update userNames

                        synchronized (listOfClientsID) {
                            clientMessage.userNames = new HashSet<>(listOfClientsID);
                        }
                        if(clientID != null){
                            updateClients(clientMessage);
                        }
                    }



                }
                catch (IOException | ClassNotFoundException e) {
                    // Handle any exceptions during message processing
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + clientID + "....closing down!");

                    // Inform other clients that this client has left the server
                    Message newMessage = new Message();
                    newMessage.userID = clientID;
                    newMessage.MessageInfo = "Client " + clientID + " has left the server!";

                    // Remove this client from the list of active clients
                    clients.remove(this);
                    listOfClientsID.remove(this.clientID);

                    synchronized (listOfClientsID){
                        newMessage.userNames = new HashSet<>(listOfClientsID);
                    }

                    if(clientID != null){
                        updateClients(newMessage);
                    }
                    break; // Exit the loop and end the thread
                }
            }

        }

        public void sendUserNames() {
            synchronized (listOfClientsID){
                try {
                    Message message = new Message();
                    message.userNames = new HashSet<>(listOfClientsID);
                    message.newUser = true;
                    out.writeObject(message);
                    System.out.println("Sent list of usernames to client" + count);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        public void validateUserName() {
            sendUserNames();
        }

    }//end of client thread
}






