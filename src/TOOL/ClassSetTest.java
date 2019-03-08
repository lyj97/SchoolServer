package TOOL;

import UIMS.GetMD5;
import UIMS.UIMSApprove;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;

public class ClassSetTest {

    static HashMap<Integer, String> classSetMap = new HashMap<>();

    public static void main(String[] args) {
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

        JSONArray courses = uimsApprove.getSelectedCourse().getJSONObject("message").getJSONArray("value");

        JSONObject teachClassMaster;
        JSONArray lessonSchedules;

        JSONObject timeBlock;

        int classSet;
        String name;

        for (int i = 0; i < courses.size(); i++) {

            teachClassMaster = courses.getJSONObject(i).getJSONObject("teachClassMaster");
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");

            for (int j = 0; j < lessonSchedules.size(); j++) {

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");

//                System.out.println(timeBlock);

                name = timeBlock.getString("name");
                classSet = timeBlock.getInt("classSet");

                if (classSetMap.containsKey(classSet)) {

                    System.out.println("removed:\tclassSet:" + classSet + "\tname:" + classSetMap.get(classSet));

                }

                classSetMap.put(classSet, name);

            }


        }

        classSetMap.forEach((k, v) -> System.out.println("classSet:" + k + "\tname:" + v));

    }

}


