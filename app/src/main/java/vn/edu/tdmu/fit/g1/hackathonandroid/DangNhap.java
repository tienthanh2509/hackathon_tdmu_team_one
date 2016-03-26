package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class DangNhap extends AppCompatActivity {

    Database db = new Database(this);
    StarterApplication st;
    Socket mSocket;


    EditText edtEmail, edtPassword;
    Button btnLogin;
    private Emitter.Listener KetQuaDangNhap = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String ketqua;
                    try {
                        ketqua = data.getString("ketqua");
                        if (ketqua.equals("true")) {
                            db.DB_QueryData("DELETE FROM khachhang");
                            String tenkh = data.getString("tenkh");
                            String sql = "INSERT INTO khachhang VALUES (1,'" + tenkh + "')";
                            db.DB_QueryData(sql);

                            Intent mhTrangChu = new Intent(DangNhap.this, TrangChu.class);
                            startActivity(mhTrangChu);
                        } else {
                            Toast.makeText(getApplicationContext(), "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        st = ((StarterApplication) getApplicationContext());
        mSocket = st.getmSocket();

        mSocket.off("server-gui-KetQuaDangNhap");
        mSocket.on("server-gui-KetQuaDangNhap", KetQuaDangNhap);

        edtEmail = (EditText) findViewById(R.id.editTextEmail);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);

        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject taikhoan = new JSONObject();
                try {
                    taikhoan.put("email", edtEmail.getText().toString().trim());
                    taikhoan.put("password", edtPassword.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("client-gui-taikhoan", taikhoan);

            }
        });
    }
}
