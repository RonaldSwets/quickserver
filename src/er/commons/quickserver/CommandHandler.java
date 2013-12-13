package er.commons.quickserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This class must be extended to handle commands
 * 
 * @author eric
 */
public abstract class CommandHandler implements Runnable {
   private Socket socket;
   private QuickServer parent;
   
   /**
    * Set the socket
    * 
    * @param socket
    */
   public void setSocket(Socket socket, QuickServer parent) {
      this.socket = socket;
      this.parent = parent;
   }
   
   /**
    * The command handler method
    * 
    * @param command
    */
   protected abstract void handle(String command) throws IOException;
   
   /**
    * The handle method can use the send method to return something to the
    * client.
    * 
    * @param message
    */
   protected void send(String message) throws IOException {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
      writer.write(message);
      writer.newLine();
      writer.flush();
   }
   
   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Runnable#run()
    */
   @Override()
   public void run() {
      System.out.println("handling client");
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
         String command = reader.readLine();
         System.out.println("command received: '" + command + "'");
         if (command == null) {
            System.out.println("null command ignored");
         } else if (command.equals("shutdown")) {
            this.parent.shutdown();
         } else {
            this.handle(command);
         }
         this.socket.close();
      } catch (IOException e) {
         System.err.println("IOException in client: " + e.getMessage());
         try {
            this.socket.close();
         } catch (IOException e1) {
            System.err.println("Another IOException in client while closing the socket: " + e.getMessage());
         }
      }
      System.out.println("client done");
   }
}
