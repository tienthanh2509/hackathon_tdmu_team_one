package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NguyenHuyLinh on 3/26/2016.
 */
public class ThongBaoAdapter extends ArrayAdapter<ThongBao> {

    public ThongBaoAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ThongBaoAdapter(Context context, int resource, List<ThongBao> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_dong_thong_bao, null);
        }

        ThongBao t = getItem(position);

        if (t != null) {
            // Anh xa + Gan gia tri
            ImageView img = (ImageView) v.findViewById(R.id.imageViewTB);
            TextView tvNoiDung = (TextView) v.findViewById(R.id.textViewNoiDung);

            Bitmap hinh = BitmapFactory.decodeByteArray(t.data, 0, t.data.length);

            img.setImageBitmap(hinh);

        }

        return v;
    }

}
