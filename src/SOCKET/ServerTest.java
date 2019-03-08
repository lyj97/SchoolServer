package SOCKET;

import UIMS.UIMSApprove;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {

    public static int PORT_NUM = 8080;

    static class ServerThread extends Thread {

        public ServerThread(Socket socket) {
            //添加
            this.socket = socket;
        }

        Socket socket;
        ServerSocket server;
        BufferedReader reader;

        @Override
        public void run() {
            System.out.println("学校服务器接收数据：线程开始。");
            try {
                //服务器响应代码在这里添加
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                pwriter.println();
//                pwriter.flush();
                String str;
                while (true) {

                    str = reader.readLine();
                    while(str == null || str.length() == 0){
                        str = reader.readLine();
                    }

                    System.out.println("client:\t" + str);

                    if(str.equals("END")){
                        break;
                    }

                    try {
                        JSONObject jsonObject = JSONObject.fromObject(str);
                        String code = jsonObject.getString("code");
                        String type = jsonObject.getString("type");

                        if(type.equals("approve")){
                            System.out.println("approving...");
                            UIMSApprove uimsApprove = new UIMSApprove(jsonObject);
                            JSONObject jsonObject1 = uimsApprove.approve();
                            new ClientTest.ClientThread(jsonObject1).start();
//                            new ClientTest.ClientThread(uimsApprove.getSelectedCourse()).start();
                            new ClientTest.ClientThread(uimsApprove.getScore()).start();
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        break;
                    }
                }

            }
            catch (Exception e) {
                //e.printStackTrace();
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
                System.out.println("学校服务器接收数据：线程结束。");
            }
        }
    }

}
