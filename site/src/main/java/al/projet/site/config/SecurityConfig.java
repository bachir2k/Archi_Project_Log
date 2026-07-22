package al.projet.site.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF desactive uniquement sur l'API interne (utile pour tester avec
            // Postman/curl avant que le formulaire HTML du back-office n'existe).
            // A retirer si vous appelez cette API depuis vos propres formulaires
            // Thymeleaf, qui embarquent deja le jeton CSRF automatiquement.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/editeur/api/**", "/admin/api/**"))
            .authorizeHttpRequests(auth -> auth
                // consultation publique : accueil, detail article, categories
                .requestMatchers("/", "/articles/**", "/categories/**", "/css/**", "/js/**").permitAll()
                // gestion articles/categories : editeur + admin
                .requestMatchers("/editeur/**").hasAnyRole("EDITEUR", "ADMIN")
                // gestion utilisateurs + jetons : admin uniquement
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Pas de loginPage() personnalisee pour l'instant -> Spring Security
            // fournit un formulaire de login par defaut sur /login, utilisable
            // tel quel pour tester l'authentification avant que le template
            // Thymeleaf de login (prevu jeudi/vendredi) ne soit pret.
            .formLogin(form -> form.permitAll())
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}
