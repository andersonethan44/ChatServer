
package EthanChatServer; 

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer implements Runnable 
{
public static void main(String[] args) throws Exception
    {
    
    new ChatServer();
    }

private ServerSocket ss;
private int          serverPort = 3333;


private ArrayList<UserProfile> clients =
    new ArrayList<UserProfile>();
private ConcurrentHashMap<String,ObjectOutputStream> whosIn = 
	new ConcurrentHashMap<String,ObjectOutputStream>();   


public ChatServer() throws Exception 
   {
 

   ss = new ServerSocket(serverPort);
    
   try {
       
        String filecontent = new Scanner(new File("clients.txt")).useDelimiter("\\Z").next();
        System.out.println(filecontent);
        
        for (int i = 0; i< filecontent.split(" ").length-1; i= i + 2){
            clients.add(new UserProfile(filecontent.split(" ")[i], filecontent.split(" ")[i+1]));           
        }
       
       
        }
    catch(FileNotFoundException fnfe)
        {
    	System.out.println("clients.txt is not found, so an empty collection will be used.");
        }
    
   System.out.println("Previously in the chat room: ");
  System.out.println(clients);
   System.out.println("ChatServer is up at "
          + InetAddress.getLocalHost().getHostAddress()
           + " on port " + ss.getLocalPort());

   new Thread(this).start();  
   }                         



public void run()
    {
	Socket             s                = null;
	ObjectInputStream  ois              = null;
	ObjectOutputStream oos              = null;
	String             firstMessage     = null;
	String             chatName         = null;
	String             enteredPassword  = null;
	String             clientAddress    = null;
	try {
	    s = ss.accept(); 
	    ois = new ObjectInputStream(s.getInputStream());
	    firstMessage = (String) ois.readObject();
            
            
	    oos = new ObjectOutputStream(s.getOutputStream());
	    }
	catch(Exception e) 
	    {              
	    if (s != null)
	    clientAddress = s.getInetAddress().getHostAddress();
	    System.out.println("Connect/join exception from " + clientAddress);
	    return; // terminate this client thread 
	    }
	finally
	    {
	    new Thread(this).start();
	    }
	
	try { 
        System.out.println("First message received: " + firstMessage);
        // This will be used later to manage username/ passwords
        int spaceOffset = firstMessage.indexOf(" ");
        if (spaceOffset < 0)
              {
              oos.writeObject("Invalid format. " 
                            + "Are you calling the right address and port?");
              oos.close();                         
              System.out.println("Invalid 1st message received (no space separator): " + firstMessage);
              return;                              // If incorrect username and password format kill the thread
              }
           chatName = firstMessage.substring(0,spaceOffset).toUpperCase();
           enteredPassword = firstMessage.substring(spaceOffset).trim();
           if (enteredPassword.contains(" "))
              {
              oos.writeObject("Invalid format. " 
                            + "Are you calling the right address and port?");
              oos.close();                         
              System.out.println("Invalid 1st message received (space in name or pw): " + firstMessage);
              return;                              // Because space is delimiter cannot be in username or password, exits thread if space
              }

  
    
       
        oos.writeObject("Welcome to the chat room " + chatName + "!"); // client is in chat room and sends message
      
        if (!whosIn.containsKey(chatName)) // checks to see if in
           {
           //sendToAllClients("Welcome to " + chatName + " who has just joined the chat room!");
           whosIn.put(chatName,oos); // add new-join client to collection
           System.out.println(chatName + " is joining");
           }
           
        }
    catch (Exception e)
        {                         
        System.out.println("Connection failure during join processing: " + e);
        if (s.isConnected())
           {
           try {s.close();}         // closes socket
           catch(IOException ioe){} 
           }
        return; // kill thread
        }
    // Show who's in the chat room
    System.out.println("Currently in the chat room:");
    String[] chatNames = whosIn.keySet().toArray(new String[0]);
    for (String name : chatNames)
    	 System.out.println(name);
    
   // Processing for sending and recieving information.
   try { 
       while (true)
           {  
           Object something = ois.readObject(); // wait for this client to say something
           if (something instanceof String)
              {	
               
              String chatMessage = ((String) something).trim();
              if (chatMessage.startsWith("PasswordRequest111")){
                  // REGISTERING THE USER
                  String[] input = new String[3];
                  //System.out.println(chatMessage.split(" "));
                  for (String k: chatMessage.split(" ")){
                      
                      System.out.println(k);
                  }
                  input[1] = chatMessage.split(" ")[1];
                  input[2] = chatMessage.split(" ")[2];
                  System.out.println("Adding new User to clients.ser with user: " + input[1] + "and pw: "+ input[2]);
                  chatName = input[1];
                  clients.add(new UserProfile(input[1],input[2]));
                  sendToAllClients("SERVER MESSAGE: Welcome to " + chatName + " who has Registerd for the first time!");
                 
     
                  saveChatName(chatName.toString()+" " +input[2]+" ");
                  
                  
              }
              else if (chatMessage.startsWith("LoginUser111")){
                  String[] input = new String[3];
                  //System.out.println(chatMessage.split(" "));
                  for (String k: chatMessage.split(" ")){
                      
                      System.out.println(k);
                  }
                  input[1] = chatMessage.split(" ")[1];
                  input[2] = chatMessage.split(" ")[2];
                  System.out.println("Checking valid username: " + input[1] + "and pw: "+ input[2]);
                  chatName = input[1];
                  UserProfile checkuser = new UserProfile(input[1],input[2]);
                  
                  for(UserProfile e: clients){
                      System.out.println("Comparing user" + e.getUserName() + " and pw: "+ e.getPassword()+ "to: " + input[1] + " and:  " +input[2]);
                      
                         if (e.getUserName().equals((String)input[1]) && e.getPassword().equals((String)input[2])){
                            
                             System.out.println("Password and user were correct");
                             sendToAllClients("SERVER MESSAGE: Welcome to " + chatName + " who has logged into the chat room!");
                             
                             
                         }
                         
                          
                      
                      
                      
                  }
                  
                  
                 
     
             
                  
                  
              }
              
              
           
              else
              {	 
                  System.out.println("TEST");
              System.out.println("Received from " + chatName + ": " + something );
              sendToAllClients(chatName + " says: " + something);
              }
              }
           else if (something instanceof byte[]){
                  byte[] inputFile = (byte[]) something;
                  System.out.println("Received File ");
                  //sendToAllClients("File Received: Check your local directory");
                  sendToAllClients(inputFile);
              }
           }        	   
       }
   
    catch (Exception e) 
       {
       // when oos fails. Exits via exception
       ObjectOutputStream currentOOS = whosIn.get(chatName);
       if (currentOOS == oos) // same oos as when they joined
          {
           // makes sure people are only exiting if their own oos exits
    	  System.out.println(chatName + " is leaving.");
          whosIn.remove(chatName); // remove client from whoin 
          sendToAllClients("Goodbye to " + chatName
    		             + " who has just left the chat room!");
          }
       else 
          { 
    	  System.out.println(chatName + " is rejoining.");
          }
       // Show who's in the chat room
       System.out.println("Currently in the chat room:");
       String[] names = whosIn.keySet().toArray(new String[0]);
       for (String name : names)
            System.out.println(name);
       }
    
    } //termination of client thread

//=========================================================
private synchronized void sendToAllClients(Object whatever) // Synchronized to work among multiple clients and multiple threads
   {
   System.out.println("Sending '" + whatever + "' to everyone.");	
  
   ObjectOutputStream[] oosList = whosIn.values().toArray(new ObjectOutputStream[0]);
   
   for (ObjectOutputStream clientOOS : oosList)
       {
       try {clientOOS.writeObject(whatever);}
       catch (IOException e) {} 
       }
	  
   }

 private synchronized void saveChatName(String input)  //Synchronized to work among multiple threads. File is in Files Tab.
  { 
  try {
     
     
      BufferedWriter writer = new BufferedWriter(new FileWriter("clients.txt",true));
	writer.write(input);
        
        writer.close();
       
	  
	  
	  }
  catch(Exception e)
	  {
	  System.out.println("clients.txt cannot be saved: " + e);
	  }
  }
}
