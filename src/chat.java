import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class chat {
	
	static Socket socket = null;
	
	public static void main (String[] args) throws IOException {
		ServerSocket serverSocket = null;
		ArrayList<Socket> client;
		HashMap<String,Socket>hm;
		String a=null;
		DataOutputStream dataOutputStream;
		DataInputStream dataInputStream2;
		try {
			serverSocket = new ServerSocket(5776); //소켓 지정
			client = new ArrayList<Socket>(); // 
			
			while(true) {
				socket = serverSocket.accept(); // 접속대기
				System.out.println(socket+"접속"); //클라이언트가 접속하면 해당 ip 주소로 보여줌
				client.add(socket);//접속한 클라이언트를 어레이에 저장
				new ChatServer(socket,client).start();
				
			}
			
		}catch (Exception e) {
			
			// TODO: handle exception
		}
		
	}

}

class ChatServer extends Thread{
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	Socket serversocket;
	ArrayList<Socket> client;
	ArrayList<String> data;
	String text = null;
	String a= null;
	JSONArray jArr;
	JSONObject obj1;
	JSONObject jsonObject;
	public ChatServer(Socket socket , ArrayList<Socket> clients) { //생성자 소켓이랑 어레이 선언
		this.serversocket = socket;
		this.client = clients;
		try {
			data =new ArrayList<String>();
			dataInputStream = new DataInputStream(serversocket.getInputStream()); //초기화
			a=dataInputStream.readUTF();
			data.add(a);
			System.out.println(data);
			dataOutputStream = new DataOutputStream(serversocket.getOutputStream());
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			System.out.println(e+"생성자");
		}
		
	}
	
	public void run() { //클라이언트에서 받은부분을 이클립스에 저장
			while(true) {
				try {
					text = dataInputStream.readUTF();
					if(text.length()!=0) { //받은값이 있으면 저장
						System.out.println("안드로이드에서 받은부분" + text);
						sendmessage(text);
					}
					
									
				}catch (Exception e) {
					
					for(int i =0; i<client.size(); i++) {//접속한 클라이언트 수 만큼 for 돌림
						if(client.get(i)==serversocket) {//접속한 클라이언트중에 소켓이 끊긴 클라이언트를 찾음
							System.out.println(client.get(i)+"접속종료");
							client.remove(i);//해당 if문이 맞으면 접속 끊긴 클라이언트 삭제
							break;
						}
					}
					// TODO: handle exception
				}
				
			}
			
	}
	
	public void sendmessage(String message ) { //안드로이드에 다시 메세지 보내기
		jArr = new JSONArray();
		obj1 = new JSONObject();
		jsonObject = new JSONObject();
		
	    obj1.put("id",a);
	    obj1.put("message",message);
	    
	    jArr.add(obj1);
	    
	    jsonObject.put("tcp",jArr);

		String jsonInfo = jArr.toJSONString();

		
		for(Socket s : client) { //접속한 클라이언트에게 전부다 보내기
			try {
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF(jsonInfo);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				try {
					serversocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(e+"send");
			}
		}
		
		
	}
	
	
}



