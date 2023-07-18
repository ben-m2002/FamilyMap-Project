package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Request.RegisterRequest;
import Result.RegisterResult;

public class RegisterTask implements Runnable{
    final private Handler messageHandler;
    private RegisterRequest req;

    public RegisterTask (Handler messageHandler, RegisterRequest request){
        this.messageHandler = messageHandler;
        this.req = request;
    }

    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy("10.0.2.2","8080");
        RegisterResult result = proxy.Register(this.req);
        sendMessage(result);
    }

    public void sendMessage (RegisterResult res){
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putString("username", res.getUsername());
        messageBundle.putString("authtoken", res.getAuthtoken());
        messageBundle.putString("personID", res.getPersonID());
        messageBundle.putString("message", res.getMessage());
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }

}
