import java.io.*;
import java.lang.management.BufferPoolMXBean;
import java.net.Socket;

public class Client {
    private String host = "";
    private String port;
    private String msg = "";
    private BufferedReader in;
    private Socket clientSocket;
    public static void main(String[] args) {
        Client client=new Client();
        client.connect();
    }
    public void connect() {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
            //asking for the hostname
            System.out.println("What is your host?");
            host = bReader.readLine();
            //asking for the port number
            System.out.println("What is your port number?");
            port = bReader.readLine();
            clientSocket = new Socket(host, Integer.parseInt(port));
            Thread thread=new Thread(new SendManagaer());
            thread.start();
            readMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            //System.out.println(port);
            //creating client socket
    public void readMessage() {
        try {
            in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String msgIn="";
            while (!clientSocket.isClosed()){
                msgIn=in.readLine();
                if(msgIn!=null){
                    System.out.println(msgIn);
                }else {
                    System.out.println("connection is close");
                    in.close();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SendManagaer implements Runnable{

        @Override
        public void run() {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Write your message here:");
                while (!clientSocket.isClosed()) {
                    msg = bReader.readLine();
                    out.write(msg);
                    out.newLine();
                    out.flush();
                }

                if(msg.equals("quit")) {
                    in.close();
                    out.close();
                    clientSocket.close();


                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
