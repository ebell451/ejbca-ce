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
package org.cesecore.certificates.ocsp.standalone;

import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.Map;

import javax.ejb.Remote;

import org.bouncycastle.cert.ocsp.OCSPException;
import org.cesecore.certificates.ocsp.cache.CryptoTokenAndChain;

/**
 * @version $Id$
 *
 */
@Remote
public interface StandaloneOcspResponseGeneratorTestSessionRemote {

    /**
     * Replaces the contents of the cache with the parameter given
     * 
     * @param newCache
     * @throws CertificateEncodingException
     * @throws OCSPException
     */
    void replaceTokenAndChainCache(Map<Integer, CryptoTokenAndChain> newCache) throws CertificateEncodingException, OCSPException;
 
    Collection<CryptoTokenAndChain> getCacheValues();
}
