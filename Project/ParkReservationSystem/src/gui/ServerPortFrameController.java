package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ClientConnectionStatus;
import ocsf.server.ConnectionToClient;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import Server.EchoServer;
import Server.ServerUI;
public class ServerPortFrameController implements Initializable{
    @FXML
    private Circle Circle;

    @FXML
    private TextField dbNameField;

    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private TextField dbUserNameField;

    @FXML
    private TextField ipAddress;

    @FXML
    private TextField portField;
    
    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;
    
    @FXML
    private Label statusServer;
    
    @FXML
    private TableView<ClientConnectionStatus> tableField;
    
    @FXML
    private TableColumn<ClientConnectionStatus, String> hostColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> ipColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> startTimeColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> statusColumn;
    
    
        
	private String getport() {
		return portField.getText();			
	}

    @FXML
	public void handleStartServerAction(ActionEvent event) {
		String p;
		tableField.setItems(EchoServer.clientsList);
        this.ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        this.hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		p=getport();
		if(p.trim().isEmpty()) {
			System.out.println("You must enter a port number");			
		}
		else
		{
			((Node)event.getSource()).getScene().getWindow(); 
			@SuppressWarnings("unused")
			Stage primaryStage = new Stage();
			@SuppressWarnings("unused")
			FXMLLoader loader = new FXMLLoader();
			ServerUI.runServer(p);
			statusServer.setText("Server Started Successfully");
			Circle.setFill(javafx.scene.paint.Color.GREEN);
			
		}
	}

    public void start(Stage primaryStage) throws IOException {
        // Load the ServerPortFrameController's FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerPortFrame.fxml")); // Ensure the path is correct
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
        primaryStage.setScene(scene);
        @SuppressWarnings("unused")
		ServerPortFrameController controller = loader.getController();
        primaryStage.setTitle("Server Control Panel");
        primaryStage.show();
    }
    
    @FXML
    void stopServerAction(ActionEvent event) throws Exception {
    	System.exit(0);
    }
   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
        // Initialization logic for controller
    	dbNameField.setText("jdbc:mysql://localhost/gonaturedb?serverTimezone=IST");
    	dbUserNameField.setText("root");
    	dbPasswordField.setText("Aa123456");
    	ipAddress.setText(EchoServer.getHostIp());
	}  
}
