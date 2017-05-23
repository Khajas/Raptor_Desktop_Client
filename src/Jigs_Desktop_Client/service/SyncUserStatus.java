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
package Jigs_Desktop_Client.service;

import Jigs_Desktop_Client.GUI.FXMLDocumentController;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.application.Platform;
import javafx.scene.text.Text;

/**
 *
 * @author Anwar
 */
public class SyncUserStatus implements Runnable{
    private boolean first_messages_scan;
    private String hostname;
    private Thread main_thread;
    private final FXMLDocumentController fx_display;
    public SyncUserStatus(String hostname
                        ,FXMLDocumentController fx_display){
        this.hostname=hostname;
        this.fx_display=fx_display;
    }
    private String processRequest(String connect_url)
                    throws MalformedURLException, IOException{
        String server_response="";
        String httpQuery=connect_url;
        URL url;
        try{
            url = new URL(httpQuery); 
        }catch(MalformedURLException e){
            throw new MalformedURLException("Invalid query!");
        }
        HttpURLConnection connection = this.connectServer(url); 
        String responseCode=connection.getResponseMessage();
        if(responseCode.equals("OK")){
            server_response=this.readResponse(connection);
        }
        connection.disconnect();
        return server_response;    
    }
    /**
     * Makes connection to server for the given URL
     * @param url
     *      API URL that should be called to serve the request.
     * @return HttpURLConnection
     *      Returns the connection variable from the above URL for GET method
     */
    private HttpURLConnection connectServer(URL url){
         HttpURLConnection connection;
         try{
             connection = (HttpURLConnection) url.openConnection(); 
             connection.setDoOutput(true); 
             connection.setInstanceFollowRedirects(false); 
             connection.setRequestMethod("GET");
         }catch(IOException e){
             throw new RuntimeException(e);
         }
         return connection;
     }
    public String readUsers() 
            throws MalformedURLException, IOException{
        return this.processRequest("http://"+hostname+":8080/jigs/webapi/user/retreive/allusers");
    }
    /**
     * Reads response from the given HttpURLConnection
     * @param connection
     *      URL connection to the request API.
     * @return server_response
     *      Response received after opening the connection.
     * @throws IOException 
     *      Signals that an I/O exception of some sort has occurred.
     */
    private String readResponse(HttpURLConnection connection ) throws IOException{
        String server_response="";
        InputStream response=(InputStream)connection.getContent();
        int numBytes=response.available();
        for(int i=0;i<numBytes;++i)
           server_response+=((char)response.read());
        return server_response;
    }
    
    @Override
    public void run(){
        this.first_messages_scan=true;
        while(true){
            final String message;
            try {
                String response=this.readUsers();
                System.out.println(response+" for user: "+response);
                if(response==null)
                    message="Error reading status";
                Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Retreviing statussssss");
                            fx_display.getMyCirclesList();
                        }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            first_messages_scan=false;
        }
    }
}