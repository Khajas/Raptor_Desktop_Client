/*
 * Copyright (C) 2017 Anwar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Jigs_Desktop_Client.GUI;

import Jigs_Desktop_Client.resources.Message;
import Jigs_Desktop_Client.resources.User;
import Jigs_Desktop_Client.service.Jigs_client;
import Jigs_Desktop_Client.service.SyncMessages;
import Jigs_Desktop_Client.service.SyncUserStatus;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Anwar
 */
public class FXMLDocumentController implements Initializable, Runnable{
    
    private String to_user;
    private static String from_user;
    private final Jigs_client jc;
    private final ObservableList<String> messages;
    private final ObservableList<String> circles;
    private final ObservableList<UserStatus> users_available;
    Thread syncmessageThread;
    Thread syncUserStatusThread;
    
    @FXML
    private AnchorPane main_panel;
    
    @FXML
    private TabPane main_tab_pane;
    
    // First tab
    
    @FXML
    private TextField user_name;
    
    @FXML
    private PasswordField password;
    
    @FXML
    private Button btn_login;
    
    @FXML
    private Button btn_logout;
    
    @FXML
    private Button btn_exit;
    
    @FXML
    private Label login_status;
    
    // Second tab
    
    @FXML
    private ImageView spin_earth_image;
    
    @FXML
    private Tab my_circle_tab;
    
    @FXML
    private Button connect_selected_user;
    
    @FXML
    private ListView<UserStatus> my_circle_list;
    
    @FXML
    private Label selected_user;
    
    @FXML
    private TextField circle_username_search;
    
    @FXML
    private Button btn_circle_user_search;
    
    // Third tab
    @FXML
    private Tab message_tab;
    
    @FXML
    private TextField search_user;
    
    @FXML
    private Button btn_disconnect;
    
    @FXML
    private Circle connection_circle;
    
    @FXML
    private Label search_user_result;
    
    @FXML
    private ListView<String> messages_list;
    
    @FXML
    private TextArea text_message;
    
    @FXML
    private Button btn_send;
    
    // Constructor
    public FXMLDocumentController(){
        jc=new Jigs_client();
        messages=FXCollections.observableArrayList();
        circles=FXCollections.observableArrayList();
        users_available=FXCollections.observableArrayList();
    }
    
    @FXML
    private void searchUser(ActionEvent event){

    }
    
    @FXML
    private void allUsers(ActionEvent event){
        
    }
    
    @FXML
    private void connectUser(ActionEvent event){
        String user_to_search=this.to_user;
        System.out.println("Connect is pressed");
        System.out.println("Called Connect user: "+to_user);
        this.connectToUser(user_to_search);
        this.btn_disconnect.setDisable(false);
    }
    
    private void connectToUser(String user_to_search){
        if(user_to_search.equals("")) user_to_search=this.to_user;
           try {
            System.out.println("Searching for: "+user_to_search);
            if(jc.findUser(user_to_search)){
                String to_user_status="offline";
                this.to_user=user_to_search;
                for(UserStatus us: this.users_available){
                    if(us.getUsername().equalsIgnoreCase(user_to_search))
                        to_user_status=us.getStatus();
                }
                if(to_user_status.equals("online")){
                    this.connection_circle.setFill(Color.LIGHTGREEN);
                    search_user_result.setText("Connected to @"+user_to_search);
                    this.selected_user.setText("Connected to @"+user_to_search);
                    try {
                    this.spin_earth_image.setImage(new Image(getClass().
                        getResource("resources/spin_earth_bright.gif").toURI().toString()));
                    } catch (URISyntaxException ex) {
                        this.alert("resources/app_error.mp3");
                    }
                }
                else{
                    this.connection_circle.setFill(Color.YELLOW);
                    search_user_result.setText("Offline messaging @"+user_to_search);
                    this.selected_user.setText("Sending offline messages @"+user_to_search);
                }
                this.btn_send.setDisable(false);
                this.alert("resources/app_user_connected.mp3");
                main_tab_pane.getSelectionModel().select(2);
            }
            else{
                this.connection_circle.setFill(Color.RED);
                search_user_result.setText("No user found for @"
                        +user_to_search);
                this.btn_send.setDisable(true);
                this.alert("resources/app_no_such_user.mp3");
            }
        } catch (IOException ex) {
            this.connection_circle.setFill(Color.RED);
            search_user_result.setText(ex.getMessage());
            this.btn_send.setDisable(true);
            this.alert("resources/app_error.mp3");
        }
    }
   
    @FXML
    private void sendMessage(ActionEvent event){
        String message=this.text_message.getText();
        this.text_message.clear();
        if(!message.equals("")){
            if(this.to_user!=null){
                System.out.println("Sending message: "+message+
                        "To "+to_user+" from "+from_user);
                jc.sendMessage(new Message(new User(to_user), new User(from_user), message));
                String your_message="You("+this.from_user+"): "+message+"@"+to_user;
                your_message=WordUtils.wrap(your_message, 42);
                messages.add(your_message);
                this.messages_list.setItems(messages);
                this.alert("resources/send_msg.mp3");
            }
        }
        System.out.println("Send is Pressed");
    }
    
    @FXML
    private void authenticateUser(ActionEvent event){
        from_user=this.user_name.getText();
        String pass_txt=this.password.getText();
        try{
            MessageDigest m=MessageDigest.getInstance("MD5");
            m.update(pass_txt.getBytes(),0,pass_txt.length());
            pass_txt=(new BigInteger(1,m.digest()).toString(16));
        }catch(NoSuchAlgorithmException e){
            this.login_status.setText(e.getMessage());
            this.alert("resources/app_error.mp3");
            return;
        }
        if((from_user.equals("")) || (pass_txt.equals(""))){
            this.login_status.setText("Username or password can't left empty!");
            this.alert("resources/app_error.mp3");
            return;
        }
        if(jc.authenticateUser(from_user, pass_txt)){
            this.alert("resources/app_login.mp3");
            this.login_status.setText("Logged In as: @"+from_user);
            this.my_circle_tab.setDisable(false);
            message_tab.setDisable(false);
            this.btn_logout.setDisable(false);
            this.user_name.setDisable(true);
            this.password.setDisable(true);
            this.btn_login.setDisable(true);
            main_tab_pane.getSelectionModel().select(1);
            this.getMyCirclesList();
            SyncMessages sm=new SyncMessages(this.from_user,"132.148.65.148"
                            ,this);
            syncmessageThread=new Thread(sm);
            syncmessageThread.start();
            SyncUserStatus sus=new SyncUserStatus("132.148.65.148", this);
            syncUserStatusThread=new Thread(sus);
            syncUserStatusThread.start();
            this.messages.clear();
            this.btn_login.setDefaultButton(false);
            this.btn_logout.setDefaultButton(true);
            this.jc.setStatus(from_user, "online");
            this.btn_disconnect.setDisable(true);
        }
        else{
            this.login_status.setText("Error logging as @"+from_user);
            this.alert("resources/app_error.mp3");
        }
    }
    
    @FXML
    private void logout(){
        this.alert("resources/app_logout.mp3");
        this.user_name.setText("");
        this.user_name.setDisable(false);        
        this.password.setText("");
        this.password.setDisable(false);
        this.btn_login.setDisable(false);
        this.btn_logout.setDisable(true);
        this.message_tab.setDisable(true);
        if(syncmessageThread.isAlive())
            syncmessageThread.stop();
        if(syncUserStatusThread.isAlive())
            syncUserStatusThread.stop();
        this.login_status.setText("");
        this.my_circle_tab.setDisable(true);
        this.btn_login.setDefaultButton(true);
        this.circles.clear();
        this.users_available.clear();
        this.jc.setStatus(from_user, "offline");
    }
    
    public void updateMessagesList(String message){
        message=WordUtils.wrap(message, 42);
        messages.add(message);
        if(!message.contains("No")){
            this.alert("resources/app_new_message.mp3");
            Stage stage = (Stage) this.main_panel.getScene().getWindow();
            if(stage.isIconified()){
                try {
                    stage.getIcons().setAll(new Image(getClass().getResource("resources/Chat_notification.png").toURI()
                            .toString()));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        messages_list.setItems(messages);
    }
    
    private void alert(String fileName){
        Media sound;
        try {
            sound = new Media(FXMLDocumentController.class.getResource(fileName).toURI().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        } catch (URISyntaxException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.alert("resources/app_launch.mp3");
        this.user_name.requestFocus();
        this.btn_logout.setDisable(true);
        this.jc.setStatus(from_user, "online");
        this.my_circle_list.setCellFactory(new Callback<ListView<UserStatus>, ListCell<UserStatus>>(){
            @Override
            public ListCell<UserStatus> call(ListView<UserStatus> userObj){
                ListCell<UserStatus> cell=new ListCell<UserStatus>(){
                  @Override
                  protected void updateItem(UserStatus usrObj, boolean btnl){
                      super.updateItem(usrObj, btnl);
                      if(usrObj!=null){
                          String filename=usrObj.getStatus();
                          if(filename==null || filename.equals("")
                                  || filename.equals("null"))
                            filename="offline";
                          else System.out.println(filename);
                          Image img=new Image(getClass().getResource("resources/"+filename+".png").toExternalForm());
                          ImageView imv=new ImageView(img);
                          setGraphic(imv);
                          setText(usrObj.getUsername());
                      }
                  }
                };
                return cell;
            }
        });
        this.main_panel.setBackground(Background.EMPTY);
        this.text_message.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent keyEvent) 
            {
                if(keyEvent.getCode() == KeyCode.ENTER)
                {
                    sendMessage(null);
                    keyEvent.consume();
                }
            }
        });
     }
    
    
    @FXML
    public void getMyCirclesList(){
        System.out.println("Getting all users");
        this.users_available.clear();
        String allUsers[];
        try {
            allUsers = this.jc.getMyCircles();
            for(String user: allUsers){
                String[] user_info=user.split(":");
                users_available.add(new UserStatus(user_info[0],user_info[1]));
            }
        } catch (IOException ex) {
            users_available.add(new UserStatus("Unable to fetch users!","offline"));
            users_available.add(new UserStatus(ex.getMessage(),"offline"));
        }
        users_available.add(new UserStatus("DemoOfflineUser1","offline"));
        this.my_circle_list.setItems(this.users_available);
    }

    @FXML
    private void showSelectedUser(MouseEvent event) {
        UserStatus usr=my_circle_list.getSelectionModel().getSelectedItem();
        if(usr==null) return;
        System.out.println("clicked on " +usr.getUsername());
        if(usr.getStatus().equalsIgnoreCase("online")){
            this.selected_user.setText("Connect to @"+usr.getUsername()+"?");
            this.connect_selected_user.setText("Connect");
        }
        else{
            this.selected_user.setText("Send offline message to @"+usr.getUsername()+"?");
            this.connect_selected_user.setText("Yes");
        }
        this.to_user=usr.getUsername();
    }
    
    @FXML
    private void exit_request(ActionEvent event){
        Stage stage = (Stage) this.main_panel.getScene().getWindow();
        stage.setIconified(true);
        this.alert("resources/app_exit.mp3");
        this.jc.setStatus(from_user, "offline");
        System.exit(0);
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @FXML
    private void disconnect(ActionEvent event){
        this.to_user="";
        this.btn_send.setDisable(true);
        this.connection_circle.setFill(Color.RED);
        this.search_user_result.setText("No active connections!");
        this.selected_user.setText("Select to user to connect!");
        try {
            this.spin_earth_image.setImage(new Image(getClass().
            getResource("resources/spin_earth_dark.gif").toURI().toString()));
        } catch (URISyntaxException ex) {
            this.alert("resources/app_error.mp3");
        }
        this.alert("resources/app_disconnect.mp3");
        this.btn_disconnect.setDisable(true);
    }
    
    public static void onClose(){
        (new Jigs_client()).setStatus(from_user, "offline");
    }
}
