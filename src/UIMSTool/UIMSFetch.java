package UIMSTool;

import HTTP.SSLClient;
import UIMS.Address;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.HashMap;

public class UIMSFetch extends Thread {

    static int MAXNumber = 1;
    static HashMap<Integer, CloseableHttpClient> httpClients = new HashMap<>();
    static HashMap<Integer, String> clientNumber_JSSIONID = new HashMap<>();

    static HttpPost httpPost = new HttpPost(Address.hostAddress + "/ntms/");

    public static CloseableHttpClient get_httpClient;
    public static String get_jssionID;
    public static boolean has_ready = false;

    public static void getAHttpClient(){

        get_httpClient = getAHttpClient(0);
        get_jssionID = getJssionID(0);

    }

    public static CloseableHttpClient getAHttpClient(int num){

        if(httpClients.containsKey(num)){
            CloseableHttpClient httpClient = httpClients.get(num);
            httpClients.remove(num);
            return httpClient;
        }
        return null;

    }

    public static String getJssionID(int num){

        if(clientNumber_JSSIONID.containsKey(num)){
            return clientNumber_JSSIONID.get(num);
        }
        return null;

    }

    public static SSLClient createAHttpClient(){
        try {
            return new SSLClient();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run(){
        String jssionID;
        while (true) {
            for (int i = 0; i < MAXNumber; i++) {
                if(!httpClients.containsKey(i)){
                    CloseableHttpClient httpClient = createAHttpClient();
                    CloseableHttpResponse response = null;
                    try {
                        response = httpClient.execute(httpPost);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Header headers1[] =  response.getHeaders("Set-Cookie");
                    if(headers1.length == 1){
                        try {
                            String str = headers1[0].getValue();
                            str = str.split(";")[0];
                            jssionID = str.split("=")[1];
                            httpClients.put(i, httpClient);
                            clientNumber_JSSIONID.put(i, jssionID);
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                            throw new RuntimeException("Get JSSIONID ERROR！");
                        }
                        finally {
                            try {
                                response.close();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    else{
                        throw new RuntimeException ("ERROR: More then one! length: " + headers1.length);
                    }
                }
            }
            has_ready = true;
            System.out.println("Prepare finished!");
//            try {
//                wait();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            break;
        }
    }

}
