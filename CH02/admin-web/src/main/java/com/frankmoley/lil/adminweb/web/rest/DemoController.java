package com.frankmoley.lil.adminweb.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.frankmoley.lil.adminweb.mapping.ldap.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {
	//
	static Logger logger = LoggerFactory.getLogger(DemoController.class); 
    //
    //
    @Autowired
    LdapTemplate ldapTemplate;
    
    @Value("${ldap.searchBase:}")
	private String ldapSearchBase;

    @RequestMapping("/")
    public String hello(){
        //log.debug(">>**hello");

        //log.debug("<<**hello");

        return "hello";
    }

    @RequestMapping("/testLdap")
    public String testLdap(){
        //
        /*List<String> ouList = this.ldapTemplate.search(ldapSearchBase,
                "(objectclass=person)",
                (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
        */
    	List<String> ouList = this.ldapTemplate.search(ldapSearchBase,
                "(objectclass=person)", new DnMapper());
      
        return ouList.toString();
    }

    @RequestMapping("/listAllOus")
    public String listAllOus(String searchBase){
        //
    	if (!StringUtils.hasLength(searchBase)) {
    		searchBase = ldapSearchBase;
    	}
    	
    	//
        List<String> dns = ldapTemplate.search(searchBase,
                "(objectclass=organizationalUnit)", new DnMapper());

        return dns == null? "Nothing found!": dns.toString();
    }
    
    @RequestMapping("/getUserDn")
    public String getDnOfUser(String uid, String searchBase, String objectClass, String idAttrName){
    	//
    	if (!StringUtils.hasLength(searchBase)) {
    		searchBase = ldapSearchBase;
    	}
    	
        //
    	if (!StringUtils.hasLength(objectClass)) {
    		objectClass = "inetOrgPerson";
    	}
    	
    	//
    	if (!StringUtils.hasLength(idAttrName)) {
    		idAttrName = "cn";
    	}
    	
    	StringBuilder queryBuffer = new StringBuilder().append(
    			"(&(objectclass=").append(objectClass).append(")(").append(idAttrName).append("=").append(uid).append("))");;

        List<String> dns = ldapTemplate.search(searchBase, queryBuffer.toString(), new DnMapper());

        return dns == null? "Nothing found!": dns.get(0);
    }
    
    //MapContextMapper
    
    @RequestMapping("/getDepartment")
    public  Map<String, Object> getDepartment(String uid, String searchBase, String objectClass, String idAttrName){
        //
        String dn = getDnOfUser(uid, searchBase, objectClass, idAttrName);
        
        logger.info("dn: " + dn);
        
        String searchFrom = dn;
        int indexOfComma = dn.indexOf(',');
        String deptDn = searchFrom.substring(indexOfComma + 1);
        
        logger.info("deptDn: " + deptDn);
        String[] attrNames = {}; //{"ou", "objectclass"};
        Map<String, Object> result = ldapTemplate.lookup(
        		deptDn, new MapContextMapper(Arrays.asList(attrNames)));

        return result;
    }
}
