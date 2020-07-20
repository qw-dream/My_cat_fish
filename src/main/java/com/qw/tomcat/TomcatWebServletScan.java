package com.qw.tomcat;

import com.qw.tomcat.annotation.WebServlet;
import com.qw.tomcat.exception.UriUnuniqueException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class TomcatWebServletScan {

    public HashMap<String, HttpServlet> servletScan() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        HashMap<String, HttpServlet> servletHashMap = new HashMap<>();
        String basePackage = "com.qw.test";
        String path = basePackage.replaceAll("\\.", "/");
        URL url = TomcatWebServletScan.class.getResource("/");
        File file = new File(url.getPath() + path);
        File[] files = file.listFiles();
        for (int i = 0,size=files.length; i < size; i++) {
            //文件
            if (files[i].isFile()){
                //所有的class文件
                String fileName = files[i].getName();
                if (fileName.endsWith(".class")){
                    String[] strings = fileName.split("\\.");
                    String className = strings[0];
                    String allClassName = basePackage + "." + className;
                    Class<?> aClass = Class.forName(allClassName);
                    WebServlet annotation = aClass.getAnnotation(WebServlet.class);
                    if (annotation != null){
                        String uri = annotation.value();
                        if (servletHashMap.get(uri) != null){
                            throw new UriUnuniqueException("出现了两个一样的地址：" + uri);
                        }
                        HttpServlet servlet = (HttpServlet) aClass.newInstance();
                        servletHashMap.put(uri,servlet);
                    }

                }
            } else {
                //文件
            }
        }
        return servletHashMap;
    }

}
