package xyz.chen.member.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.chen.commons.base.UserInfo;
import xyz.chen.commons.config.ConfigData;
import xyz.chen.commons.utils.JwtUtils;
import xyz.chen.member.entity.AuthUser;
import xyz.chen.member.services.AuthService;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService authService;

    @Autowired
    ConfigData configData;

    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> passUrls = List.of(
            "/actuator",
            "/actuator/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        UserInfo userInfo = null;
        String jwtToken;
        String url = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());

        // 白名单
        if (configData.getPermitAllUrls().stream().anyMatch(checkUrl -> pathMatcher.match(checkUrl, url))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (method.equals(HttpMethod.OPTIONS)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (passUrls.stream().anyMatch(checkUrl -> pathMatcher.match(checkUrl, url))) {
            filterChain.doFilter(request, response);
            return;
        }



        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            userInfo = JwtUtils.getUserInfo(jwtToken);
        }


        if (userInfo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new AuthUser(userInfo);

//            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }


            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("anonymous", "anonymous",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationToken);
        }

        filterChain.doFilter(request, response);


    }
}
