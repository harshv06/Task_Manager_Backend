package com.consultadd.taskmanager.filter;

import com.consultadd.taskmanager.service.JwtService;
import com.consultadd.taskmanager.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailService UserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader=request.getHeader("Authorization");
        final String jwt;
        final String userName;

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        try{
            jwt=jwtGetFromRequest(request);

            if (jwtService.isRefreshToken(jwt)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Refresh token cannot be used for authentication");
                return;
            }

            if (!jwtService.isTokenValid(jwt)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token");
                return;
            }

            userName = jwtService.extractUsernameFromToken(jwt);

            // If the username is valid and the user is not yet authenticated in the current context
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.UserDetailService.loadUserByUsername(userName);

                // Validate the token and user details
                if (jwtService.validateTokenForUser(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the user in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT authentication failed: ", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Authentication failed: " + e.getMessage());
            return;
        }
            filterChain.doFilter(request,response);

    }

    public String jwtGetFromRequest(HttpServletRequest httpServletRequest){
        final String authHeader=httpServletRequest.getHeader("Authorization");
        return authHeader.substring(7);
    }
}
