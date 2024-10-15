package com.frankmoley.lil.adminweb.service;

import java.util.List;
import java.util.Map;

public interface LdapSearchService {

	Map<String, Object> getNodeInfoByDn(String dn, String[] attrNames);

	Map<String, Object> getDepartmentByDn(String dn, String[] attrNames);

	List<String> getDepartmentNameList(String userOrgDn, int levels);

	List<Map<String, Object>> getDepartmentInfoList(String userOrgDn, int levels, String[] attrNames);

	String getDnOfUser(String uid);

	String getDnOfUser(String uid, String searchBase, String objectClass, String idAttrName);

	List<String> getRoleParticipantDns(String searchBase, String roleName);

	List<Map<String, Object>> getRoleParticipants(String searchBase, String roleName, String[] attrNames);

}
