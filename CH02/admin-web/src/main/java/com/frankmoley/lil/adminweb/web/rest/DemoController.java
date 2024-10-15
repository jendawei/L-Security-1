package com.frankmoley.lil.adminweb.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankmoley.lil.adminweb.data.model.UserPrincipal;
import com.frankmoley.lil.adminweb.mapping.ldap.*;
import com.frankmoley.lil.adminweb.service.LdapSearchService;

import java.util.ArrayList;
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
    
    @Value("${ldap.searchBase}")
	private String ldapSearchBase;
    
    @Value("${ldap.ouDisplayAttribute:fullName}")
   	private String ouDisplayAttribute;
    
    //ldap.roleSearchBase
    @Value("${ldap.roleSearchBase}")
   	private String roleSearchBase;
    
    @Value("${ldap.roleObjectClass}")
   	private String roleObjectClass;
    
    @Value("${ldap.roleAttribute:groupMembership}")
    private String roleAttribute;
    
    @Value("${ldap.userObjectClass:inetOrgPerson}")
    private String userObjectClass;
    
    @Autowired
    LdapSearchService ldapSearchService;
    
    @Autowired
	AuthenticationManager authenticationManager;
    

    @RequestMapping("/")
    public String hello(){
       
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
    	//return this.ldapSearchService.getDnOfUser(uid, searchBase, objectClass, idAttrName);
    	return this.ldapSearchService.getDnOfUser(uid);
    }
    
    //MapContextMapper
    
    @RequestMapping("/getDepartment")
    public Map<String, Object> getDepartment(String uid, String searchBase, String objectClass, String idAttrName){
        //
        String dn = getDnOfUser(uid, searchBase, objectClass, idAttrName);
        
        return getDepartmentByDn(dn);
    }
    
    
    @RequestMapping("/getDepartmentByDn")
    public Map<String, Object> getDepartmentByDn(String dn){
        
       return this.ldapSearchService.getDepartmentByDn(dn, null);
    }
    
    @RequestMapping("/getNodeInfoByDn")
    public Map<String, Object> getNodeInfoByDn(String dn, String[] attrNames){
        return this.ldapSearchService.getNodeInfoByDn(dn, attrNames);
    } 
    
    @RequestMapping("/getDepartmentInfoList")
    public List<Map<String, Object>> getDepartmentInfoList(String userOrgDn, int levels, String[] attrNames){
    	
    	return this.ldapSearchService.getDepartmentInfoList(userOrgDn, levels, attrNames);
    }
    
    //ouDisplayAttribute
    
    @RequestMapping("/getDepartmentNameList")
    public List<String> getDepartmentNameList(String userOrgDn, int levels){
    	//
    	return this.ldapSearchService.getDepartmentNameList(userOrgDn, levels);
    }
    
    @RequestMapping("/getRoleParticipantDns")
    public List<String> getRoleParticipants(String searchBase, String roleName){
    	return this.ldapSearchService.getRoleParticipantDns(searchBase, roleName);
    }
    
    @RequestMapping("/getRoleParticipants")
    public List<Map<String, Object>> getRoleParticipants(String searchBase, String roleName, String[] attrNames){
    	return this.ldapSearchService.getRoleParticipants(searchBase, roleName, attrNames);
    }
    
    
    //
    @RequestMapping("/testAuthentication")
	public UserDetails testAuthentication(String uid, String pass) {
		logger.debug("testAuthentication>> uid: " + uid + ", pass: " + pass);
		
		UserDetails userDetails = null;
		Authentication authToken = null;
		
		try {
			Authentication authRequest = new UsernamePasswordAuthenticationToken(uid, pass);
			
			Authentication _authToken = authenticationManager.authenticate(authRequest);
			
			SecurityContextHolder.getContext().setAuthentication(_authToken);
			
			//
			authToken = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authToken.getPrincipal();
			
			if (principal instanceof UserDetails) {
				userDetails = (UserDetails) principal;
			} 
			
		} catch (Throwable exp) {
			//exp.printStackTrace();
			logger.debug("exception: " + exp.getMessage());
			authToken = new AnonymousAuthenticationToken(
				    "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_NONE"));
		}
		
		
		logger.debug("<< authToken: " + (authToken == null? "Nothing": authToken));
		return userDetails;
		//return "testAuthentication is done!";
	}
 
}
