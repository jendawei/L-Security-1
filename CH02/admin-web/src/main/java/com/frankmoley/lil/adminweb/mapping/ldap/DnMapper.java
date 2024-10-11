package com.frankmoley.lil.adminweb.mapping.ldap;

import javax.naming.NamingException;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class DnMapper implements ContextMapper<String>{

	@Override
	public String mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter)ctx;
        return context.getDn().toString(); // returns the DN as a String
    }

}
