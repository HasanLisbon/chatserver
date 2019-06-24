import java.io.*;
import java.lang.management.BufferPoolMXBean;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String host="";
        String port;
        String msg = "";
        BufferedReader bReader= new BufferedReader(new InputStreamReader(System.in));
        try {
            //asking for the hostname
            System.out.println("What is your host?");
            host=bReader.readLine();
            //asking for the port number
            System.out.println("What is your port number?");
            port=bReader.readLine();

            //System.out.println(port);
            //creating client socket
            Socket clientSocket= new Socket(host, Integer.parseInt(port));

            PrintWriter out= new PrintWriter(clientSocket.getOutputStream(),true);

            BufferedReader in= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(!msg.equals("quit")){
                System.out.println("Write your message here:");
                msg=bReader.readLine();
                out.println(msg);
                System.out.println(in.readLine());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
