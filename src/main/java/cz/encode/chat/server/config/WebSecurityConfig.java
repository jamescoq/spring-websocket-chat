package cz.encode.chat.server.config;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().headers()
		        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
		        .and().formLogin().defaultSuccessUrl("/index.html").loginPage("/login.html").permitAll().and().logout()
		        .logoutSuccessUrl("/login.html").logoutUrl("/logout").permitAll().and().authorizeRequests()
		        .antMatchers("/css/**").permitAll().anyRequest().authenticated().and();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new ChatAuthenticationProvider());
	}

	private class ChatAuthenticationProvider implements AuthenticationProvider {

		private static final String UNDERSCORE = "_";
		private static final String PASSWORD = "chat";

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {

			// user is authenticated only when he has proper password and was not authenticated before
			if (!authentication.isAuthenticated()) {

				if (PASSWORD.equals(authentication.getCredentials())) {

					String randomString = RandomStringUtils.randomAlphanumeric(5);
					return new UsernamePasswordAuthenticationToken(authentication.getPrincipal() + UNDERSCORE + randomString,
					        authentication.getCredentials(), authentication.getAuthorities());

				} else {
					throw new BadCredentialsException("Authentication of user failed. Enter correct credentials.");
				}

			} else {
				return authentication;
			}
		}

		@Override
		public boolean supports(Class<?> authentication) {
			return authentication.equals(UsernamePasswordAuthenticationToken.class);
		}
	}
}
