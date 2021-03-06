import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class ChatClient implements Runnable, CommandListener
{
  private SprintChat parent; //this'll be used later to retrieve the phone's active display
  private Display display;

  //The following will be obtained from the parent class:
  private String nickname, channel;

  public Form frmChatUI;

  private StringItem sitemCurrentChat;

  //private StringBuffer sbufCurrentChat;

  private CommIRC circSprintChat; //Use Justin Arthur's IRC communications library.

  private boolean stop;

  InputStream is;
  OutputStream os;
  SocketConnection sc;
  Sender sender; //Use Sun's Sender class.

  /** Default Constructor **/
  public ChatClient(SprintChat parentClass)
  {
    parent = parentClass;
    display = Display.getDisplay(parent);
    nickname = parent.tfNick.getString();
    channel = parent.tfChannel.getString();

    frmChatUI = new Form("Connecting to server...");
    frmChatUI.append(sitemCurrentChat = new StringItem("", ""));
    display.setCurrent(frmChatUI);
  }

  public Sender getSender() //Required method for CommIRC
  {
    return sender;
  }

  public void start()
  {
    Thread tClient = new Thread(this);
    tClient.start();
  }

  public void run() //invoked by start method of tClient
  {
    try
    {
      sc = (SocketConnection) Connector.open("socket://chat.serenia.net:6667");
      sitemCurrentChat.setText("Connected to server");
      is = sc.openInputStream();
      os = sc.openOutputStream();

      // Start the thread for sending CRLF-terminated strings to server:
      sender = new Sender(os);

      // Invoke an instance of CommIRC:
      circSprintChat = new CommIRC(this);

      // Loop forever, receiving data
      while (true)
      {
        StringBuffer sb = new StringBuffer();
        int c = 0;

        while (((c = is.read()) != '\n') && (c != -1))
        {
          sb.append((char) c);
        }

        if (c == -1)
        {
          System.out.println("breaking connection");
          break;
        }
        //A line of text has been recieved, register to the IRC server if it is the first:
        if (!circSprintChat.isRegistered())
        {
          circSprintChat.register(nickname,"SprintUser","SprintChat User");
        }

        // Display received text to user (for debug only)
        System.out.println(sb.toString());

        circSprintChat.parse(sb.toString().trim()); //Trim will kill off any crlf combination
      }
      stop();
      sitemCurrentChat.setText("Connection closed");
      //f.removeCommand(sendCommand);
    }
    catch (ConnectionNotFoundException cnfe)
    {
      Alert a = new Alert("SprintChat", "Connection Failed.",
        null, AlertType.ERROR);
      a.setTimeout(Alert.FOREVER);
      //a.setCommandListener(this);
      display.setCurrent(a);
    }
    catch (IOException ioe)
    {
      if (!stop)
      {
        ioe.printStackTrace();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void onRegistered() //Used by CommIRC
  {
    circSprintChat.join(channel);
    frmChatUI.setTitle("Chatting in " + channel);
  }

  public void onJoin(String joinedChannel)
  {
    //This part of CommIRC not complete yet.
    //frmChatUI.setTitle("Chatting in " + channel);
  }

  public void onMessage(String user, String target, String message)
  {
    System.out.println("user = " + user);
    System.out.println("target = " + target);
    System.out.println("message = " + message);
    if (target.toUpperCase().equals(channel.toUpperCase()))
    {
      sitemCurrentChat.setText(
      sitemCurrentChat.getText() +
      "\n<" + user + "> " +message);
    }
  }

  /** Close Off all of the Sockets **/
  public void stop()
  {
    try {
      stop = true;

      if (sender != null) {
        sender.stop();
      }

      if (is != null) {
        is.close();
      }

      if (os != null) {
        os.close();
      }

      if (sc != null) {
        sc.close();
      }
    } catch (IOException ioe) {}

  }

  //Added for testing purposes:
      public void commandAction(Command c, Displayable s) { }
}
