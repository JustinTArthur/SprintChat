//IRC Communnications Library
//By Justin Turner Arthur

public class CommIRC
{
  private String network;
  private ChatClient parent;
  private Sender sender;

  private boolean isRegistered;

  /** Default Constructor **/
  public CommIRC(ChatClient parentClass)
  {
    parent = parentClass;
    sender = parent.getSender();
    isRegistered = false;
  }

  public void register(String nickname, String username, String realname)
  {
    sender.send("NICK " + nickname);
    sender.send("USER " + username + " hostname servername :" + realname);
    isRegistered = true;
  }

  public void join(String channel)
  {
    sender.send("JOIN " + channel);
  }

  public void pong(String server)
  {
    sender.send("PONG " + server);
  }

  public void parse(String irctext)
  {
    System.out.println("parsing has begun");
    if (irctext.charAt(0) == ':')
    {
      //System.out.println("Server command detected, process:");
      String prefix = new String(getToken(irctext,1,' '));
      //System.out.println("prefix has been set");

      String command = new String(getToken(irctext,2,' '));
      //System.out.println("command has been set");

      String[] params = getTokenLast(irctext,3,' ',':');
      //System.out.println("params have been set");

      String originator = new String(getToken(prefix,1,'!').substring(1,getToken(prefix,1,'!').length()));
      //System.out.println("originator has been set");


      if (command.equals("001"))
      {
        parent.onRegistered();
      }
      else if (command.equals("PRIVMSG"))
      {
        parent.onMessage(originator, params[0], params[1]);
      }
      else
      {
        System.out.println("unparsable command: " + command);
      }
    }
    else
    {
      if (irctext.substring(0,4).equals("PING"))
      {
        pong(getToken(irctext,2, ' ').substring(1,getToken(irctext,2, ' ').length()));
      }
    }
  }

  public boolean isRegistered()
  {
    return isRegistered;
  }

  public String getToken(String s, int token, char sep)
  {
    //First test if there is only one token:
    if (s.indexOf(sep) == -1)
    {
      //There is only one token, so return the whole string:
      return s;
    }
    else
    {
      //There is more than one token, so continue:
      StringBuffer currentToken = new StringBuffer("");
      int currentIndex = 0;
      for (int i = 1; i <= token; i++)
      {
        //System.out.println("in getToken, about to access substring: ");
	//System.out.println(currentIndex + firstCheck(i));
	//System.out.println(s.indexOf(sep, currentIndex + 1) - lastCheck(s.lastIndexOf(sep), currentIndex));
	//System.out.println("where length is: " + s.length());
        currentToken.delete(0, currentToken.length());
        if (s.indexOf(sep, currentIndex + 1) != -1)
	{
	  currentToken.append(s.substring(currentIndex + firstCheck(i), s.indexOf(sep, currentIndex + 1)));
	}
	else
	{
	  currentToken.append(s.substring(currentIndex + firstCheck(i), s.length()));
	}
        currentIndex = s.indexOf(sep, ++currentIndex);
      }
    return currentToken.toString();
    }
  }

  // Method for getting tokens where a character indicates trailing string.
  public String[] getTokenLast(String s, int startToken, char sep, char trailSep)
  {
    //Collect all data that follows startToken's index
    StringBuffer sbDataFromStartOnward = new StringBuffer();
    for (int i = startToken; i <= numTokens(s, sep); i++)
    {
      sbDataFromStartOnward.append(getToken(s, i , sep));
      //replace any seperators that were removed by getToken
      if (i != numTokens(s, sep))
      {
        sbDataFromStartOnward.append(sep);
      }
    }
    String head = new String(getToken(sbDataFromStartOnward.toString(), 1, trailSep).substring(0,getToken(sbDataFromStartOnward.toString(), 1, trailSep).length() - 1));
    String tail = new String(getToken(sbDataFromStartOnward.toString(), 2, trailSep));
    System.out.println("head = " + head);
    System.out.println("Number of toks in head: " + numTokens(head, ' '));
    System.out.println("tail = " + tail);
    String[] sarrayParameters = new String[numTokens(head, ' ') + 1];
    for (int i = 0; i <= (numTokens(head, ' ') - 1); i++)
    {
      sarrayParameters[i] = new String(getToken(head, i + 1, ' '));
    }
    sarrayParameters[sarrayParameters.length - 1] = new String(tail);
    return sarrayParameters;
  }


  //Method for dertermining the number of tokens present:
  public int numTokens(String s, char sep)
  {
    //System.out.println("numTokens invoked where string length is " + s.length());
    int currentSeps = 0;
    //Check for existance of seperator:
    if (s.indexOf(sep) == -1)
    {
      //No instances of sep found:
      return 1;
    }
    else
    {
      for (int i = 0; i <= (s.length() - 1); i++)
      {
        if (s.charAt(i) == sep) { currentSeps++; }
      }
      return currentSeps + 1; //Add 1 because there is always one more token then seperator
    }
  }

  //Method for adding one to index unless currently checked token is the first.
  public int firstCheck(int i)
  {
    if (i == 1)
    {
      return 0;
    }
    else
    {
      return 1;
    }
  }

  //Method for subtracting one from index unless currently checked token is the last.
  public int lastCheck(int a, int b)
  {
    if (a == b)
    {
      return 0;
    }
    else
    {
      return 1;
    }
  }

}