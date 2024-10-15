package com.frankmoley.lil.adminweb.mapping.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.CollectionUtils;

public class MapContextMapper implements ContextMapper<Map<String, Object>>{
	//
	static Logger logger = LoggerFactory.getLogger(MapContextMapper.class);
	
	//
	private List<String> attrNames;
	
	//
	public MapContextMapper(List<String> attrNames) {
		this.attrNames = attrNames;
	}
	
	@Override
	public Map<String, Object> mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter)ctx;
        
        Map<String, Object> attrMap = new HashMap<>();
        
        List<String> _attrNames = new ArrayList<>();
        
		if (CollectionUtils.isEmpty(attrNames)) {
			logger.debug("**empty attrNames...");
			try {
				for (NamingEnumeration<String> ae = context.getAttributes().getIDs(); ae.hasMore();) {
					String attrName = ae.next();
					logger.debug("**processing: " + attrName);
					_attrNames.add(attrName);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			
			logger.debug("**_attrNames: " + _attrNames);
		} else {
			_attrNames.addAll(attrNames);
		}
       
		attrMap.put("dn", context.getDn().toString());
        
		for (String attrName : _attrNames) {
			 //List<Object> values = new ArrayList<>();
			 Object[] objs = context.getObjectAttributes(attrName);
			 //List<Object> values = Arrays.asList(objs);
			 Object value = (objs == null || objs.length == 0)? null: (objs.length == 1? objs[0]: Arrays.asList(objs));
			 attrMap.put(attrName, value);      
		}
       
        
        return attrMap;
    }

}
