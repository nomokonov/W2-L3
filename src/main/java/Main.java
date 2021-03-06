import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class Main {
//    static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static Selector sel = null;
    public static ServerSocketChannel server = null;
    public static SocketChannel socket = null;
    public static int port = 5050;
    public static String result = null;
    public static int count_mess = 0;

    public static void main(String args[]) throws IOException {


        sel = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        InetSocketAddress isa = new InetSocketAddress(port);
        server.socket().bind(isa);
//        logger.info("Server started");

        SelectionKey acceptKey = server.register(sel, SelectionKey.OP_ACCEPT);
        System.out.println("Server started");
        while (acceptKey.selector().select() > 0) {

            Iterator it = sel.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();

                if (key.isValid() && key.isAcceptable()) {
//                    System.out.println("Key is Acceptable");ass
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    socket = (SocketChannel) ssc.accept();
                    socket.configureBlocking(false);
                    SelectionKey another = socket.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
                if (key.isValid() && key.isReadable()) {
//					System.out.println("Key is readable");
                    String ret = readMessage(key);
                    if (ret.toLowerCase().equals("bye.")) {
                        socket.close();
                    } else if (ret.length() > 0) {
                        writeMessage(socket, ret);
                    }

                }
                if (key.isValid() && key.isWritable()) {
                    String ret = readMessage(key);
                    socket = (SocketChannel) key.channel();
                    if (ret.toLowerCase().equals("bye.")) {
                        socket.close();
                    } else if (result.length() > 0) {
                        writeMessage(socket, ret);
                    }
                }
            }
        }
    }

    private static void writeMessage(SocketChannel socket, String ret) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(24);
            buffer = ByteBuffer.wrap(ret.getBytes());
            socket.write(buffer);
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String readMessage(SelectionKey key) {
        int nBytes = 0;
        socket = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(24);
        try {
            nBytes = socket.read(buf);
//            logger.info("READ socket ");
            buf.flip();
            Charset charset = Charset.forName("utf-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buf);
            result = charBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}



