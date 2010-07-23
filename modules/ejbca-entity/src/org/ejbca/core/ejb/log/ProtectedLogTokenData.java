/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/

package org.ejbca.core.ejb.log;

import java.io.Serializable;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.ejbca.util.Base64;
import org.ejbca.util.CertTools;
import org.ejbca.util.GUIDGenerator;

/**
 * Representation of a ProtectedLogToken in the database.
 * 
 * @version $Id$
 */
@Entity
@Table(name="ProtectedLogTokenData")
public class ProtectedLogTokenData implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ProtectedLogTokenData.class);

	private String pk;
	private int tokenIdentifier;
	private int tokenType;
	private String b64TokenCertificate;
	private String tokenReference;

	/**
	 * Entity Bean holding data of a service configuration.
	 */
	public ProtectedLogTokenData(int tokenIdentifier, int tokenType, Certificate tokenCertificate, String tokenReference) {
	       setPk(GUIDGenerator.generateGUID(this));
	       setTokenIdentifier(tokenIdentifier);
	       setTokenType(tokenType);
	       setTokenCertificate(tokenCertificate);
	       setTokenReference(tokenReference);
	}

	public ProtectedLogTokenData() { }

	/** Primary Key. A 32 byte GUID generated by org.ejbca.util.GUIDGenerator. */
	@Id
	@Column(name="pk")
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

	@Column(name="tokenIdentifier", nullable=false)
    public int getTokenIdentifier() { return tokenIdentifier; }
    public void setTokenIdentifier(int tokenIdentifier) { this.tokenIdentifier = tokenIdentifier; }

	@Column(name="tokenType", nullable=false)
    public int getTokenType() { return tokenType; }
    public void setTokenType(int tokenType) { this.tokenType = tokenType; }
	
	// DB2: CLOB(100K) [100K (2GBw/o)], Derby: LONG VARCHAR [32,700 characters], Informix: TEXT (2147483648 b?), Ingres: , MSSQL: TEXT [2,147,483,647 bytes], MySQL: TEXT [65535 chars], Oracle: CLOB [4G chars], Sybase: TEXT [2,147,483,647 chars]  
    @Column(name="b64TokenCertificate", length=32700)
	@Lob
    public String getB64TokenCertificate() { return b64TokenCertificate; }
    public void setB64TokenCertificate(String b64TokenCertificate) { this.b64TokenCertificate = b64TokenCertificate; }

	// DB2: CLOB(100K) [100K (2GBw/o)], Derby: VARCHAR(32672) [32672 chars], Informix: TEXT (2147483648 b?), Ingres: , MSSQL: TEXT [2,147,483,647 bytes], MySQL: TEXT [65535 chars], Oracle: CLOB [4G chars], Sybase: TEXT [2,147,483,647 chars]  
	@Column(name="tokenReference", length=32672)
	@Lob
    public String getTokenReference() { return tokenReference; }
    public void setTokenReference(String tokenReference) { this.tokenReference = tokenReference; }

    /**
     * @return certificate
     */
    @Transient
    public Certificate getTokenCertificate() {
        try {
        	String b64TokenCertificate = getB64TokenCertificate();
        	if (b64TokenCertificate != null) {
            	return CertTools.getCertfromByteArray(Base64.decode(b64TokenCertificate.getBytes()));
        	} else {
        		return null;
        	}
        } catch (CertificateException ce) {
            log.error("Can't decode certificate.", ce);
        }
		return null;
    }

    /**
     * @param tokenCertificate certificate
     */
    public void setTokenCertificate(Certificate tokenCertificate) {
        try {
        	String b64Cert = null;
        	if (tokenCertificate != null) {
        		b64Cert = new String(Base64.encode(tokenCertificate.getEncoded(), false));
        	}
            setB64TokenCertificate(b64Cert);
        } catch (CertificateEncodingException cee) {
            log.error("Can't extract DER encoded certificate information.", cee);
        }
    }

    //
	// Search functions. 
	//

    /** @return the found entity instance or null if the entity does not exist */
    public static ProtectedLogTokenData findByPk(EntityManager entityManager, String pk) {
    	return entityManager.find(ProtectedLogTokenData.class,  pk);
    }

	/**
	 * @throws NonUniqueResultException if more than one entity with the name exists
	 * @return the found entity instance or null if the entity does not exist
	 */
    public static ProtectedLogTokenData findByTokenIdentifier(EntityManager entityManager, int tokenIdentifier) {
    	ProtectedLogTokenData ret = null;
    	try {
    		Query query = entityManager.createQuery("from ProtectedLogTokenData a WHERE a.tokenIdentifier=:tokenIdentifier");
    		query.setParameter("tokenIdentifier", tokenIdentifier);
    		ret = (ProtectedLogTokenData) query.getSingleResult();
    	} catch (NoResultException e) {
    	}
    	return ret;
    }    

	/** @return return the query results as a List. */
	public static Collection<ProtectedLogTokenData> findAll(EntityManager entityManager) {
		Query query = entityManager.createQuery("from ProtectedLogTokenData a");
		return query.getResultList();
	}
}
