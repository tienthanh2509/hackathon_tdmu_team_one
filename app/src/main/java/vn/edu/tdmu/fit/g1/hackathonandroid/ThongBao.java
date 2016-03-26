package vn.edu.tdmu.fit.g1.hackathonandroid;

/**
 * Created by NguyenHuyLinh on 3/26/2016.
 */
public class ThongBao {
    public String id;
    public String noidung;
    public byte[] data;
    public byte[] amthanh;
    public String thoigian;
    public String status;

    public ThongBao(String id, String noidung, byte[] data, byte[] amthanh, String thoigian, String status) {
        this.id = id;
        this.noidung = noidung;
        this.data = data;
        this.amthanh = amthanh;
        this.thoigian = thoigian;
        this.status = status;
    }
}
