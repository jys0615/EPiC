//package com.semothon.team15.semo_backend.common.authority;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) {
//        String token = resolveToken(request);
//
//        if (token != null) {
//            Claims claims = jwtTokenProvider.validateToken(token);
//
//            if (claims != null) {
//                String username = claims.getSubject();
//                List<?> rawRoles = claims.get("roles", List.class);
//
//                List<SimpleGrantedAuthority> authorities = rawRoles.stream()
//                        .filter(obj -> obj instanceof String)
//                        .map(obj -> new SimpleGrantedAuthority((String) obj))
//                        .collect(Collectors.toList());
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(username, null, authorities);
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            throw new RuntimeException("Filter chain error", e);
//        }
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
