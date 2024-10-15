package com.frankmoley.lil.adminweb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.frankmoley.lil.adminweb.mapping.ldap.DnMapper;
import com.frankmoley.lil.adminweb.mapping.ldap.MapContextMapper;
import com.frankmoley.lil.adminweb.mapping.ldap.SingleAttributeMapper;

@Service
public class LdapSearchServiceImpl implements LdapSearchService{
	//
	static Logger log = LoggerFactory.getLogger(LdapSearchServiceImpl.class);
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
    
    @Value("${ldap.userIdAttribute:cn}")
    private String userIdAttribute;
    
    //
    @Override
	public String getDnOfUser(String uid, String searchBase, String objectClass, String idAttrName){
    	//
    	if (!StringUtils.hasLength(searchBase)) {
    		searchBase = ldapSearchBase;
    	}
    	
        //
    	if (!StringUtils.hasLength(objectClass)) {
    		objectClass = userObjectClass;
    	}
    	
    	//
    	if (!StringUtils.hasLength(idAttrName)) {
    		idAttrName = userIdAttribute;
    	}
    	
    	StringBuilder queryBuffer = new StringBuilder().append(
    			"(&(objectclass=").append(objectClass).append(")(").append(idAttrName).append("=").append(uid).append("))");;

        List<String> dns = ldapTemplate.search(searchBase, queryBuffer.toString(), new DnMapper());

        return dns == null? "Nothing found!": dns.get(0);
    }
    
    //
    private List<String> resolveDepartmentDns(String userOrgDn, int levels){
    	//
    	boolean done = false;
    	String workingDn = userOrgDn;
    	int processedLevels = 0;
    	
    	List<String> result = new ArrayList<>();
    	
    	while (!done) {
    		result.add(workingDn);
    		
    		//
    		processedLevels++;
    		
    		//
    		if (processedLevels >= levels) {
    			done = true;
    		} else {
    			workingDn = resolveParentDn(workingDn);
    			if (workingDn == null) {
    				done = true;
    			}
    		}
    	}
    	
    	return result;
    }
    
    private String resolveParentDn(String dn) {
    	int indexOfComma = dn.indexOf(',');
    	
    	if (indexOfComma < 0) {
    		return null;
    	}
    	
        String parentDn = dn.substring(indexOfComma + 1);
        
        return parentDn;
    }
    
    private String resolveRoleDn(String roleName) {
    	return "cn=" + roleName + "," + roleSearchBase;
    }
    
    private List<String> _getRoleParticipantDns(String searchBase, String roleDn){
    	//
    	log.debug("**roleDn: " + roleDn);
    	
    	StringBuilder queryBuffer = new StringBuilder().append(
    			"(&").append(
    			"(objectclass=").append(userObjectClass).append(")").append(
    			"(").append(roleAttribute).append("=").append(roleDn).append(")").append(
    			")");
    	
    	log.debug("**query: " + queryBuffer.toString());
    	
    	List<String> participantDns = ldapTemplate.search(
    			(StringUtils.hasText(searchBase))? searchBase: ldapSearchBase,
    			queryBuffer.toString(), new DnMapper()
        );

        return participantDns;
    }
    
    private List<Map<String, Object>> _getRoleParticipants(String searchBase, String roleDn, String[] attrNames){
    	//
    	log.debug("**roleDn: " + roleDn);
    	
    	StringBuilder queryBuffer = new StringBuilder().append(
    			"(&").append(
    			"(objectclass=").append(userObjectClass).append(")").append(
    			"(").append(roleAttribute).append("=").append(roleDn).append(")").append(
    			")");
    	
    	log.debug("**query: " + queryBuffer.toString());
    	
    	List<String> attrNameList = (attrNames == null)? new ArrayList<>():Arrays.asList(attrNames); 
    	
    	//
    	List<Map<String, Object>> participantMaps  = ldapTemplate.search(
    			(StringUtils.hasText(searchBase))? searchBase: ldapSearchBase,
    			queryBuffer.toString(), new MapContextMapper(attrNameList)
        );

        return participantMaps;
    }
    
    //
    @Override
	public String getDnOfUser(String uid){
    	return this.getDnOfUser(uid, ldapSearchBase, userObjectClass, userIdAttribute);
    }
    
    //
    @Override
	public Map<String, Object> getNodeInfoByDn(String dn, String[] attrNames){
        //
        
        List<String> attrNameList = (attrNames == null)? new ArrayList<>():Arrays.asList(attrNames); 
        //
        return ldapTemplate.lookup(dn, new MapContextMapper(attrNameList));
    } 
    
    @Override
	public Map<String, Object> getDepartmentByDn(String dn, String[] attrNames){
        //
        //log.info("dn: " + dn);
        
        return getNodeInfoByDn(dn, null);
       
    }
    
    @Override
	public List<Map<String, Object>> getDepartmentInfoList(String userOrgDn, int levels, String[] attrNames){
    	List<String> deptDnList = resolveDepartmentDns(userOrgDn, levels);
    	
    	List<Map<String, Object>> result = new ArrayList<>();
    	for (String deptDn: deptDnList) {
    		Map<String, Object> infoMap = getNodeInfoByDn(deptDn, attrNames);
    		result.add(infoMap);
    	}
    	
    	return result;
    }
    
    @Override
	public List<String> getDepartmentNameList(String userOrgDn, int levels){
    	//
    	List<String> deptDnList = resolveDepartmentDns(userOrgDn, levels);
    	
    	List<String> result = new ArrayList<>();
    	
    	for (String deptDn: deptDnList) {
    		//
    		String ouDisplay = ldapTemplate.lookup(deptDn, new SingleAttributeMapper(ouDisplayAttribute));
    		
    		result.add(ouDisplay);
    	}
    	
    	return result;
    }
    
    @Override
	public List<String> getRoleParticipantDns(String searchBase, String roleName){
    	String roleDn = resolveRoleDn(roleName);  
    
        return _getRoleParticipantDns(searchBase, roleDn);
    }

    
    @Override
	public List<Map<String, Object>> getRoleParticipants(String searchBase, String roleName,  String[] attrNames){
    	String roleDn = resolveRoleDn(roleName);  
    
        return this._getRoleParticipants(searchBase, roleDn, attrNames);
    }
}
