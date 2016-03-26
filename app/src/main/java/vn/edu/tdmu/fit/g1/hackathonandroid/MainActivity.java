package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Database db = new Database(this);
    StarterApplication st;
    Socket mSocket;

    Button btnKhanCap, btnDangNhap;
    ImageView imgTemp;

    int PICK_CAMERA_REQUEST = 8888;

    //Gui am thanh
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;

    Timer time;
    Boolean check_count=false;
    int count=0;


    //Toa do
    GPSLocation gpsLocation;
    Button btnGPSLocation, btnNetworkLocation;
    double latitude=0, longitude=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db.DB_QueryData("CREATE TABLE IF NOT EXISTS khachhang (id INTEGER PRIMARY KEY, tenkh NVARCHAR )");


        st = ((StarterApplication)getApplicationContext());
        mSocket = st.getmSocket();

        int check=0;
        Cursor kq = db.DB_GetData("SELECT * FROM khachhang where id=1");
        while (kq.moveToNext()){
            check++;
        }
        if(check>0){
            Intent mhTrangChu = new Intent(MainActivity.this,TrangChu.class);
            startActivity(mhTrangChu);
        }


        gpsLocation = new GPSLocation(MainActivity.this);
        if (gpsLocation.canGetLocation()) {

            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
        } else {
            gpsLocation.showSettingsAlert();
        }

        imgTemp = (ImageView)findViewById(R.id.imageViewTemp);

        btnKhanCap = (Button)findViewById(R.id.buttonKhanCap);
        btnKhanCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gpsLocation = new GPSLocation(MainActivity.this);
                if (gpsLocation.canGetLocation()) {

                    latitude = gpsLocation.getLatitude();
                    longitude = gpsLocation.getLongitude();
                } else {
                    gpsLocation.showSettingsAlert();
                }


                start(v);
                check_count=true;
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_CAMERA_REQUEST);
            }
        });

        btnDangNhap = (Button)findViewById(R.id.buttonDangNhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mhDangNhap = new Intent(MainActivity.this,DangNhap.class);
                startActivity(mhDangNhap);
            }
        });

        time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (check_count == true) {
                            count++;
                        }
                        if(count>9){
                            check_count=false;
                            count=0;
                            stop(getCurrentFocus());
                        }


                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            double w,h;
            w =  bitmap.getWidth();
            h = bitmap.getHeight();
            if(w > h){
                bitmap = Bitmap.createScaledBitmap(bitmap, 640, 360, true);
            }
            else{
                bitmap = Bitmap.createScaledBitmap(bitmap, 360, 640, true);
            }

            imgTemp.setImageBitmap(bitmap);

            if(check_count==true)
                stop(getCurrentFocus());
            String path=outputFile = Environment.getExternalStorageDirectory().
                    getAbsolutePath() + "/hackathon.3gpp";
            byte[] amthanh=FileLocal_To_Byte(path);

            JSONObject noidung = new JSONObject();
            byte[] hinh = ImageView_To_Byte(imgTemp);
            try {
                noidung.put("kinhdo",longitude);
                noidung.put("vido",latitude);
                noidung.put("hinh", hinh);
                noidung.put("amthanh",amthanh);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            mSocket.emit("client-gui-khancap", noidung);
        }
        if(resultCode==RESULT_CANCELED)
            stop(getCurrentFocus());
    }

    public byte[] ImageView_To_Byte(ImageView h){
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.chomuc);
        BitmapDrawable drawable = (BitmapDrawable) h.getDrawable();
        Bitmap bmp = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void start(View view){
        try {
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hackathon.3gpp";
            myRecorder = new MediaRecorder();
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myRecorder.setOutputFile(outputFile);

            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Bắt đầu ghi âm...",
                Toast.LENGTH_SHORT).show();
    }

    public void stop(View view){
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder  = null;

            Toast.makeText(getApplicationContext(), "Đã ngừng ghi âm...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public byte[] FileLocal_To_Byte(String path){
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }


}
