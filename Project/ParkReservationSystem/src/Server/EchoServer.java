package Server;

import java.io.*;
import java.net.InetAddress;
import java.rmi.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import gui.ServerPortFrameController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ocsf.server.*;
import logic.ClientConnectionStatus;
import logic.Order;

/**
 * This class overrides some of the methods in the abstract superclass to provide
 * specific functionality for an order management server.
 */
public class EchoServer extends AbstractServer {
    // Class variables
    final public static int DEFAULT_PORT = 5555;
    
    
    public static ObservableList<ClientConnectionStatus> clientsList = FXCollections.observableArrayList();

    public  String dbCMessage="";
    // Constructor
    public EchoServer(int port) {
        super(port);
    }
    
    @SuppressWarnings("unused")
	private static Connection createDbConnection() {
  	  try 
  		{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            System.out.println("EchoServer> Driver definition succeed");
        } catch (Exception ex) {
        	/* handle the error*/
        	System.out.println("EchoServer> Driver definition failed ");
        	 }
        try 
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/gonaturedb?serverTimezone=IST","root","Aa123456");
          
            System.out.println("SQL connection succeed");
            return conn;
            //createTableCourses(conn);
     	} catch (SQLException ex) 
     	    {/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            }
        return null;
    }

    // Instance methods
    /**
     * This method handles any messages received from the client.
     * 
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("EchoServer> Message received: " + (String)msg + " from " + client);
        String message = (String) msg.toString();
        System.out.println("EchoServer> " + message);
        String[] result = message.split(" ");
       // System.out.println(result[0]);
        if (result[0].equals("1")) {
        	String Ip = client.toString() + " " ;
        	String[] clientIp=Ip.split(" ");
        	String hostIp= getHostIp();
        	ClientConnectionStatus thisClient=clientConnection(clientIp[0],hostIp);
        	updateClientConnect(thisClient);
        }
        String [] details=new String[result.length-1];
        for(int i=0; i<details.length;i++) {
        	details[i]=result[i+1];

        }   
        if (details.length < 1) {
            handleErrorMessage(client, "Invalid message format");
            return;
        }
        //System.out.println(details[1]);
        switch (details[0]) {
            case "updateOrderDetails":
                if (updateOrderDetails(details) == 1) {
                    sendToClient(client, "Order was successfully updated into the database!");
                } else {
                    sendToClient(client, "Order failed to be updated into the database!");
                }
                break;
            case "orderExist":
                if (details.length < 2) {
                    handleErrorMessage(client, "Invalid message format");
                    return;
                }
                if (orderExist(details[1]) == 1) {
                    sendToClient(client, "Order exists");
                } else {
                    sendToClient(client, "Order does not exist");
                }
                break;
            case "loadOrder":
                if (details.length < 2) {
                    handleErrorMessage(client, "Invalid message format");
                    return;
                }
                Connection conn = createDbConnection();
                Order order = DbController.loadOrder(conn, details[1]);
                if(order!=null) {
                	sendToClient(client,order.toString());
                	return;}
                else {
                sendToClient(client, "Failed to load order");
                break;
                }
            default:
                handleErrorMessage(client, "Unknown command");
        }
    }
    
    

	public void updateClientConnect(ClientConnectionStatus thisClient) {
		ClientConnectionStatus client = new ClientConnectionStatus(thisClient.ip, thisClient.host, thisClient.status);
		if(clientsList.indexOf(client) == -1 ) {
			clientsList.add(client);
		}	
		else {
			clientsList.remove(clientsList.indexOf(client));
			clientsList.add(client);
		}
		//ClientConnectionStatus.WriteToFile(client.ip+ " " +client.host+" "+client.status+ " "+ client.startTime+ " "  );
		System.out.println(client.ip +" Connected succsessfully!");
	}
	//ServerPortFrameController newserver=new ServerPortFrameController();
	

	private ClientConnectionStatus clientConnection(String clientIp, String hostIp) {
		//System.out.println("test from client connection");
		ClientConnectionStatus clientStatus = new ClientConnectionStatus(clientIp,hostIp,"Connected");
		return clientStatus;	
	}

	private void sendToClient(ConnectionToClient client, String message) {
        try {
            client.sendToClient(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleErrorMessage(ConnectionToClient client, String errorMessage) {
        sendToClient(client, errorMessage);
    }


public static int orderExist(String order) {
	  Connection conn = createDbConnection();
	  int exist=DbController.searchOrder(conn,order);

	  if (exist==1) {return 1;} 
	  else {return 0;}
}

public static int updateOrderDetails(String[] orderdetails) {
	int update=0;
	Connection conn = createDbConnection();
	System.out.println("EchoServer> Sending the data to dbc ");
	update=DbController.updateOrder(conn,orderdetails);
	return update;
}

    protected void serverStarted() {
        System.out.println("EchoServer> Server listening for connections on port " + getPort());
    }

    protected void serverStopped() {
        System.out.println("EchoServer> Server has stopped listening for connections.");
    }
    
	public static String getHostIp() {
		try {
			InetAddress localHost= InetAddress.getLocalHost();
			return localHost.getHostAddress();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getClientIp(ConnectionToClient client) {
	    return client.getInetAddress().getHostAddress();
	}
}
