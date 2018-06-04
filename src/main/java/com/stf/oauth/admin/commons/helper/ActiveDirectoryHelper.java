package com.stf.oauth.admin.commons.helper;


import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Clase de apoyo de gestión de Active Directory 
 * para el Modulo SSO
 * @author team ssoinnova4j
 */
public class ActiveDirectoryHelper{
	
	// Logger
	private static final Logger LOG = Logger.getLogger(ActiveDirectoryHelper.class.getName());

    private Properties properties;
    private DirContext dirContext;
    private SearchControls searchCtls;
	private String[] returnAttributes = {"sAMAccountName", "givenName", "cn", "mail", 
			"distinguishedname","userAccountControl","admincount", "description"};
    private String domainBase;
    
    
    public ActiveDirectoryHelper() {
    	
    }

    /**
     * Incialización del Contexto del LDAP
     *
     * @param username Usuario para establecer conexión al LDAP
     * @param password Clave del usuario para establecer conexión al LDAP
     * @param domain nombre o ip de servidor LDAP
     */
    public void cargarContextoLdap(String username, String password, String domain, String servidor) {

    	//Propiedades del contexto LDAP
        properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        properties.put(Context.PROVIDER_URL, "LDAP://" + domain);
        properties.put(Context.SECURITY_AUTHENTICATION, "simple");
        //properties.put(Context.SECURITY_PRINCIPAL, "INNOVA4J@intranet.studiof.com.co");
        properties.put(Context.SECURITY_PRINCIPAL, servidor);
        properties.put(Context.SECURITY_CREDENTIALS, password);
        properties.put(Context.REFERRAL, "follow");

        //inicialización de la conexion al servidor LDAP
        try {
			dirContext = new InitialDirContext(properties);
		} catch (NamingException e) {
			LOG.severe(e.getMessage());
		}

        //dominio de busqueda por defecto
        domainBase = getDomainBase(domain);

        //inicialización de los controles de busqueda
        searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setReturningAttributes(returnAttributes);
    }
    
    /**
     * Autentica a un usuario en el Active Dierctory
     *
     * @param username Usuario para establecer conexión al LDAP
     * @param password Clave del usuario para establecer conexión al LDAP
     * @param domain nombre o ip de servidor LDAP
     * @param dominioServidor nombre contexto del dominio del ldap server
     */
    public Boolean isUserValidInLDAP(String username, String password, String domain, String dominioServidor) {
        //Propiedades del contexto LDAP
        properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        properties.put(Context.PROVIDER_URL, "LDAP://" + domain);
        properties.put(Context.SECURITY_AUTHENTICATION, "simple");
        //properties.put(Context.SECURITY_PRINCIPAL, "INNOVA4J@intranet.studiof.com.co");
        properties.put(Context.SECURITY_PRINCIPAL, username+"@"+dominioServidor);
        //properties.put(Context.SECURITY_PRINCIPAL, servidor);
        properties.put(Context.SECURITY_CREDENTIALS, password);
        properties.put(Context.REFERRAL, "follow");

        //inicialización de la conexion al servidor LDAP
        try {
            dirContext = new InitialDirContext(properties);
        } catch (NamingException e) {
            LOG.severe(e.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }
    
    /**
     * Buscar en el directorio activo por email/username
     * 
     * @param searchValue valor de busqueda usado para el Active Dierctory por ejemplo: username or email
     * @param searchBy ambito de busqueda del email, del username o id
     * @param searchBase valor de ambito base de busqueda por ejemplo: eg. DC=myorg,DC=com
     * @return search result resultado de busqueda del directorio activo
     * @throws NamingException
     */
    public NamingEnumeration<SearchResult> searchUser(String searchValue, String searchBy, String searchBase) throws NamingException {
    	String filter = getFilter(searchValue, searchBy);    	
    	String base = (null == searchBase) ? domainBase : this.getDomainBase(searchBase);
    	
		return this.dirContext.search(base, filter, this.searchCtls);
    }
    
    /**
     * Permite buscar todos los usuario en el dominio Active Directory
     * 
     * @param searchBase
     * @return NamingEnumeration<SearchResult>
     * @throws NamingException
     */
    public NamingEnumeration<SearchResult> searchAllUser(String searchBase) throws NamingException {
    	//String baseFilterAll = "(&(objectCategory=person)(objectClass=user))";
    	
    	//Definición de patron de busqueda, utilizando nombre de usuarios
    	String baseFilterAll = "(&(objectClass=user)(sAMAccountName=*))";
    	String base = (null == searchBase) ? domainBase : this.getDomainBase(searchBase);
    	
		return this.dirContext.search(base, baseFilterAll, this.searchCtls);
    }

    /**
     * closes the LDAP connection with Domain controller
     */
    public void closeLdapConnection(){
        try {
            if(dirContext != null)
                dirContext.close();
        }
        catch (NamingException e) {
        	LOG.severe(e.getMessage());            
        }
    }
    
    /**
     * Cadena de filtro de busqueda del dominio LDAP para usuario 
     * particular utilizando username o email
     * 
     * @param searchValue valor de busqueda username/email
     * @param searchBy selector de busque o por nombre de usuario o por email
     * @return filtro para Ldap.
     */
    private String getFilter(String searchValue, String searchBy) {
    	String filter = "(&((&(objectCategory=Person)(objectClass=User)))";  
    	
    	if(searchBy.equals("email")) {
    		filter += "(mail=" + searchValue + "))";
    	} else if(searchBy.equals("username")) {
    		filter += "(samaccountname=" + searchValue + "))";
    	}
		return filter;
	}
    
    /**
     * Creando valor base del dominio de busqueda a partir del dominio Ldap.
     * 
     * @param base nombre del dominio ldap
     * @return nombre base de busqueda por ejemplo DC=myorg,DC=com
     */
	private String getDomainBase(String base) {
		char[] namePair = base.toUpperCase().toCharArray();
		String dn = "";
		for (int i = 0; i < namePair.length; i++) {
			if (namePair[i] == '.') {
				dn += ",DC=" + namePair[++i];
			} else {
				dn += namePair[i];
			}
		}
		return dn;
	}

}
