quickserver
===========

Very simple thread-based server. Incoming commands are handled by a CommandHandler, the most basic usage of this
quickserver would be somethink like this:

public class EchoServer extends CommandHandler {
  protected void handle(String command) throws IOException {
    this.send("You sent: " + command);
  }
  
  public static void main(String[] args) {
    new QuickServer(this, 10000);
  }
}

where the 10000 is the port to listen on for commands. If the command "shutdown" is sent, the server will shutdown
after all client threads are finished.
