package com.cangqiong.takeaway.interceptor;

import com.cangqiong.takeaway.utils.JwtUtil;
import com.cangqiong.takeaway.utils.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String CAMPUS_PUBLIC_PREFIX = "/api/campus/public/";
    private static final String CAMPUS_ADMIN_PREFIX = "/api/campus/admin/";
    private static final String CAMPUS_COURIER_TOKEN_PATH = "/api/campus/courier/auth/token";
    private static final String CAMPUS_COURIER_PROFILE_PATH = "/api/campus/courier/profile";
    private static final String CAMPUS_COURIER_REVIEW_STATUS_PATH = "/api/campus/courier/review-status";

    private static final Set<String> PUBLIC_GET_PATHS = Set.of(
            "/api/public/categories",
            "/api/public/dishes",
            "/api/public/shop/status"
    );

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (isPublicRequest(request)) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("请求缺少有效的Authorization头");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.unauthorized()));
            return false;
        }

        token = token.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.warn("Token验证失败或已过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.unauthorized("Token已过期或无效")));
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String userType = jwtUtil.getUserTypeFromToken(token);
        String requiredUserType = resolveRequiredUserType(requestUri);

        if (!isUserTypeAllowed(requestUri, requiredUserType, userType)) {
            log.warn("用户类型不匹配: uri={}, required={}, actual={}", requestUri, requiredUserType, userType);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.forbidden()));
            return false;
        }

        BaseContext.setCurrentUserId(userId);
        BaseContext.setCurrentUserType(userType);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.clear();
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (uri.startsWith(CAMPUS_PUBLIC_PREFIX)) {
            return true;
        }

        if (CAMPUS_COURIER_TOKEN_PATH.equals(uri)) {
            return true;
        }

        if ("/api/employees/login".equals(uri) || "/api/users/login".equals(uri)) {
            return true;
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (PUBLIC_GET_PATHS.contains(uri)) {
                return true;
            }

            return uri.startsWith("/api/public/dishes/") || uri.startsWith("/api/files/");
        }

        return false;
    }

    private String resolveRequiredUserType(String uri) {
        if (uri.startsWith(CAMPUS_PUBLIC_PREFIX)) {
            return null;
        }
        if (uri.startsWith(CAMPUS_ADMIN_PREFIX)) {
            return "employee";
        }
        if (uri.startsWith("/api/campus/customer/")) {
            return "customer";
        }
        if (uri.startsWith("/api/campus/courier/")) {
            return "courier";
        }
        if (uri.startsWith("/api/users/") || uri.startsWith("/api/user/")) {
            return "customer";
        }
        if (uri.startsWith("/api/public/")) {
            return null;
        }
        return "employee";
    }

    private boolean isUserTypeAllowed(String uri, String requiredUserType, String actualUserType) {
        if (requiredUserType == null) {
            return true;
        }
        if (requiredUserType.equals(actualUserType)) {
            return true;
        }
        if (isCourierBridgePath(uri)) {
            return "customer".equals(actualUserType) || "courier".equals(actualUserType);
        }
        return false;
    }

    private boolean isCourierBridgePath(String uri) {
        // Step 03E 仍保留 onboarding bridge：
        // 未审核通过的用户还无法拿到 courier token，因此资料提交与审核状态查询继续允许 customer/courier 双通道。
        return CAMPUS_COURIER_PROFILE_PATH.equals(uri) || CAMPUS_COURIER_REVIEW_STATUS_PATH.equals(uri);
    }
}
