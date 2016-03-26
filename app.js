var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs= require("fs");
var mysql= require("mysql");
server.listen(process.env.PORT || 3000);

app.get("/", function(req, res){
	res.sendFile(__dirname + "/index.html");
	
});

//Thiet lap ket noi mysql
var connection = mysql.createConnection({
	host : 'localhost',
	user : 'root',
	password : '12345',
	database : 'khancap'
	
});
connection.connect();
	
io.sockets.on('connection', function (socket){
	
	console.log("Co nguoi connect ne");
	
	//Kiem tra Dang nhap
	socket.on('client-gui-taikhoan', function(data){
		var ketqua = false;
		
		connection.query("SELECT * FROM khachhang where email=? and password=?",[data.email,data.password],	
			function(err, rows, fields){
			if(err) throw err;
			
			if(rows.length>0)
			{
				socket.emit('server-gui-KetQuaDangNhap', { ketqua: true, tenkh: rows[0].tenkh });	
			}				
			else
			{
				socket.emit('server-gui-KetQuaDangNhap', { ketqua: false });
				
				io.sockets.emit('server-gui-bando', { x:10.9833172, y:106.6655848 });
			}
		});
	});
	
	//Lay noi dung
	socket.on('client-gui-khancap', function(data){
		connection.query("INSERT INTO noidung VALUES ('','','','',?,?,CURRENT_TIMESTAMP,1)",[data.hinh, data.amthanh],	
			function(err, rows, fields){
			if(err) throw err;
			
			if(rows.affectedRows > 0)
			{
				connection.query("SELECT * FROM noidung order by id desc",	
					function(err2, rows2, fields2){
					if(err2) throw err2;
					
					if(rows2.length > 0)
					{
						console.log("da insert noi dung");
						io.sockets.emit('server-gui-noidung', { noidung: rows2 });
						io.sockets.emit('server-gui-thongbao', { ketqua: true });
						
					}				
				});
			}				
			else
			{
				console.log("insert that bai");
			}
		});
	});
	
	//Yeu cau noi dung
	socket.on('client-yeucau-noidung', function(data){
		connection.query("SELECT * FROM noidung order by id desc",
			function(err, rows, fields){
			if(err) throw err;
			
			if(rows.length > 0)
			{
				socket.emit('server-gui-noidung', { noidung: rows });
			}				
		});
	});
	
	//Ngat ket noi
	socket.on('disconnect', function(data){
		console.log('da thoat');
		
	});

	
});


