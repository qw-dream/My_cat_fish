package com.qw.test;

import com.qw.tomcat.HttpServlet;
import com.qw.tomcat.annotation.WebServlet;

@WebServlet("/user")
public class UserServlet implements HttpServlet {
    @Override
    public void doGet() {
        System.out.println("Get方法");
    }

    @Override
    public void doPost() {
        System.out.println("Post方法");
    }
}