//package com.springapp.authentication;
//
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import javax.naming.AuthenticationException;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.text.MessageFormat;
//
///**
// * Created by franschl on 16.02.15.
// */
//public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    public CustomTokenAuthenticationFilter(String defaultFilterProcessesUrl) {
//        super(defaultFilterProcessesUrl);
//        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl));
//        setAuthenticationManager(new NoOpAuthenticationManager());
//        setAuthenticationSuccessHandler(new TokenSimpleUrlAuthenticationSuccessHandler());
//    }
//
//
//    public final String HEADER_SECURITY_TOKEN = "X-CustomToken";
//
//
//    /**
//     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
//     */
//    @Override public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        String token = request.getHeader(HEADER_SECURITY_TOKEN);
//        logger.info("token found:"+token);
//        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(token);
//        if(userAuthenticationToken == null) throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
//        return userAuthenticationToken;
//    }
//
//
//    /**
//     * authenticate the user based on token
//     * @return
//     */
//    private AbstractAuthenticationToken authUserByToken(String token) {
//        if(token==null) {
//            return null;
//        }
//        AbstractAuthenticationToken authToken = new JWTAuthenticationToken(token);
//        try {
//            return authToken;
//        } catch (Exception e) {
//            logger.error("Authenticate user by token error: ", e);
//        }
//        return authToken;
//    }
//
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res,
//                         FilterChain chain) throws IOException, ServletException {
//        super.doFilter(req, res, chain);
//    }
//
//}
