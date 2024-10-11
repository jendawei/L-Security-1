package com.frankmoley.lil.adminweb.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

	static Logger logger = LoggerFactory.getLogger(CustomUserDetailsContextMapper.class); 
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		//System.out.println(">>username: " + username);
		//System.out.println("authorities: " + authorities);
		logger.debug(">>");
		logger.debug("username: " + username);
		logger.debug("authorities: " + authorities);
		
		for (GrantedAuthority grantedAuthority: authorities) {
			logger.debug("--class: " + grantedAuthority.getClass() + 
					", authority: " + grantedAuthority.getAuthority());
		}
		
		AutoUser user = new AutoUser();
		
		//SimpleGrantedAuthority
		ArrayList<? extends GrantedAuthority> authorities_converted = new ArrayList<>();
		((List<GrantedAuthority>)authorities_converted).add(new SimpleGrantedAuthority("ROLE_USER"));
		
		// Get the multi-valued attribute from LDAP
        Attributes attributes = ctx.getAttributes();
        
        logger.debug("authorities_converted: " + authorities_converted);
        
        try {
			for (NamingEnumeration<?> ae = attributes.getAll(); ae.hasMore();) {
			    Attribute attribute = (Attribute) ae.next();
			    logger.debug("processing attribute: " + attribute.getID());
			    
			    /* print each value */
			    for (NamingEnumeration<?> e = attribute.getAll(); e.hasMore(); ) {
			    		logger.debug("--value: " + e.next());
			    }
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		
		user.setUserName(username);
		//user.setAuthorities(authorities);
		user.setAuthorities(authorities_converted);
		
		return user;
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		//
		throw new UnsupportedOperationException("Not implemented");
	}

}
