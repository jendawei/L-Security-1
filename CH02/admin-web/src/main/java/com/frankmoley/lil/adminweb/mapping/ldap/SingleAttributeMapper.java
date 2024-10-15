package com.frankmoley.lil.adminweb.mapping.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

public class SingleAttributeMapper implements AttributesMapper<String> {

    private final String attributeName;

    public SingleAttributeMapper(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String mapFromAttributes(Attributes attrs) throws NamingException {
        return (String) attrs.get(attributeName).get();
    }

}