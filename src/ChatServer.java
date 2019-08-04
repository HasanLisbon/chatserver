import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



public class ChatServer {
    private ServerSocket bindingSocket;
    private List<ChatServerWorker> workers= Collections.synchronizedList(new ArrayList<ChatServerWorker>());
    private int workerId=0;
    private final int PORT_NUM=7171;

    public static void main(String[] args) {
        ChatServer chatServer=new ChatServer();
        chatServer.listen();
    }

    /**
     *This method listen to the communication by the client
     */
    private  void listen() {
        try {
            bindingSocket=new ServerSocket(PORT_NUM);
            serve(bindingSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates a socket for the client and creates new thread when each client connects to the server
     * @param bindingSocket
     */
    private void serve(ServerSocket bindingSocket) {
        while(true){
            try {
                Socket clientSocket=bindingSocket.accept();
                String name="Client"+workerId;
                ChatServerWorker chatServerWorker=new ChatServerWorker(clientSocket, name);
                workerId++;
                workers.add(chatServerWorker);

                Thread thread= new Thread(chatServerWorker,name);
                thread.setName(name);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private class ChatServerWorker implements  Runnable{
        private Socket clientSocket;
        private BufferedReader in;
        private String messageIn="";
        private String name;

        /**
         *
         * @param clientSocket
         * @param name
         */
        public ChatServerWorker(Socket clientSocket, String name) {
            this.clientSocket=clientSocket;
            this.name=name;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * this method receives the message through clientsocket
         */
        public void receiveMessage(){
            try {
                while (!clientSocket.isClosed()){
                    in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    messageIn=in.readLine();
                    System.out.println(Thread.currentThread().getName()+" : "+messageIn);
                    if (messageIn==null || messageIn.equals("/quit")){
                        in.close();
                        clientSocket.close();
                        continue;
                    }else if (messageIn.equals("/alias")){
                        name=in.readLine();
                        System.out.println(name);
                        setName(name);
                    }else if (messageIn.equals("/list")){
                        reply(this, "list of clients: "+"\n"+list());
                    }else{
                        broadcast(messageIn);
                    }
                }
                workers.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
        receiveMessage();
        }
    }

    private void broadcast(String messageIn) {
        PrintWriter out;
        synchronized (workers){
            Iterator<ChatServerWorker> it=workers.iterator();
            while(it.hasNext()){
                try {
                    out=new PrintWriter(it.next().getClientSocket().getOutputStream(), true);
                    out.println(messageIn+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void reply(ChatServerWorker chatServerWorker, String s) {
        try {
            PrintWriter out= new PrintWriter(chatServerWorker.getClientSocket().getOutputStream(), true);
            out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String list() {
      StringBuilder list=new StringBuilder();
      synchronized (workers){
          Iterator<ChatServerWorker> it=workers.iterator();
          while (it.hasNext()){
              list.append("\t");
              list.append(it.next().getName());
              list.append("\n");
          }
      }
      return list.toString();
    }
}
