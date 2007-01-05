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

package org.ejbca.core.protocol.xkms.generators;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import org.ejbca.core.protocol.xkms.common.XKMSConstants;
import org.w3._2002._03.xkms_.LocateRequestType;
import org.w3._2002._03.xkms_.LocateResultType;
import org.w3._2002._03.xkms_.UnverifiedKeyBindingType;

/**
 * Class generating a response for a locate call
 * 
 * 
 * @author Philip Vendil 2006 sep 27
 *
 * @version $Id: LocateResponseGenerator.java,v 1.2 2007-01-05 05:32:51 herrvendil Exp $
 */

public class LocateResponseGenerator extends
		KISSResponseGenerator {
	

	public LocateResponseGenerator(String remoteIP, LocateRequestType req) {
		super(remoteIP, req);
	}
	
	/**
	 * Returns a locate response
	 */
	public LocateResultType getResponse(boolean requestVerifies){
		LocateResultType result = xkmsFactory.createLocateResultType();		
		super.populateResponse(result, requestVerifies);		
		LocateRequestType req = (LocateRequestType) this.req;
		

		if(resultMajor == null){ 		
			if(!checkValidRespondWithRequest(req.getRespondWith())){
				resultMajor = XKMSConstants.RESULTMAJOR_SENDER;
				resultMinor = XKMSConstants.RESULTMINOR_MESSAGENOTSUPPORTED;
			}

			if(resultMajor == null){ 
				List<X509Certificate> queryResult = processRequest(req.getQueryKeyBinding());

				if(resultMajor == null){ 		
					Iterator<X509Certificate> iter = queryResult.iterator();
					while(iter.hasNext()){
						X509Certificate nextCert = iter.next();
						result.getUnverifiedKeyBinding().add((UnverifiedKeyBindingType) getResponseValues(req.getQueryKeyBinding(),nextCert,false,false));

					}		  
				}
			}
		}
		
		if(resultMajor == null){ 
			resultMajor = XKMSConstants.RESULTMAJOR_SUCCESS;
		}
		  		   
		setResult(result);
		

		
		
		return result;
	}
	

    
    


}
