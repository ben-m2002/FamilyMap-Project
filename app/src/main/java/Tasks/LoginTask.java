package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Request.LoginRequest;
import Result.LoginResult;
import Result.RegisterResult;

public class LoginTask implements Runnable {
    private final Handler messageHandler;
    private LoginRequest request;

    public LoginTask (Handler messageHandler, LoginRequest req){
        this.messageHandler = messageHandler;
        this.request = req;
    }

    @Override
    public void run (){
        ServerProxy proxy = new ServerProxy("10.0.2.2","8080");
        LoginResult result = proxy.Login(request);
        sendMessage(result);
    }

    private void sendMessage (LoginResult res){
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putString("username", res.getUsername());
        messageBundle.putString("authToken", res.getAuthtoken());
        messageBundle.putString("personID", res.getPersonID());
        messageBundle.putString("message", res.getMessage());
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }

}
