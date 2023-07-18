package Tasks;

import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;

import java.io.*;
import java.net.*;
import com.google.gson.*;

import Data.DataCache;
import Models.User;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.AllEventResult;
import Result.AllPersonResult;
import Result.LoginResult;
import Result.RegisterResult;

public class ServerProxy {

    String serverHost;
    String serverPort;


    public ServerProxy(String serverHost, String serverPort){
        // serverHost = 10.0.2.2
        // serverPort = 8080
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public ServerProxy(){

    }

    public RegisterResult Register (RegisterRequest req){

        try {
            // create the url
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            // construct our HTTP Request

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // make it a post

            http.setRequestMethod("POST");

            // open for request output

            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            // Connect to the sever

            http.connect();
            
            Gson gson = new Gson();
            String reqData = gson.toJson(req);

            //get the outputStream

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData,reqBody);
            reqBody.close();

            // get the response code

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                // success
                System.out.println("The user was successfully registered");
                InputStream is = http.getInputStream();
                String respData = readString(is);
                System.out.println(respData);

                // create a class from it
                GsonBuilder gb = new GsonBuilder();
                Gson gs = gb.create();
                RegisterResult rs = gs.fromJson(respData, RegisterResult.class);

                // Put the Auth Token in the Data Cache

                DataCache dc = DataCache.getInstance();
                dc.setAuthToken(rs.getAuthtoken());

                // task the GetDataTask

                GetDataTask tk = new GetDataTask();
                tk.getPersonsAndEvents();

                return rs;
            }
            else{
                // error
                System.out.println("Error: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                String respData = readString(resBody);
                System.out.println(respData);

                // create a class from it
                GsonBuilder gb = new GsonBuilder();
                Gson gs = gb.create();
                RegisterResult rs = gs.fromJson(respData, RegisterResult.class);

                return rs;
            }

        }
        catch ( IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public LoginResult Login (LoginRequest req){

        try {
            // create the url
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            // construct our HTTP Request

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // make it a post

            http.setRequestMethod("POST");

            // connect

            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(req);

            //get the outputStream

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData,reqBody);
            reqBody.close();

            // get the response code

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                // success
                System.out.println("The user has successfully logged in");
                InputStream is = http.getInputStream();
                String respData = readString(is);
                System.out.println(respData);

                // put the response in the Login Class
                GsonBuilder gb = new GsonBuilder();
                Gson gs = gb.create();
                LoginResult rs = gs.fromJson(respData, LoginResult.class);

                // Put the Auth Token in the Data Cache

                DataCache dc = DataCache.getInstance();
                dc.setAuthToken(rs.getAuthtoken());

                // task the GetDataTask

                GetDataTask tk = new GetDataTask();
                tk.getPersonsAndEvents();

                return rs;
            }
            else{
                // error
                System.out.println("Error: " + http.getResponseMessage());
                InputStream resBody = http.getErrorStream();
                String respData = readString(resBody);
                System.out.println(respData);

                // put the response in the Login Class
                GsonBuilder gb = new GsonBuilder();
                Gson gs = gb.create();
                LoginResult rs = gs.fromJson(respData, LoginResult.class);

                return rs;
            }

        }
        catch ( IOException e ){
            e.printStackTrace();
        }
        return null;
    }

    public AllPersonResult getAllPersons (){
        DataCache dc = DataCache.getInstance();
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", dc.getAuthToken());

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("Successfully got all the Persons from the server");
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                GsonBuilder gb = new GsonBuilder();
                Gson gson = gb.create();
                AllPersonResult rs = gson.fromJson(respData, AllPersonResult.class);
                resBody.close();
                return rs;
            }
            else{
                System.out.println("Error getting persons from the server");
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                GsonBuilder gb = new GsonBuilder();
                Gson gson = gb.create();
                AllPersonResult rs = gson.fromJson(respData, AllPersonResult.class);
                resBody.close();
                return rs;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public AllEventResult getAllEvents (){
        DataCache dc = DataCache.getInstance();
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", dc.getAuthToken());

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("Successfully got all the Events from the server");
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                GsonBuilder gb = new GsonBuilder();
                Gson gson = gb.create();
                AllEventResult es = gson.fromJson(respData, AllEventResult.class);
                resBody.close();
                return es;
            }
            else{
                System.out.println("Error getting Events from the server");
                InputStream resBody = http.getInputStream();
                String respData = readString(resBody);
                GsonBuilder gb = new GsonBuilder();
                Gson gson = gb.create();
                AllEventResult es = gson.fromJson(respData, AllEventResult.class);
                resBody.close();
                return es;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }


}
