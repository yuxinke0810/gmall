package com.atguigu.gmall.config;

import com.alibaba.fastjson.JSON;
import com.arguigu.gmall.util.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    /**
     * 拦截之前执行，用户进入控制器之前
     * @param request request
     * @param response response
     * @param handler handler
     * @return boolean
     * @throws Exception Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = request.getParameter("newToken");
        //将token放入cookie中
//        Cookie cookie = new Cookie("token", token);
//        response.addCookie(cookie);
        if (token != null) {
            CookieUtil.setCookie(request, response, "token", token, WebConst.COOKIE_MAXAGE, false);
        }
        //当用户访问非登录之后的页面，登录之后继续访问其他业务模块url
        if (token == null) {
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        //从cookie中获取token，解密token 获取nickName
        if (token != null) {
            Map map = getUserMapByToken(token);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName", nickName);
        }
        //在拦截器中获取方法上的注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //获取方法上的注解
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation != null) {
            //有注解 获取服务器上的ip地址
            String salt = request.getHeader("X-forwarded-for");
            //判断用户是否登录 调用verify
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&salt=" + salt);
            if ("success".equals(result)) {
                //已登录 认证成功
                //保存userId
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                //认证失败 且methodAnnotation.autoRedirect() = true:必须登录
                if (methodAnnotation.autoRedirect()) {
                    String requestUrl = request.getRequestURL().toString();
                    System.out.println("requestUrl = " + requestUrl);
                    String encodeURL = URLEncoder.encode(requestUrl, "UTF-8");
                    System.out.println("encodeURL = " + encodeURL);
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * 解密token 获取map数据
     * @param token token
     * @return Map<String, Object>
     */
    private Map getUserMapByToken(String token) {
        //获取token中间部分
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        //将tokenUserInfo进行base64解码
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        //解码之后的byte数组
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        String mapJson = null;
        try {
            mapJson = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(mapJson, Map.class);
    }

    /**
     * 进入控制器之后，试图渲染之前
     * @param request request
     * @param response response
     * @param handler handler
     * @param modelAndView modelAndView
     * @throws Exception Exception
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 视图渲染之后
     * @param request request
     * @param response response
     * @param handler handler
     * @param ex ex
     * @throws Exception Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
