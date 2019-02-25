import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public class Client
{
    public SocketChannel client = null;
    public InetSocketAddress isa = null;
    public RecvThread rt = null;
    public int count =0;
    public int recv_count =0;
    public static  final  String HELLO =  "Hello!";

    public Client()
    {
    }

    public void makeConnection()  {
        int result = 0;
        try
        {

            client = SocketChannel.open();
            isa = new InetSocketAddress("localhost",5050);
            client.connect(isa);
            client.configureBlocking(false);
            receiveMessage();
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        while ((result = sendMessage(HELLO)) != -1)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        try
        {
            client.close();
            System.out.println("Reciv " + recv_count);
            System.out.println("Send " + count);
            System.exit(0);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public int sendMessage(String mess)
    {
//        System.out.println("Inside SendMessage");
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        String msg = "Hello!";
        ByteBuffer bytebuf = ByteBuffer.allocate(24);
        int nBytes = 0;
        try
        {
//            msg = in.readLine();

//            System.out.println("msg is "+mess);
            bytebuf = ByteBuffer.wrap(mess.getBytes());
            nBytes = client.write(bytebuf);
//            System.out.println("nBytes is "+nBytes);
              count++;
            if (count == 5000 ){
//                System.out.println("msg is "+"Bye.");
                bytebuf = ByteBuffer.wrap("Bye.".getBytes());
                nBytes = client.write(bytebuf);
//                System.out.println("nBytes is "+nBytes);
                interruptThread();
                try
                {
                    Thread.sleep(5000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                client.close();
                return -1;
            }

            if (mess.equals("quit") || mess.equals("shutdown")) {
                System.out.println("time to stop the client");
                interruptThread();
                try
                {
                    Thread.sleep(5000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                client.close();
                return -1;
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
//        System.out.println("Wrote "+nBytes +" bytes to the server");
        return nBytes;
    }

    public void receiveMessage()
    {
        rt = new RecvThread("Receive THread",client);
        rt.start();

    }

    public void interruptThread()
    {
        rt.val = false;
    }

    public static void main(String args[])
    {
        Client cl = new Client();
        cl.makeConnection();
    }

    public class RecvThread extends Thread
    {
        public SocketChannel sc = null;
        public boolean val = true;

        public RecvThread(String str,SocketChannel client)
        {
            super(str);
            sc = client;
        }

        public void run() {

//           System.out.println("Inside receivemsg");
            int nBytes = 0;
            ByteBuffer buf = ByteBuffer.allocate(24);
            try
            {
                while (val)
                {
                    while ( (nBytes = nBytes = client.read(buf)) > 0){
                        buf.flip();
                        Charset charset = Charset.forName("utf-8");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buf);
                        String result = charBuffer.toString();
                        System.out.println(result);
                        recv_count++;
                        buf.flip();

                    }
                }

            }
            catch(IOException e)
            {
                e.printStackTrace();

            }


        }
    }
}