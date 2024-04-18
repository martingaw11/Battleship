import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;


public class Server{

    /**
     *  number of clients connected to server
     */
    int count = 0;

    /**
     *  ArrayList of clientThread
     */
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

    /**
     *  list of unique clients usernames
     */
    Set<String> listOfClientsID;

    /**
     *  map of userName to clientThread
     */
    HashMap<String, ClientThread> clientMap = new HashMap<>();

    /**
     *  queue of clientThread waiting for opponent
     */
    Queue<ClientThread> gameQueue = new ArrayDeque<>();

    /**
     *  ArrayList of GameThreads
     */
    ArrayList<GameThread> gamesInProgress = new ArrayList<>();

    /**
     *  The server
     */
    TheServer server;

    /**
     *  Callback
     */
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
        public void updateClients(GameMessage newGameMessage) {

            for (ClientThread t : clients) {
                try {
                    t.out.writeObject(newGameMessage);
                }
                catch (Exception e) {
                    System.out.println("did not notify clients");
                }
            }
        }
        public void processRequest(GameMessage clientRequest){
            switch(clientRequest.operationInfo){
                case "deploy":
                    callback.accept(clientRequest.userID  + " requested : " + clientRequest.operationInfo);
                    System.out.println("deploy");
                    break;

                case "backToBase":
                    callback.accept(clientRequest.userID  + " requested : " + clientRequest.operationInfo);
                    System.out.println("backToBase");
                    break;

                case "":
                    callback.accept(clientRequest.userID  + " requested : " + clientRequest.operationInfo);
                    System.out.println("null");
                    break;

                default:
                    callback.accept(clientRequest.userID  + " : " + clientRequest.operationInfo + "is an invalid operation");
                    System.out.println(clientRequest.userID  + " : " + clientRequest.operationInfo + "is an invalid operation");
            }
        }

        //todo: update clients moves

        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);

                validateUserName();


            }
            catch (IOException e) {
                // Handle any exceptions related to socket I/O
                System.out.println("Streams not open");
            }


            // todo Process client request
            while (true) {
                try {

                    // Read a request from the client
                    GameMessage clientGameMessage = (GameMessage) in.readObject();

                    // if new client joins, add to clients online
                    if(clientGameMessage.newUser){

                        listOfClientsID.add(clientGameMessage.userID);      // add clientThread to List
                        this.clientID = clientGameMessage.userID;           // set clientName
                        clientMap.put(this.clientID, this);                // map clientID to clientThread

                        callback.accept("adding " + clientGameMessage.userID + " to game base");
                        System.out.println("adding " + clientGameMessage.userID);

                        GameMessage newGameMessage = new GameMessage();
                        newGameMessage.userID = clientID;
                        newGameMessage.newUser = true;

                        synchronized (listOfClientsID) {
                            newGameMessage.userNames = new HashSet<>(listOfClientsID);
                        }
                        if(clientID != null) {
                            updateClients(newGameMessage);  // update usernames on client side
                        }
                    }
                    else{
                        // todo: listening for client request
                        // todo: if client request is "deploy" add thread to queue

                        // you can skip block
                        {   //  ***************************     debugging     **************************

                            // Update server && clients with the received message
                            if(clientGameMessage.opponentMatched){
                                callback.accept("Sending GameInfo from player" +  clientID + " to player : "+ clientGameMessage.opponent);
                                System.out.println("Sending GameInfo from player" +  clientID + " to player : "+ clientGameMessage.opponent);
                            }

                            clientGameMessage.userID = clientID;

                            //todo: here i update userNames
                            synchronized (listOfClientsID) {
                                clientGameMessage.userNames = new HashSet<>(listOfClientsID);
                            }
                            if(clientID != null){
                                updateClients(clientGameMessage);   // for some reason lets keep updating client usernames
                            }

                        }

                        //  ***************************     debugging     **************************


                        // ****************************** process client request ******************************
                        if(!Objects.equals(clientGameMessage.operationInfo,"")){
                            processRequest(clientGameMessage);
                        }
                    }



                }
                catch (IOException | ClassNotFoundException e) {
                    // Handle any exceptions during message processing
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + clientID + "....closing down!");

                    // Inform other clients that this client has left the server
                    GameMessage newGameMessage = new GameMessage();
                    newGameMessage.userID = clientID;
                    newGameMessage.MessageInfo = "Client " + clientID + " has left the server!";

                    // Remove this client from the list of active clients
                    clients.remove(this);
                    listOfClientsID.remove(this.clientID);

                    synchronized (listOfClientsID){
                        newGameMessage.userNames = new HashSet<>(listOfClientsID);
                    }

                    if(clientID != null){
                        updateClients(newGameMessage);
                    }
                    break; // Exit the loop and end the thread
                }
            }

        }

        public void sendUserNames() {
            synchronized (listOfClientsID){
                try {
                    GameMessage gameMessage = new GameMessage();
                    gameMessage.userNames = new HashSet<>(listOfClientsID);
                    gameMessage.newUser = true;
                    out.writeObject(gameMessage);
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

    /**
     * Thread listening for communication between two client playing against each other
     */
    class GameThread {
        boolean versusPlayer = false;
        ClientThread playerOne;
        ClientThread playerTwo;

        GameThread(ClientThread p1, ClientThread p2){
            this.playerOne = p1;
            this.playerTwo = p2;
        }
    }
}






