package er.commons.quickserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The QuickServer shell
 * 
 * @author eric
 */
public class QuickServer implements Runnable {
   /**
    * The handler class
    */
   private Class<? extends CommandHandler> handlerClass;
   
   /**
    * Are we running?
    */
   private boolean run;
   
   /**
    * Port to listen on
    */
   private int port;
   
   /**
    * List with client threads
    */
   private List<Thread> handlers;
   
   /**
    * Constructor
    * 
    * @param handlerClass
    */
   public QuickServer(Class<? extends CommandHandler> handlerClass, int port) {
      this.handlerClass = handlerClass;
      this.port = port;
      this.handlers = new ArrayList<Thread>();
      
      Thread t = new Thread(this);
      t.start();
   }
   
   /**
    * Shutdown the server
    */
   public void shutdown() {
      this.run = false;
   }
   
   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Runnable#run()
    */
   @Override()
   public void run() {
      System.out.println("starting server");
      try {
         ServerSocket ss = new ServerSocket(this.port);
         ss.setSoTimeout(1000);
         
         this.run = true;
         while (this.run) {
            try {
               Socket socket = ss.accept();
               
               try {
                  CommandHandler handler = this.handlerClass.newInstance();
                  handler.setSocket(socket, this);
                  Thread handlerThread = new Thread(handler);
                  System.out.println("starting thread " + handlerThread.getId());
                  handlerThread.start();
                  this.handlers.add(handlerThread);
               } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                  System.err.println("exception creating handler: " + e.getMessage());
                  e.printStackTrace();
               }
            } catch (SocketTimeoutException se) {
            }
            
            // sleep
            try {
               Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            
            // cleanup handlers list
            Iterator<Thread> i = this.handlers.iterator();
            while (i.hasNext()) {
               Thread t = i.next();
               if (!t.isAlive()) {
                  i.remove();
                  System.out.println("thread " + t.getId() + " is dead");
               }
            }
         }
         
         // wait for all threads to exit
         System.out.println("waiting for running client threads...");
         while (this.handlers.size() > 0) {
            Iterator<Thread> i = this.handlers.iterator();
            while (i.hasNext()) {
               Thread t = i.next();
               if (!t.isAlive()) {
                  i.remove();
               }
            }
            
            try {
               Thread.sleep(500);
            } catch (InterruptedException e) {
            }
         }
         
         ss.close();
      } catch (IOException e) {
         System.err.println("IOException in server: " + e.getMessage());
      }
      
      System.out.println("server done");
   }
}
