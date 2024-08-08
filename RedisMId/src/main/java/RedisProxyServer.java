import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.Jedis;

public class RedisProxyServer {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(6378));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Proxy server started on port " + 6378);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(selector, key);
                    } else if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void read(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead;

        try {
            bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                clientChannel.close();
                key.cancel();
                return;
            }

            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);

            String command = new String(data).trim();
            System.out.println("Received command: " + command);

            threadPool.submit(() -> handleCommand(clientChannel, command));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            key.cancel();
        }
    }

    private static void handleCommand(SocketChannel clientChannel, String command) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String[] parts = parseCommand(command);


            String cmd = parts[0].toUpperCase();
            String response;

            switch (cmd) {
                case "GET":

                    String value = jedis.get(parts[1]);
                    if (value == null) {
                        response = "$-1\r\n";
                    } else {
                        response = "$" + value.length() + "\r\n" + value + "\r\n";
                    }
                    break;
                case "SET":

                    jedis.set(parts[1], parts[2]);
                    response = "+OK\r\n";
                    break;
                default:
                    response = "-ERROR\r\n";
            }

            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(responseBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] parseCommand(String command) {
        String[] lines = command.split("\r\n");
        if (lines.length < 2 || !lines[0].startsWith("*")) {
            return null;
        }

        int numArgs;
        try {
            numArgs = Integer.parseInt(lines[0].substring(1));
        } catch (NumberFormatException e) {
            return null;
        }

        String[] parts = new String[numArgs];
        int index = 0;
        for (int i = 1; i < lines.length; i += 2) {
            if (lines[i].startsWith("$")) {
                parts[index++] = lines[i + 1];
            } else {
                return null;
            }
        }

        if (index != numArgs) {
            return null;
        }

        return parts;
    }


}
