import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class SprintChat extends MIDlet implements CommandListener, ItemCommandListener
{
  private Command cmdConnect = new Command("Chat!", Command.SCREEN, 2);
  private Command cmdAdvSettings = new Command("Advanced","Advanced Settings...", Command.SCREEN, 3);
  private Command cmdBack = new Command("Back", Command.BACK, 1);
  private Command cmdExit = new Command("Exit", Command.EXIT, 1);

  private StringItem sitemChatButton = new StringItem("Chat!", null, Item.BUTTON);

  public TextField tfNick, tfChannel;

  private boolean firstTime;

  private Display display;

  private ChatClient chatClient;

  private Form frmConnect;
  private Form frmAdvanced;

  public String[] strNetworks = {"Esper", "Serenia"};

  public SprintChat()
  {
    firstTime = true;
    frmConnect = new Form("Connect to Chat Network");
    frmAdvanced = new Form("Advanced Connection Settings");
  }

  protected void startApp()
  {
    display = Display.getDisplay(this);
    if(firstTime)
    {
      createConnectionForm();
      createAdvSettingsForm();
      firstTime = false; //Prevents form recreation on midlet resume/restart:
    }
      showConnectionForm();
  }

  public void createConnectionForm()
  {
    //Create form items for connection settings form:
    tfNick = new TextField("Nickname:", "", 32, TextField.ANY);
    tfChannel = new TextField("Chat Channel:", "#ChatZone", 20, TextField.ANY);
    //StringItem sitemChatButton = new StringItem("Chat!", null, Item.BUTTON);
    sitemChatButton.setDefaultCommand(cmdConnect);
    sitemChatButton.setItemCommandListener(this);
    sitemChatButton.setLayout(Item.LAYOUT_CENTER);
    ChoiceGroup cgNetworks = new ChoiceGroup("Chat Network:", ChoiceGroup.POPUP, strNetworks, null);
    cgNetworks.setSelectedIndex(1, true); //Set default network to Serenia

    //Add items to the connection info form:
    frmConnect.append("Enter your user info to connect:");
    frmConnect.append(tfNick);
    frmConnect.append(tfChannel);
    frmConnect.append(cgNetworks);
    frmConnect.append(sitemChatButton);
    frmConnect.addCommand(cmdAdvSettings);
    frmConnect.addCommand(cmdExit);
    frmConnect.setCommandListener(this);
  }

  public void showConnectionForm()
  {
    Display.getDisplay(this).setCurrent(frmConnect);
  }

  public void createAdvSettingsForm()
  {
    //Create form items for advanced connection settings form:
    TextField tfEmail = new TextField("E-Mail", "", 15, TextField.EMAILADDR);
    TextField tfNickServPassword = new TextField("NickServ Password", "", 15, TextField.PASSWORD);
    //Add form items to advanced connection settings form:
    frmAdvanced.append(tfEmail);
    frmAdvanced.append(tfNickServPassword);
    frmAdvanced.addCommand(cmdBack);
    frmAdvanced.setCommandListener(this);
  }

  public void showAdvSettingsForm()
  {
    Display.getDisplay(this).setCurrent(frmAdvanced);
  }

  public void commandAction(Command c, Displayable s)
  {
    if (c == cmdExit)
    {
      destroyApp(false);
      notifyDestroyed();
    }
    else if (c == cmdAdvSettings)
    {
      showAdvSettingsForm();
    }
    else if ((c == cmdBack) && (Display.getDisplay(this).getCurrent() == frmAdvanced))
    {
      showConnectionForm();
    }
    else if (c == cmdConnect)
    {
      System.out.println("chat command!");
      ChatClient chatClient = new ChatClient(this);
      chatClient.start();
    }
    else
    { System.out.println(c.getLabel()); }
  }

  public void commandAction(Command c, Item item) {
    if (c == cmdConnect)
    {
      System.out.println("chat command!");
      ChatClient chatClient = new ChatClient(this);
      chatClient.start();
    }
  }


  protected void destroyApp(boolean unconditional)
  {
  }

  protected void pauseApp()
  {
  }

}
