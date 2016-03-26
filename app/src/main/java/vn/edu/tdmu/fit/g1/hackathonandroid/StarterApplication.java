package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by NguyenHuyLinh on 3/26/2016.
 */
public class StarterApplication extends Application {
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.43.73:3000");
        } catch (URISyntaxException e) {
        }
    }

    public Socket getmSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket.connect();


    }
}
