package com.frankmoley.lil.adminweb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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

    private void addLdapAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .userDetailsContextMapper(userDetailsContextMapper())
                //.contextSource()
                .contextSource(ldapContextSource)
                //.url("ldap://localhost:8389/dc=landon,dc=org")
                //.and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPassword");
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

    /*
    @Bean
    public UserDetailsService users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
     */

    //@Bean
    /*
    public GrantedAuthoritiesMapper authoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true);
        return authorityMapper;
    }*/

//    @Bean
//    public LdapContextSource contextSource() {
//        LdapContextSource contextSource = new LdapContextSource();
//
//        /*
//        .contextSource()
//                .url("ldap://localhost:8389/dc=landon,dc=org")
//         */
//        contextSource.setUrl("ldap://localhost:8389/dc=landon,dc=org");
//       /* contextSource.setBase(
//                env.getRequiredProperty("ldap.partitionSuffix"));
//        contextSource.setUserDn(
//                env.getRequiredProperty("ldap.principal"));
//        contextSource.setPassword(
//                env.getRequiredProperty("ldap.password"));*/
//
//        return contextSource;
//    }

    @Autowired
	LdapContextSource ldapContextSource;
    
    
    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
    	CustomUserDetailsContextMapper contextMapper = new CustomUserDetailsContextMapper();
        
        return contextMapper;
    }
}
