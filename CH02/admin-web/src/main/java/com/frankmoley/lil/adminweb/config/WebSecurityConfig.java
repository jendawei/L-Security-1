package com.frankmoley.lil.adminweb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import com.frankmoley.lil.adminweb.bean.CustomUserDetailsContextMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home", "/demo/**").permitAll()
                .antMatchers("/customers/**").hasRole("USER")
                .antMatchers("/orders").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .permitAll()
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        ;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //
        //addJdbcAuthentication(auth);
        addLdapAuthentication(auth);
        //addJdbcAuthentication(auth);
        addDaoAuthentication(auth);
    }

    private void addLdapAuthentication_old(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .userDetailsContextMapper(userDetailsContextMapper)
                //.contextSource()
                .contextSource(ldapContextSource);
     
    }
    
    private void addLdapAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    	/*StringBuilder queryBuffer = new StringBuilder().append(
    			"(&(objectclass=").append(userObjectClass).append(")(").append(userIdAttribute).append("={0}").append("))");*/
        auth.ldapAuthentication()
                //.userSearchFilter("(&(objectClass=inetOrgPerson)(uid={0}))")
                .userSearchFilter(ldapSearhFilter)
                .userDetailsContextMapper(userDetailsContextMapper)
                .contextSource(ldapContextSource);
    }

    private void addJdbcAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                //.usersByUsernameQuery("select username, password, enabled from users where username=?")
                //.authoritiesByUsernameQuery("select username, authority from authorities where username=?")
                .passwordEncoder(new BCryptPasswordEncoder())
        ;
    }

    private void addDaoAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new JdbcUserDetailsManager(dataSource))
    //userDetailsPasswordManager((UserDetailsPasswordService) new JdbcUserDetailsManager(dataSource))
                //.dataSource(dataSource)
                //.usersByUsernameQuery("select username, password, enabled from users where username=?")
                //.authoritiesByUsernameQuery("select username, authority from authorities where username=?")
                .passwordEncoder(new BCryptPasswordEncoder())
        ;
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
	LdapContextSource ldapContextSource;
    
    @Autowired
	CustomUserDetailsContextMapper userDetailsContextMapper;
    
    @Value("${ldap.searhFilter:}")
	private String ldapSearhFilter;
    
    @Value("${ldap.userObjectClass:inetOrgPerson}")
    private String userObjectClass;
    
    @Value("${ldap.userIdAttribute:cn}")
    private String userIdAttribute;
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
}
