package SOCKET;

import UIMS.Address;
import UIMS.UIMSApprove;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientTest {

    public static void main(String args[]){
        new DateSynchronizationThread().start();
    }

    static class ClientThread extends Thread {

        Socket socket;
        JSONObject jsonObject;


        public ClientThread(JSONObject jsonObject) {
            //添加
            this.jsonObject = jsonObject;
        }

        @Override
        public void run() {
            System.out.println("学校服务器\t发送数据：线程开始。");
            try{
                //客户端
                //1、创建客户端Socket，指定服务器地址和端口
                Socket socket =new Socket(Address.socketHost,8000);
                //2、获取输出流，向服务器端发送信息
                OutputStream os = socket.getOutputStream();//字节输出流
                PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流
                pw.println(jsonObject.toString());
                pw.flush();
//                socket.shutdownOutput();
//                3、获取输入流，并读取服务器端的响应信息
//                InputStream is = socket.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                String info;
//                while((info=br.readLine()) != null){
//                    System.out.println("学校服务器\t收到消息："+info);
//                    if(info.equals("END")){
//                        break;
//                    }
//                }

//                4、关闭资源
//                br.close();
//                is.close();
//                os.write((jsonObject.toString() + "eof").getBytes());
//                os.flush();
//                socket.shutdownOutput();
//                pw.close();
//                os.close();
//                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    if(socket != null) {
                        socket.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("学校服务器\t发送数据：线程结束。");
            }
        }
    }

    static class DateSynchronizationThread extends Thread {

        Socket socket;
        JSONObject jsonObject;
        JSONObject connectJSON;

        public DateSynchronizationThread() {
            //添加
            this.jsonObject = JSONObject.fromObject(
                    "{\"code\":\"\"," +
                            "\"type\":\"DateSynchronization\"," +
                            "\"message\":\"DateSynchronization\"}"
            );
            connectJSON = new JSONObject();
            connectJSON.put("user", "00000000");
            connectJSON.put("pass", "000000");
            connectJSON.put("type", "connect?");
        }

        @Override
        public void run() {
            System.out.println("学校服务器\t同步数据：线程开始。");
//            new DateSynchronizationCareThread(socket).start();
            try {
//                for(;;) {
                //客户端
                //1、创建客户端Socket，指定服务器地址和端口
                Socket socket = new Socket(Address.socketHost, 8000);
                //2、获取输出流，向服务器端发送信息
                OutputStream os = socket.getOutputStream();//字节输出流
                PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
                //3、获取输入流，并读取服务器端的响应信息
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                try {
                    pw.println(jsonObject.toString());
                    pw.flush();
//                    os.write(jsonObject.toString().getBytes());
//                    os.flush();os.flush();
//                    socket.shutdownOutput();
                    new ServerTest.ServerThread(socket).start();
//                        Thread.sleep(60000);
//                        pw.write("END\n");
//                        pw.flush();
                    sleep(6000);
                    while(true) {

                        pw.println(jsonObject.toString());
                        pw.flush();

                        String str = br.readLine();

                        while (str == null || str.length() == 0) {
                            str = br.readLine();
                        }

                        if (str.contains("connect")) {
                            sleep(60000);
                        }
                        else{
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    break;
                }

                //4、关闭资源
//                    br.close();
//                    is.close();
//                    pw.close();
//                    os.close();
//                    socket.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("学校服务器\t同步数据：线程结束。");
                System.out.println("重启中...");
                new DateSynchronizationThread().start();
            }
        }
    }

    static class DateSynchronizationCareThread extends Thread{

        Socket socket;
        JSONObject jsonObject;

        public DateSynchronizationCareThread(Socket socket){
            this.socket = socket;
            jsonObject = new JSONObject();
            jsonObject.put("user", "00000000");
            jsonObject.put("pass", "000000");
            jsonObject.put("type", "connect?");
        }

        @Override
        public void run() {
            try {
                sleep(6000);
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                int result = 0;
                String str;
                while(true) {

                    outputStream.write(jsonObject.toString().getBytes());
                    outputStream.flush();

                    byte[] bytes = new byte[1024];

                    result = inputStream.read(bytes);

                    while (result != -1) {
                        stringBuilder.append(new String(bytes, 0, result));
                        result = inputStream.read(bytes);
                    }

                    str = stringBuilder.toString();

                    if (str.contains("connect")) {
                        sleep(60000);
                    }
                    else{
                        break;
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            } finally {
                System.out.println("SchoolServer:\t重启中...");
                new DateSynchronizationThread().start();
            }
        }
    }

}
