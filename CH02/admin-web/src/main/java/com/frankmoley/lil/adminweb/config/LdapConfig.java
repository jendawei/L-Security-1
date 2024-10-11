package com.frankmoley.lil.adminweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.util.StringUtils;

@Configuration
public class LdapConfig {
	static Logger logger = LoggerFactory.getLogger(LdapConfig.class);
	
	//
	@Value("${ldap.url:ldap://localhost:8389/dc=landon,dc=org}")
    private String ldapUrl;
	
	@Value("${ldap.user:}")
    private String ldapUser;
	
	@Value("${ldap.pass:}")
    private String ldapPass;
	
	@Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        logger.debug("**ldapUrl: " + ldapUrl);
       
        contextSource.setUrl(ldapUrl);
        
        if (StringUtils.hasText(ldapUser)) {
        	System.out.println("**ldapUser: " + ldapUser);
        	contextSource.setUserDn(ldapUser);
        	contextSource.setPassword(ldapPass);
        }

        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
