package com.lzw.crm.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * jackson工具可以将对象、对象数组、对象List集合、Map集合解析成JSON字符串
 */
public class PrintJson {

    //将boolean值解析为json串；如果只需要为前端提供一个布尔标记，调用本方法，如增删改，操作成功或失败都封装成一个布尔标记响应给前端
    public static void printJsonFlag(HttpServletResponse response, boolean flag) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        //将接收的布尔值包装成Map集合的一个键值对
        map.put("success", flag);

        ObjectMapper om = new ObjectMapper();
        try {
            //{"success":true}
            String json = om.writeValueAsString(map);
            response.getWriter().print(json);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //将对象解析为json串；除了仅响应布尔标记外，其余的都调用本方法转JSON字符串，包括对象和map
    public static void printJsonObj(HttpServletResponse response, Object obj) {

        /*
         *
         * Person p
         * 	id name age
         * {"id":"?","name":"?","age":?}
         *
         * List<Person> pList
         * [{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?}...]
         *
         * Map
         * 	key value
         * {key:value}
         *
         *
         */

        ObjectMapper om = new ObjectMapper();
        try {
            String json = om.writeValueAsString(obj);
            response.getWriter().print(json);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}