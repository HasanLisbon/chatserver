import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        String msg= "";
        try {
            ServerSocket serverSocket= new ServerSocket(8080);

            Socket clientSocket= serverSocket.accept();

            PrintWriter out= new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(!msg.equals("quit")){
                msg=in.readLine();
                out.println(msg);
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
