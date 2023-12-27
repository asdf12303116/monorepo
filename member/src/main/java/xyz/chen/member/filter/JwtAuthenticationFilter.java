package xyz.chen.member.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.chen.commons.base.UserInfo;
import xyz.chen.commons.utils.JwtUtils;
import xyz.chen.member.entity.AuthUser;
import xyz.chen.member.services.AuthService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService authService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        UserInfo userInfo = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                userInfo = JwtUtils.getUserInfo(jwtToken);
            } catch (Exception e) {
                // Handle token extraction/validation errors
                System.out.println("Error extracting username from token: " + e.getMessage());
            }
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
