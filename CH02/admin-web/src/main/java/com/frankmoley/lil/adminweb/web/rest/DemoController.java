package com.frankmoley.lil.adminweb.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    //
    @Autowired
    LdapTemplate ldapTemplate;

    @RequestMapping("/")
    public String hello(){
        //log.debug(">>**hello");

        //log.debug("<<**hello");

        return "hello";
    }

    @RequestMapping("/testLdap")
    public String testLdap(){
        //
        List<String> ouList = this.ldapTemplate.search("ou=people",
                "(objectclass=person)",
                (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());

        return ouList.toString();
    }

    @RequestMapping("/listAllOus")
    public String listAllOus(){
        //
        /*List<String> ouList = this.ldapTemplate.search("",
                "(objectclass=top)",
                (AttributesMapper<String>) attrs -> "'" + ((String) attrs.get("dn").get()) + "'");

         */

        List<String> dns = ldapTemplate.search("",
                "(objectclass=organizationalUnit)",
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object ctx) {
                        DirContextAdapter context = (DirContextAdapter)ctx;
                        return "'" + context.getDn().toString() + "'"; // returns the DN as a String
                    }
                }
        );

        return dns == null? "Nothing found!": dns.toString();
    }
}
