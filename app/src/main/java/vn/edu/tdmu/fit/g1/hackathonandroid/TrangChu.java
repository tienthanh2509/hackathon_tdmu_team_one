package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TrangChu extends AppCompatActivity {

    Database db = new Database(this);
    StarterApplication st;
    Socket mSocket;

    Button btnDangXuat;
    ListView lvTrangChu;

    //Gui am thanh
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        st = ((StarterApplication)getApplicationContext());
        mSocket = st.getmSocket();

        mSocket.off("server-gui-noidung");
        mSocket.on("server-gui-noidung", LayNoiDung);

        mSocket.off("server-gui-thongbao");
        mSocket.on("server-gui-thongbao", ThongBao);

        mSocket.emit("client-yeucau-noidung", "abc");

        btnDangXuat = (Button)findViewById(R.id.buttonDangXuat);
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.off("server-gui-noidung");
                mSocket.off("server-gui-thongbao");
                db.DB_QueryData("DELETE FROM khachhang");
                Intent mhMain = new Intent(TrangChu.this, MainActivity.class);
                startActivity(mhMain);
                finish();
            }
        });

        lvTrangChu = (ListView)findViewById(R.id.listViewTrangChu);
        lvTrangChu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThongBao item = (ThongBao) parent.getItemAtPosition(position);
                if(myPlayer!=null && myPlayer.isPlaying()){
                    myPlayer.release();
                    myPlayer=null;
                }
                byte[] amthanh=item.amthanh;
                if(amthanh.length>50)
                    playMp3FromByte(amthanh);

            }
        });
    }

    private Emitter.Listener LayNoiDung = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray noidung;
                    ArrayList<ThongBao> mangThongBao = new ArrayList<ThongBao>();
                    try {
                        //Toast.makeText(getApplicationContext(),"nhan data",Toast.LENGTH_SHORT).show();
                        noidung = data.getJSONArray("noidung");
                        byte[] hinh, amthanh;
                        JSONObject row_js;
                        for (int i = 0; i < noidung.length(); i++) {
                            row_js = noidung.getJSONObject(i);

                            hinh = (byte[]) row_js.get("data");
                            amthanh = (byte[]) row_js.get("amthanh");

                            mangThongBao.add(new ThongBao(row_js.getString("id"), row_js.getString("noidung"), hinh, amthanh, row_js.getString("thoigian"), row_js.getString("status")));
                        }

                        ThongBaoAdapter adapter = new ThongBaoAdapter(getApplicationContext(),R.layout.activity_dong_thong_bao,mangThongBao);
                        lvTrangChu.setAdapter(adapter);

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener ThongBao = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    Intent intent = new Intent(getApplicationContext(),TrangChu.class);

                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    Notification n  = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Thông báo khẩn cấp")
                            .setContentText("Bạn nhận được một thông báo khẩn cấp")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .setSound(alarmSound).build();


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(1, n);
                }
            });
        }
    };

    private void playMp3FromByte(byte[] mp3SoundByteArray) {
        try {

            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            myPlayer = new MediaPlayer();

            FileInputStream fis = new FileInputStream(tempMp3);
            myPlayer.setDataSource(fis.getFD());

            myPlayer.prepare();
            myPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
