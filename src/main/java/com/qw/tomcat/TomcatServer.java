package com.qw.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class TomcatServer {

    public static HashMap<String, HttpServlet> servletHashMap;

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        //创建一个ServerSocket对象
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Tomcat start");
        System.out.println("Scan All Servlet");
        TomcatWebServletScan tomcatWebServletScan = new TomcatWebServletScan();
        servletHashMap = tomcatWebServletScan.servletScan();

        while (true){

            //没来一个请求就会有一个socket对象被创建
            //当没有请求的时候，会一直被阻塞在此处
            Socket socket = serverSocket.accept();

            OutputStream outputStream = socket.getOutputStream();
            //从输入流中可以读取到浏览器发给我们的消息
            InputStream inputStream = socket.getInputStream();

            //服务器收到浏览器的请求之后，需要返回202，之后才能读取到数据
            String begin = "HTTP/1.1 202 Accepted\n" +
                    // "Location: http://www.baidu.com\n"+
                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                    "Server: Apache\n";
            outputStream.write(begin.getBytes());
            outputStream.flush();

            //用来存放input读取到数据
            byte[] buffer = new byte[1024];
            //用来记录读取了多少字节
            int len = 0;

            StringBuilder stringBuilder = new StringBuilder();
            //如果没有数据了，input就会返回一个-1
            while (true) {
                len = inputStream.read(buffer);

                //浏览器如果不关闭，此处流就会一直等待
                System.out.println(new String(buffer,0,len));
                stringBuilder.append(new String(buffer,0 ,len));

                if (len < 1024){
                    break;
                }
            }

            HttpProtocol httpProtocol = paresHttpStr(stringBuilder.toString());
            //想问的Servlet
            String servletUri = httpProtocol.getServletUri();

            HttpServlet httpServlet = servletHashMap.get(servletUri);
            //进行Get和Post请求转发
            if ("Get".toUpperCase().equals(httpProtocol.getRequestMethod())){
                //Get请求
                httpServlet.doGet(); //接口回调
            } else if ("Post".toUpperCase().equals(httpProtocol.getRequestMethod())){
                //Post请求
                httpServlet.doPost(); //接口回调
            }

            String body = "<html><head></head><body><h1>HelloWorld</h1></body></html>";

            String str = "HTTP/1.1 200 OK\n" +
                    // "Location: http://www.baidu.com\n"+
                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                    "Server: Apache\n" +
                    "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                    "ETag: \"34aa387-d-1568eb00\"\n" +
                    "Accept-Ranges: bytes\n" +
                    "Content-Length: "+body.length()+"\n" +
                    "Vary: Accept-Encoding\n" +
                    "Content-Type: text/html\n"+
                    "\n"+body;

            outputStream.write(str.getBytes());
            outputStream.flush();

            inputStream.close();
            outputStream.close();

        }

    }

    private static HttpProtocol paresHttpStr(String http){
        HttpProtocol httpProtocol = new HttpProtocol();

        String[] strings = http.split("\n");
        System.out.println(strings);
        for (int i = 0,len=strings.length; i < len; i++) {
            if (i == 0){
                String[] s = strings[0].split(" ");
                httpProtocol.setRequestMethod(s[0]);
                httpProtocol.setUrl(s[1]);
                String[] split = s[1].split("\\?");
                httpProtocol.setServletUri(split[0]);
            }
        }
        return httpProtocol;
    }

}