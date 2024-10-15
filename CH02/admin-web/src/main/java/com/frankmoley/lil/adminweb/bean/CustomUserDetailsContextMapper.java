package com.frankmoley.lil.adminweb.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.frankmoley.lil.adminweb.data.model.UserPrincipal;

import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

@Component
public class CustomUserDetailsContextMapper extends LdapUserDetailsMapper {

	static Logger logger = LoggerFactory.getLogger(CustomUserDetailsContextMapper.class); 
	
	//In CTCB ldap, the roles(group membership) is defined as the attribute "groupMemberShip" in user-node.
	@Value("${ldap.roleAttribute:groupMembership}")
    private String roleAttribute;
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		logger.debug(">>");
		logger.debug("username: " + username);
		logger.debug("authorities(input): " + authorities);
		
		//Use objectClass to demonstrate how parent class resolve additional authorities.
		//this.setRoleAttributes(ctcb_role_attributes);		
		//this.setRoleAttributes(new String[] {"objectClass"});
		//this.setRoleAttributes(new String[] {roleAttribute});
		
        LdapUserDetailsImpl details = (LdapUserDetailsImpl) super.mapUserFromContext(
        		ctx, username, authorities);
        
        Collection<GrantedAuthority> mergedAuthorities = details.getAuthorities();
        logger.debug("authorities(resolved by parent-class): " + mergedAuthorities);
		
		String dn = ctx.getDn().toString();
        logger.debug("dn: " + dn);
        
        List<String> roleNames = this.resolveRoles(ctx, roleAttribute);
        List<GrantedAuthority> intrestedAuthorities = this.resolveIntrestedAuthorities(roleNames);
        
        UserPrincipal user = new UserPrincipal();
		
		//List<GrantedAuthority> intrestedAuthorities = resolveIntrestedAuthorities(dn, mergedAuthorities);
        //logger.debug("intrestedAuthorities: " + intrestedAuthorities);
        
        //listNodeAttributes(ctx);

        user.setUserName(username);
		user.setAuthorities(intrestedAuthorities);
		user.setDn(dn);
		
		return user;
		
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		//
		throw new UnsupportedOperationException("Not implemented");
	}
	
	private List<GrantedAuthority> resolveIntrestedAuthorities(List<String> roleNames) {
		//demo!
		ArrayList<GrantedAuthority> intrestedAuthorities = new ArrayList<>();
		if (!CollectionUtils.isEmpty(roleNames)) {
			//intrestedAuthorities.addAll(authorities);
			for (String roleName: roleNames) {
				intrestedAuthorities.add(new SimpleGrantedAuthority(roleName));
			}
			
		}
		
		return intrestedAuthorities;
	}
	
	private List<String> resolveRoles(DirContextOperations ctx, String roleAttribute) {
		//
		logger.debug("------------------------------------------------");
		Attributes attributes;
		List<String> roleNames = new ArrayList<>();
		try {
			String[] rolesForAttribute = ctx.getStringAttributes(roleAttribute);
			
			for (String stringValue: rolesForAttribute) {
				//tmp processing for cn=RMAS0003XXX,ou=...
				String roleName = stringValue;
				
		    	int indexOf1stComma = stringValue.indexOf(',');
		    	if (indexOf1stComma >=0) {
		    		roleName = stringValue.substring(0, indexOf1stComma);
		    	}
		    	
		    	int indexOf1stEqual = roleName.indexOf('=');
		    	if (indexOf1stEqual >=0) {
		    		roleName = roleName.substring(indexOf1stEqual + 1);
		    	}
		    	
		    	if (roleName.startsWith("RMAS0003")) {
		    		roleNames.add(roleName);
		    	}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("------------------------------------------------");
		
		return roleNames;
	}

}
