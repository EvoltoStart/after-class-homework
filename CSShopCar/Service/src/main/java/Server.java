import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.Map;
class server implements Runnable {

    private Socket socket;
    private ShopCar shopCar;

    public server(Socket socket, ShopCar shopCar) {

        this.socket = socket;
        this.shopCar = shopCar;
    }

    @Override
    public void run() {

        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String sss;
            while ((sss=read.readLine())!=null) {
                System.out.println(sss);
                String[] s = sss.split(",");
                System.out.println(s[0]);
                if (s[0].equals("num")) {
                    Map<String, Integer> map = shopCar.getMap();
                    StringBuilder ss = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + ":" + entry.getValue());

                        ss.append(entry.getKey() + "剩余数量: " + entry.getValue()).append(",");
                    }
                    System.out.println(ss);
                    writer.println(ss);
                    writer.flush();
                } else if (s[0].equals("car")) {
                    Map<String, Integer> map = shopCar.getCarNum();
                    StringBuilder ss = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        if(entry.getKey()!=null) {
                            System.out.println(entry.getKey() + ":" + entry.getValue());

                            ss.append(entry.getKey() + "购买数量: " + entry.getValue()).append(",");
                        }else {
                            System.out.println();
                        }
                    }
                    System.out.println(ss);
                    writer.println(ss);
                    writer.flush();

                } else {
                    if (shopCar.add(s[0], Integer.parseInt(s[1]))) {
                        Integer n = shopCar.getNumber(s[0]);
                        writer.println("添加成功,"+s[0]+"剩余数量：" + n.toString());
                        writer.flush();
                    } else {
                        writer.write("添加失败\n");
                        writer.flush();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
public class Server {
    public static void main(String[] args) throws IOException {
        ShopCar shopCar=new ShopCar();
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("服务器已启动");
        while (true) {
            new Thread(new server(serverSocket.accept(),shopCar)).start();


            }
        }
    }


