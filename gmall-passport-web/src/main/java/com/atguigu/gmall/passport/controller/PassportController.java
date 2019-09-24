package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.config.JwtUtil;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Value("token.key")
    private String key;

    @Reference
    private UserService userService;

    @RequestMapping("/index")
    public String index(HttpServletRequest request){
        //获取originUrl
        String originUrl = request.getParameter("originUrl");
        //保存originUrl
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    /**
     * 登录方法
     * @param userInfo
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request){
        String salt = request.getHeader("X-forwarded-for");
        //调用登录方法
        UserInfo user = userService.login(userInfo);
        if (user != null) {
            //如果登录成功之后返回token
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("nickName", user.getNickName());
            //生成token
            String token = JwtUtil.encode(key, map, salt);
            return token;
        } else {
            return "fail";
        }
    }

    @RequestMapping("/verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        //获取服务器的ip，token
        //String salt = request.getHeader("X-forwarded-for");
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
        //key+ip,解密token得到用户信息
        Map<String, Object> map = JwtUtil.decode(token, key, salt);
        //判断用户是否登录 key=user：userId：info value=userInfo
        if (map != null && map.size() > 0) {
            String userId = (String) map.get("userId");
            UserInfo userInfo = userService.verify(userId);
            //判断userInfo
            if (userInfo != null) {
                return "success";
            } else {
                return "fail";
            }
        }
        return "fail";
    }

}
