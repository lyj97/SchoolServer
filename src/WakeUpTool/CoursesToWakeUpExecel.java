package WakeUpTool;

import UIMS.GetMD5;
import UIMS.UIMSApprove;
import UIMSTool.ClassSetConvert;
import UIMSTool.UIMSFetch;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.Scanner;

public class CoursesToWakeUpExecel {

    public static void main(String[] args) {

        new UIMSFetch().start();
        System.out.println("准备中，请稍等...");

        while(true){
            if(UIMSFetch.has_ready) break;
            try {
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("程序帮助您生成xlsx格式的课程表，方便您将已选课程导入WakeUp课程表.");
        System.out.println("WakeUp课程表下载地址：https://www.coolapk.com/apk/com.suda.yzune.wakeupschedule");
        System.out.println("请在吉林大学校园网下使用.");
        System.out.println("如遇问题，请联系开发者（QQ：1159386449 微信：luyajun97）.");

        Scanner scanner = new Scanner(System.in);

        String user = "";
        String pass = "";

        if(args.length == 2 && args[0].length() == 8){
            user = args[0];
            pass = args[1];
        }
        else{
//            user = "54160728";
//            pass = "040310";

            System.out.print("请输入您的教学号：");
            user = scanner.nextLine();
            while(user.length() != 8){
                System.out.print("请输入您的8位【教学号】：");
                user = scanner.nextLine();
            }
            System.out.print("请输入您的教务密码：");
            pass = scanner.nextLine();

//            System.out.println("login as : 54160907");

            System.out.println("login as : \t" + user);

        }

        String md5 = GetMD5.getMD5Str("UIMS" + user + pass);

        JSONObject jsonObject1 = JSONObject.fromObject("{\"studentNumber\":\"" + user + "\"," +
                "\"studentPassword\":\"" + md5 + "\"}");
//        System.out.println(jsonObject1);

        JSONObject jsonObject = JSONObject.fromObject("{\"message\":" + jsonObject1 + "}");
//        System.out.println(jsonObject);

        UIMSApprove uimsApprove = new UIMSApprove(jsonObject);
        uimsApprove.approve();

//        System.out.println(uimsApprove.approve());

//        System.out.println(uimsApprove.getScore());

//        System.out.println(uimsApprove.getSelectedCourse());

        JSONArray courses = uimsApprove.getSelectedCourse().getJSONObject("message").getJSONArray("value");

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("Sheet1");
        String[] strings = new String[]{"课程名称","星期","开始节数","结束节数","老师","地点","周数"};
        createXSSFCell(xssfSheet, 0, strings);

        System.out.println("您的选课信息如下（请先清空模板所有数据【首行除外】，然后粘贴两条虚线间的内容至模板中，保存并导入至WakeUp课程表中）：");

        System.out.println("--------------------------------------------------------------------");

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        int row = 1;

        for (int i = 0; i < courses.size(); i++) {

            teachClassMaster = courses.getJSONObject(i).getJSONObject("teachClassMaster");

            lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
            lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

            courName = lessonSegment.getString("fullName");

            teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

            for (int j = 0; j < lessonSchedules.size(); j++) {

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                classroomName = classroom.getString("fullName");

                classSet = timeBlock.getInt("classSet");
                dayOfWeek = timeBlock.getInt("dayOfWeek");
                beginWeek = timeBlock.getInt("beginWeek");
                endWeek = timeBlock.getInt("endWeek");

                try{
                    weekOddEven = timeBlock.getString("weekOddEven");
                }catch (Exception e){
//                    e.printStackTrace();
                }

                ClassSetConvert classSetConvert = new ClassSetConvert();

                start_end = classSetConvert.mathStartEnd(classSet);

                System.out.println(courName + "\t" + dayOfWeek + "\t" + start_end[0] + "\t" + start_end[1] + "\t" + teacherName + "\t" + classroomName + "\t" + getWeeks(beginWeek, endWeek, weekOddEven));

                strings = new String[]{courName, ""+dayOfWeek, ""+start_end[0], ""+start_end[1], teacherName, classroomName, getWeeks(beginWeek, endWeek, weekOddEven)};
                createXSSFCell(xssfSheet, row++, strings);

                weekOddEven = "";

            }

        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("课程表_" + user + ".xlsx");
            xssfWorkbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.println("请先清空模板所有数据【首行除外】，然后粘贴两条虚线间的内容至模板中，保存并导入至WakeUp课程表中.");
        System.out.println("已生成 \"课程表_" + user + ".xlsx\" ，请在程序目录查看.");

        System.out.print("需要获取推荐课表吗？(y/n)");

        String choose = "";
        choose = scanner.nextLine();
        if(choose.toLowerCase().equals("y")){
            getRecommendCourse(uimsApprove, user);
        }

        System.out.println("感谢使用，再见.");

    }

    private static void getRecommendCourse(UIMSApprove uimsApprove, String user){
        JSONArray courses = uimsApprove.getRecommendCourse().getJSONObject("message").getJSONArray("value");

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("Sheet1");
        String[] strings = new String[]{"课程名称","星期","开始节数","结束节数","老师","地点","周数"};
        createXSSFCell(xssfSheet, 0, strings);

        System.out.println("您的推荐课表如下（请先清空模板所有数据【首行除外】，然后粘贴两条虚线间的内容至模板中，保存并导入至WakeUp课程表中）：");

        System.out.println("--------------------------------------------------------------------");

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        for (int i = 0; i < courses.size(); i++) {

            teachClassMaster = courses.getJSONObject(i).getJSONObject("teachClassMaster");

            lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
            lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

            courName = lessonSegment.getString("fullName");

            teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

            for (int j = 0; j < lessonSchedules.size(); j++) {

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                classroomName = classroom.getString("fullName");

                classSet = timeBlock.getInt("classSet");
                dayOfWeek = timeBlock.getInt("dayOfWeek");
                beginWeek = timeBlock.getInt("beginWeek");
                endWeek = timeBlock.getInt("endWeek");

                try{
                    weekOddEven = timeBlock.getString("weekOddEven");
                }catch (Exception e){
//                    e.printStackTrace();
                }

                ClassSetConvert classSetConvert = new ClassSetConvert();

                start_end = classSetConvert.mathStartEnd(classSet);

                System.out.println(courName + "\t" + dayOfWeek + "\t" + start_end[0] + "\t" + start_end[1] + "\t" + teacherName + "\t" + classroomName + "\t" + getWeeks(beginWeek, endWeek, weekOddEven));

                strings = new String[]{courName, ""+dayOfWeek, ""+start_end[0], ""+start_end[1], teacherName, classroomName, getWeeks(beginWeek, endWeek, weekOddEven)};
                createXSSFCell(xssfSheet, i+1, strings);

                weekOddEven = "";

            }

        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("推荐课程表_" + user + ".xlsx");
            xssfWorkbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.println("请先清空模板所有数据【首行除外】，然后粘贴两条虚线间的内容至模板中，保存并导入至WakeUp课程表中.");
        System.out.println("已生成 \"推荐课程表_" + user + ".xlsx\" ，请在程序目录查看.");
    }

    private static void createXSSFCell(XSSFSheet xssfSheet, int row, String[] strings){
        XSSFRow xssfRow = xssfSheet.createRow(row);
        XSSFCell[] xssfCells = new XSSFCell[strings.length];
        for(int i=0; i<xssfCells.length; i++){
            xssfCells[i] = xssfRow.createCell(i);
            xssfCells[i].setCellValue(strings[i]);
        }
    }

    /**
     *
     * @param begin 开始周数
     * @param end 结束周数
     * @param weekOddEven
     *      ""
     *      "e" 双周
     *      "o" 单周
     * @return
     */
    private static String getWeeks(int begin, int end, String weekOddEven){

        String string = "";

        if(begin == end) string += begin;
        else{
            string += begin + "-" + end;
        }

        switch (weekOddEven.length()){
            case 0:{
                break;
            }
            case 1:{
                switch (weekOddEven.toUpperCase()){
                    case "E": {
                        string += "双";
                        break;
                    }
                    case "O": {
                        string += "单";
                        break;
                    }
                    default: {
                        System.out.println("ERROR: value of \"weekOddEven\":\t" + weekOddEven);
                    }
                }
                break;
            }
            default:{
                System.out.println("ERROR: value of \"weekOddEven\":\t" + weekOddEven);
            }
        }

        return string;

    }

}
