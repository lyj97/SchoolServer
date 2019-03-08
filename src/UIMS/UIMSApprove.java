package UIMS;

import HTTP.SSLClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIMSApprove {

    JSONObject jsonObject;
    String cookie1;//有pwdStrength
    String cookie2;
    String cookie3;//j_spring_security_check返回
    String cookie4;
    String student_id;
    String user;
    String pass;
    String jssionID;
    String jssionID2;
    String term_id;
    String adcId;
    CloseableHttpClient httpclient;
    CookieStore cookieStore;
    List<Cookie> cookies;

    public UIMSApprove(JSONObject jsonObject){
        this.jsonObject = jsonObject;
        try{
            httpclient = new SSLClient();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void newHttpClient(){
        try{
            httpclient.close();
            httpclient = new SSLClient();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject approve(){

        if(jsonObject == null){

            throw new RuntimeException("ERROR:\t未指定json！");

        }

        user = "";
        pass = "";

        try{

            user = (String) ((JSONObject)jsonObject.get("message")).get("studentNumber");
            pass = (String) ((JSONObject)jsonObject.get("message")).get("studentPassword");

        }
        catch (Exception e){

            e.printStackTrace();
            throw new RuntimeException("ERROR！");

        }

//        System.out.println("user:\t" + user);
//        System.out.println("oass:\t" + pass);



        jssionID = "";
        System.out.println("获取会话ID...");

//        System.out.println("url:\t" + url);
//        System.out.println("key:\t" + key);

        HttpPost httppost1 = new HttpPost(Address.hostAddress + "/ntms/");
//        httppost1.setHeader("Cookie", "UM_distinctid=16758a03df86d6-05464bd3e66df4-6313363-144000-16758a03df937b");
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httppost1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Header headers1[] =  response1.getHeaders("Set-Cookie");
        if(headers1.length == 1){
            try {
                String str = headers1[0].getValue();
                str = str.split(";")[0];
                jssionID = str.split("=")[1];
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                throw new RuntimeException("Get JSSIONID ERROR！");
            }
            finally {
                try {
                    response1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        else{
            throw new RuntimeException ("ERROR: More then one! length: " + headers1.length);
        }

//        System.out.println(jssionID);

        String uid = "";



        System.out.println("登录中...");

//        System.out.println("user:" + user + "\tpass: " + pass);

        Map<String, String> map1 = new HashMap();
        map1.put("j_username",user);
        map1.put("j_password",pass);
        map1.put("mousePath","SGwABSAgCeSBgCwSCQDASCgDRSDQDhSDwDzSEQEDSFgEUSGgEkSHgE1SIgFGSJwFWSLwFnSMwF4SOAGJSPAGZSQQGqSRQG6SSQHMSTgHcSUgHtSVgH9SWgIOSYQIeSZgIvSbAJAScQJRSdwJiSewJySgAKDShAKUShgKkSigK0SjQLFSkALWSlgLmSmgL4SnwMISowMZSpgMpSqQM6SrQNLSsQNbStANsStwN9SvAOOSvgOfSxAOvSxwPASywPRSzwPhS1APyS2AQCS3QQTS4QQkS5QQ1S6QRFARwXr");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httppost2 = new HttpPost(Address.hostAddress + "/ntms/j_spring_security_check");
//        cookie1 =  "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID + " UM_distinctid=16758a03df86d6-05464bd3e66df4-6313363-144000-16758a03df937b";
        cookie1 =  "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID;
        httppost2.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost2.setHeader("Cookie",cookie1);
        httppost2.setEntity(entity);
        CloseableHttpResponse response2 = null;
        try {
            response2 = httpclient.execute(httppost2);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Get UID response ERROR！");
        }

        HttpEntity entity1 = response2.getEntity();
        Header headers2[] =  response2.getHeaders("Set-Cookie");
        if(headers2.length == 1){
            try {
                String str = headers2[0].getValue();
                str = str.split(";")[0];
                jssionID2 = str.split("=")[1];
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                throw new RuntimeException("Get JSSIONID2 ERROR！");
            }
            finally {
                try {
                    response1.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        else{
            throw new RuntimeException ("ERROR: More then one! length: " + headers1.length);
        }

        String result = null;
        try {
            result = EntityUtils.toString(entity1);
            response2.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("Get UID ERROR！");
        }

        uid = result;
//        System.out.println("uid:\t" + uid);

        System.out.println("正在加载用户信息...");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);

//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        cookie3 =  "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID2;
        cookie4 =  "loginPage=userLogin.jsp; alu=" + user + "; JSESSIONID=" + jssionID2;

        HttpPost httppost3 = new HttpPost(Address.hostAddress + "/ntms/action/getCurrentUserInfo.do");
        httppost3.setHeader("Referer", Address.hostAddress + "/ntms/index.do");
        httppost3.setHeader("Connection", "keep-alive");
        httppost3.setHeader("User-Agent","Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost3.setHeader("Cookie", cookie3);
        CloseableHttpResponse response3 = null;
        JSONObject object = null;
        try {
            response3 = httpclient.execute(httppost3);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity2 = null;
        Header headers3[] = null;

        if (response3.getStatusLine().getStatusCode() == 200 || response3.getStatusLine().getStatusCode() == 302) {

            entity2 = response3.getEntity();
            headers3 = response3.getAllHeaders();

            // 得到httpResponse的实体数据
            if (entity2 != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity2.getContent(), "UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
//                    System.out.println("entity:\t" + entityStringBuilder.toString());
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    response3.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {

            System.out.println("WRONG HTTP STATE:\t" + response3.getStatusLine().getStatusCode());

            throw new RuntimeException("WRONG HTTP STATE: \t" + response3.getStatusLine().getStatusCode() + " ！");

        }

        student_id = "";
        term_id = "";
        adcId = "";

        try {

//            for (int i = 0; i < headers3.length; i++) {
//                System.out.print(headers3[i].getName() + "\t");
//                System.out.println(headers3[i].getValue());
//            }

            if (object != null) {
//                System.out.println(object.toString());
            } else {
                System.out.println("object IS NULL!");
            }

            JSONObject defRes = (JSONObject) object.get("defRes");
            student_id = defRes.getString("personId");
            term_id = defRes.getString("term_l");
            adcId = defRes.getString("adcId");

//            System.out.println("student_id:\t" + student_id);
//            System.out.println("term_id:\t" + term_id);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        JSONObject request_json1 = new JSONObject();
        request_json1.put("studId",student_id);

        JSONObject request_json = new JSONObject();
        request_json.put("tag","student@stu_infor_table");
        request_json.put("params",request_json1);

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }


        HttpPost httppost4 = new HttpPost(Address.hostAddress + "/ntms/service/res.do");
//        cookie2 =  "loginPage=userLogin.jsp;alu=" + user + ";JSESSIONID=" + jssionID;
        httppost4.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost4.setHeader("Host","uims.jlu.edu.cn");
        httppost4.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost4.setHeader("Content-Type","application/json");
        httppost4.setHeader("Cookie",cookie4);
        httppost4.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response4 = null;
        JSONObject object1 = null;
        try {
            response4 = httpclient.execute(httppost4);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity3 = null;
        Header headers4[] = null;

        if (response4.getStatusLine().getStatusCode() == 200) {

            entity3 = response4.getEntity();
            headers4 = response4.getAllHeaders();

            // 得到httpResponse的实体数据
            if (entity3 != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity3.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object1 = JSONObject.fromObject(entityStringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                } finally {
                    try {
                        response4.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        String schoolName = "";//学院
        String deptName = "";//专业
        String admissionYear = "";//入学时间
        String name = "";

        try {

//            for (int i = 0; i < headers4.length; i++) {
//                System.out.print(headers4[i].getName() + "\t");
//                System.out.println(headers4[i].getValue());
//            }

            if (object1 != null) {
//                System.out.println(object1.toString());
            } else {
                System.out.println("object IS NULL!");
            }

            JSONObject value = (JSONObject)object1.get("value");
            JSONObject person = null;
            JSONObject department = null;
            JSONObject school = null;
            try{
                person = value.getJSONObject("person");
                department = value.getJSONObject("department");
                school = value.getJSONObject("school");

                schoolName = school.getString("schoolName");//学院
                deptName = department.getString("deptName");//专业
                admissionYear = value.getString("admissionYear");//入学时间
                name = person.getString("name");

            }
            catch (Exception e1){
//                e1.printStackTrace();
                throw new RuntimeException("ERROR！");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        JSONObject jsonObject1 = JSONObject.fromObject(
                "{\"college\":\"" + schoolName + "\"," +
                "\"major\":\"" + deptName + "\"," +
                "\"grade\":\"" + admissionYear + "\"," +
                "\"name\":\"" + name + "\"}");

        JSONObject return_json = JSONObject.fromObject(
                "{\"type\":\"approve\"," +
                "\"code\":\"\"," +
                "\"state\":0," +
                "\"userID\":\"" + user + "\"," +
                "\"password\":\"" + pass + "\"," +
                "\"studentID\":\"" + student_id + "\"," +
                "\"message\":" + jsonObject1 + "}");

        request_json.put("user", user);
        request_json.put("pass", pass);

        System.out.println("欢迎您，来自 " + deptName + " 的 " + name + " .");

        return return_json;

    }


    /**
     * 参数
     * branch	latest
     * params	{}
     * rowLimit	15
     * tag	archiveScore@queryCourseScore
     * @return
     */

    public JSONObject getScore(){

        JSONObject request_json = new JSONObject();
        request_json.put("branch","latest");
        request_json.put("params","{}");
        request_json.put("rowLimit","15");
        request_json.put("tag","archiveScore@queryCourseScore");

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpPost httppost = new HttpPost(Address.hostAddress + "/ntms/service/res.do");
        httppost.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost.setHeader("Host","uims.jlu.edu.cn");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost.setHeader("Content-Type","application/json");
        httppost.setHeader("Cookie",cookie2);
        httppost.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response = null;
        JSONObject object = null;
        try {
            response = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity = null;
        Header headers[] = null;

        if (response.getStatusLine().getStatusCode() == 200) {

            entity = response.getEntity();

            // 得到httpResponse的实体数据
            if (entity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    JSONObject object1 = new JSONObject();
                    object1.put("code","");
                    object1.put("type","score");
                    object1.put("user",user);
                    object1.put("pass", pass);
                    object1.put("userID",user);
                    object1.put("password", pass);
                    object1.put("studentID",student_id);
                    object1.put("student", object.getJSONArray("value").getJSONObject(0).getJSONObject("student"));
                    object1.put("scoreStatistics", getScoreStatistics());
                    object1.put("message",addScorePercent(object));
//                    System.out.println(object1.getString("userID" + "\t" + object1.getString("password")));
                    return object1;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {
            System.out.println("state:\t" + response.getStatusLine().getStatusCode());
            throw new RuntimeException("ERROR！");
        }

        try{
            response.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 参数
     * params	{…}
     * studId	266662
     * res	stat-avg-gpoint
     * type	query
     * @return
     */
    public JSONObject getScoreStatistics(){

//        System.out.println("getScoreStatistics");

        JSONObject request_json1 = new JSONObject();
        request_json1.put("studId",student_id);

        JSONObject request_json = new JSONObject();
        request_json.put("res","stat-avg-gpoint");
        request_json.put("params",request_json1);
        request_json.put("type","query");

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpPost httppost = new HttpPost(Address.hostAddress + "/ntms/service/res.do");
        httppost.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost.setHeader("Host","uims.jlu.edu.cn");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost.setHeader("Content-Type","application/json");
        httppost.setHeader("Cookie",cookie2);
        httppost.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response = null;
        JSONObject object = null;
        try {
            response = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity = null;
        Header headers[] = null;

        if (response.getStatusLine().getStatusCode() == 200) {

            entity = response.getEntity();

            // 得到httpResponse的实体数据
            if (entity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    return object;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {
            System.out.println("state:\t" + response.getStatusLine().getStatusCode());
            throw new RuntimeException("ERROR！");
        }
        return null;
    }

    public JSONObject addScorePercent(JSONObject score){
        JSONArray scores = score.getJSONArray("value");
        int i = 0;
        while (true) {
            try {
                JSONObject temp = scores.getJSONObject(i);
                JSONObject temp1 = temp.getJSONObject("teachingTerm");
                JSONObject temp2 = temp.getJSONObject("course");

                String asId = temp.getString("asId");

                temp.put("percent", getScorePercent(asId));

                temp.remove("student");
                temp.remove("xkkh");
                temp.remove("notes");
                temp.remove("planDetail");
                temp1.remove("activeStage");
                temp1.remove("weeks");
                temp1.remove("startDate");
                temp1.remove("vacationDate");
                temp2.remove("type5");
                temp2.remove("englishName");
                temp2.remove("courType3");
                temp2.remove("activeStatus");

                i++;
            } catch (Exception e) {
                break;
            }
        }
        return score;
    }

    /**
     * 参数
     * asId	8792747
     * @return
     */
    public JSONObject getScorePercent(String asId){

        JSONObject request_json = new JSONObject();

        request_json.put("asId",asId);

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpPost httppost = new HttpPost(Address.hostAddress + "/ntms/score/course-score-stat.do");
        httppost.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost.setHeader("Host","uims.jlu.edu.cn");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost.setHeader("Content-Type","application/json");
        httppost.setHeader("Cookie",cookie2);
        httppost.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response = null;
        JSONObject object = null;
        try {
            response = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity = null;
        Header headers[] = null;

        if (response.getStatusLine().getStatusCode() == 200) {

            entity = response.getEntity();

            // 得到httpResponse的实体数据
            if (entity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    return object;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {
            System.out.println("state:\t" + response.getStatusLine().getStatusCode());
            throw new RuntimeException("ERROR！");
        }
        return null;
    }

    /**
     *
     * searchterm_header:
     * Cookie:loginPage=userLogin.jsp; alu=教学号; JSESSIONID=？？？
     * Referer: http://uims.jlu.edu.cn/ntms/index.do
     * @return termid
     *
     * 参数
     * branch	default
     * params	{…}
     *     studId	???
     *     termId	???
     * tag	teachClassStud@schedule
     *
     * {"tag":"teachClassStud@schedule","branch":"default","params":{"termId":135,"studId":266662}}
     */
    public JSONObject getSelectedCourse() {

        System.out.println("获取选课信息...");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);
//        System.out.println("student_id:\t" + student_id);
//        System.out.println("term_id:\t" + term_id);

        HashMap<String, String> map = new HashMap();


//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        JSONObject request_json1 = new JSONObject();
        request_json1.put("termId",term_id);
        request_json1.put("studId",student_id);

        JSONObject request_json = new JSONObject();
        request_json.put("tag","teachClassStud@schedule");
        request_json.put("branch","default");
        request_json.put("params",request_json1);

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }


        HttpPost httppost = new HttpPost(Address.hostAddress + "/ntms/service/res.do");
        String cookie =  "loginPage=userLogin.jsp;alu=" + user + ";JSESSIONID=" + jssionID;
        httppost.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost.setHeader("Host","uims.jlu.edu.cn");
        httppost.setHeader("Origin","http://uims.jlu.edu.cn");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost.setHeader("Content-Type","application/json");
        httppost.setHeader("Cookie",cookie);
        httppost.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response = null;
        JSONObject object = null;
        try {
            response = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity1 = null;
        Header headers[] = null;

        if (response.getStatusLine().getStatusCode() == 200) {

            entity1 = response.getEntity();
            headers = response.getAllHeaders();

            // 得到httpResponse的实体数据
            if (entity1 != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity1.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    JSONObject object1 = new JSONObject();
                    object1.put("code","");
                    object1.put("type","courses");
                    object1.put("userID",user);
                    object1.put("studentID",student_id);
                    object1.put("message",object);
                    return object1;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {
            System.out.println("state:\t" + response.getStatusLine().getStatusCode());
            throw new RuntimeException("ERROR！");
        }
        return null;
    }

    /**
     * 参数
     * branch	byAdc
     * params	{…}
     *      adcId	6049
     *      termId	134
     * tag	tcmAdcAdvice@dep_recommandT
     */
    public JSONObject getRecommendCourse() {

        System.out.println("获取推荐课表...");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);
//        System.out.println("student_id:\t" + student_id);
//        System.out.println("term_id:\t" + term_id);

        HashMap<String, String> map = new HashMap();


//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

        JSONObject request_json1 = new JSONObject();
        request_json1.put("termId",term_id);
        request_json1.put("adcId",adcId);

        JSONObject request_json = new JSONObject();
        request_json.put("tag","tcmAdcAdvice@dep_recommandT");
        request_json.put("branch","byAdc");
        request_json.put("params",request_json1);

        StringEntity json_entity = null;

        try {
            json_entity = new StringEntity(request_json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }


        HttpPost httppost = new HttpPost(Address.hostAddress + "/ntms/service/res.do");
        String cookie =  "loginPage=userLogin.jsp;alu=" + user + ";JSESSIONID=" + jssionID;
        httppost.setHeader("Referer",Address.hostAddress + "/ntms/index.do");
        httppost.setHeader("Host","uims.jlu.edu.cn");
        httppost.setHeader("Origin","http://uims.jlu.edu.cn");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httppost.setHeader("Content-Type","application/json");
        httppost.setHeader("Cookie",cookie);
        httppost.setEntity(json_entity);

//        Header[] httpHeaders = httppost.getAllHeaders();
//        System.out.println("HEADERS");
//        for(int i=0; i<httpHeaders.length; i++){
//            System.out.println(httpHeaders[i].getName() + "\t" + httpHeaders[i].getValue());
//        }
//        System.out.println("ENTITY");
//        try {
//            System.out.println(EntityUtils.toString(json_entity));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CloseableHttpResponse response = null;
        JSONObject object = null;
        try {
            response = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR！");
        }

        HttpEntity entity1 = null;
        Header headers[] = null;

        if (response.getStatusLine().getStatusCode() == 200) {

            entity1 = response.getEntity();
            headers = response.getAllHeaders();

            // 得到httpResponse的实体数据
            if (entity1 != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity1.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "/n");
                    }
                    // 利用从HttpEntity中得到的String生成JsonObject
                    object = JSONObject.fromObject(entityStringBuilder.toString());
                    JSONObject object1 = new JSONObject();
                    object1.put("code","");
                    object1.put("type","courses");
                    object1.put("userID",user);
                    object1.put("studentID",student_id);
                    object1.put("message",object);
                    return object1;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }
            }
        }
        else {
            System.out.println("state:\t" + response.getStatusLine().getStatusCode());
            throw new RuntimeException("ERROR！");
        }
        return null;
    }

    public static void main(String args[]){

        String user = "54160907";
        String pass = "225577";

        String md5 = GetMD5.getMD5Str("UIMS" + user + pass);

        JSONObject jsonObject1 = JSONObject.fromObject("{\"studentNumber\":\"" + user + "\"," +
                "\"studentPassword\":\"" + md5 + "\"}");
//        System.out.println(jsonObject1);

        JSONObject jsonObject = JSONObject.fromObject("{\"message\":" + jsonObject1 + "}");
//        System.out.println(jsonObject);

        UIMSApprove uimsApprove = new UIMSApprove(jsonObject);

        System.out.println(uimsApprove.approve());

//        System.out.println(uimsApprove.getScore());

//        System.out.println(uimsApprove.getSelectedCourse());
//        System.out.println(uimsApprove.getRecommendCourse());

    }

}
