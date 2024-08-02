import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class client implements Runnable{
private BufferedReader reader;
private ShopCar shopCar;
    public client(BufferedReader reader) {this.reader = reader;}
    @Override
    public void run() {
        String message=null;
        try {
            while ((message=reader.readLine())!=null) {
                System.out.println(message);
                System.out.println("输入指令：");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("localhost", 8888);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader read=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Client connected to server");

        ExecutorService poor= Executors.newFixedThreadPool(1);
        Thread thread=new Thread(new client(read));
        poor.submit(thread);
        thread.start();
        System.out.println("输入指令：1：购买苹果 2：购买香蕉 3：购买橘子 4：退出 5：查看购物车 6：查看库存\n输入指令：");
        Scanner scanner=new Scanner(System.in);
        int n=scanner.nextInt();
        int num;
        while (true) {
            switch (n) {
                case 1:

                    System.out.print("购买苹果,输入数量：");
                    num = scanner.nextInt();
                    writer.write("苹果," + num+"\n");
                    writer.flush();
                    break;
                case 2:
                    System.out.print("购买香蕉,输入数量：");
                    num = scanner.nextInt();
                    writer.write("香蕉," + num+"\n");
                    writer.flush();
                    break;
                case 3:
                    System.out.println("购买橘子,输入数量：");
                    num = scanner.nextInt();
                    writer.write("橘子," + num+"\n");
                    writer.flush();
                    break;
                case 5:
                    writer.write("car,car\n");
                    writer.flush();
                    break;
                case 4:;
                    System.out.println("退出");
                    poor.shutdown();
                break;
                case 6:
                    writer.write("num,num\n");
                    writer.flush();
                    break;

            }
            if(n==4)break;
            n = scanner.nextInt();
        }
    }
}
