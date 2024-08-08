import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.Jedis;

public class xxx {

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

            if (parts == null || parts.length == 0) {
                sendError(clientChannel, "Invalid command");
                return;
            }

            String cmd = parts[0].toUpperCase();
            String response;

            switch (cmd) {
                case "GET":
                    if (parts.length != 2) {
                        sendError(clientChannel, "Invalid number of arguments for GET");
                        return;
                    }
                    String value = jedis.get(parts[1]);
                    if (value == null) {
                        response = "$-1\r\n";
                    } else {
                        response = "$" + value.length() + "\r\n" + value + "\r\n";
                    }
                    break;
                case "SET":
                    if (parts.length != 3) {
                        sendError(clientChannel, "Invalid number of arguments for SET");
                        return;
                    }
                    jedis.set(parts[1], parts[2]);
                    response = "+OK\r\n";
                    break;
                default:
                    response = forwardToRedis(jedis, parts);
            }

            sendResponse(clientChannel, response);
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

    private static String forwardToRedis(Jedis jedis, String[] parts) {
        String response;
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "DEL":
                response = ":" + jedis.del(parts[1]) + "\r\n";
                break;
            case "EXISTS":
                response = ":" + jedis.exists(parts[1]) + "\r\n";
                break;
            case "LPUSH":
                response = ":" + jedis.lpush(parts[1], parts[2]) + "\r\n";
                break;
            case "RPUSH":
                response = ":" + jedis.rpush(parts[1], parts[2]) + "\r\n";
                break;
            case "LPOP":
                response = "$" + jedis.lpop(parts[1]) + "\r\n";
                break;
            case "RPOP":
                response = "$" + jedis.rpop(parts[1]) + "\r\n";
                break;
            case "LLEN":
                response = ":" + jedis.llen(parts[1]) + "\r\n";
                break;
            case "HSET":
                response = ":" + jedis.hset(parts[1], parts[2], parts[3]) + "\r\n";
                break;
            case "HGET":
                response = "$" + jedis.hget(parts[1], parts[2]) + "\r\n";
                break;
            default:
                response = "-ERROR: Unsupported command\r\n";
        }
        return response;
    }

    private static void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        clientChannel.write(responseBuffer);
    }

    private static void sendError(SocketChannel clientChannel, String errorMessage) {
        String response = "-ERR " + errorMessage + "\r\n";
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        try {
            clientChannel.write(responseBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
