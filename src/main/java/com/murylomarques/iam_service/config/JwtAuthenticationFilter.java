package com.murylomarques.iam_service.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.murylomarques.iam_service.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Verificar se existe o Header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extrair o Token (remove o prefixo "Bearer ")
        jwt = authHeader.substring(7);
        
        // 3. Extrair o email do token
        userEmail = jwtService.extractUsername(jwt);

        // 4. Se o usuário não estiver autenticado ainda no contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // 5. Validar o token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // 6. Criar o objeto de autenticação do Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 7. Atualizar o contexto de segurança (Diz: "Esse cara tá logado!")
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Segue o baile para o próximo filtro
        filterChain.doFilter(request, response);
    }
}