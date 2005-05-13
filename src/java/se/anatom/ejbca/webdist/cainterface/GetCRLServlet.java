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
 
package se.anatom.ejbca.webdist.cainterface;

import java.io.IOException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import se.anatom.ejbca.ca.store.ICertificateStoreSessionLocal;
import se.anatom.ejbca.ca.store.ICertificateStoreSessionLocalHome;
import se.anatom.ejbca.log.Admin;
import se.anatom.ejbca.util.CertTools;
import se.anatom.ejbca.util.ServiceLocator;
import se.anatom.ejbca.webdist.ServletUtils;
import se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean;

/**
 * Servlet used to distribute  CRLs.<br>
 *
 * The servlet is called with method GET or POST and syntax
 * <code>command=&lt;command&gt;</code>.
 * <p>The follwing commands are supported:<br>
 * <ul>
 * <li>crl - gets the latest CRL.
 *
 * @version $Id: GetCRLServlet.java,v 1.24 2005-05-13 06:51:42 anatom Exp $
 * 
 * @web.servlet name = "GetCRL"
 *              display-name = "GetCRLServlet"
 *              description="Used to retrive CA certificate request and Processed CA Certificates from AdminWeb GUI"
 *              load-on-startup = "99"
 *
 * @web.servlet-mapping url-pattern = "/ca/getcrl/getcrl"
 *
 */
public class GetCRLServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(GetCRLServlet.class);

    private static final String COMMAND_PROPERTY_NAME = "cmd";
    private static final String COMMAND_CRL = "crl";
    private static final String ISSUER_PROPERTY = "issuer";

    private ICertificateStoreSessionLocalHome storehome = null;

    private synchronized ICertificateStoreSessionLocalHome getStoreHome() throws IOException {
        try{
            if(storehome == null){
              storehome = (ICertificateStoreSessionLocalHome)ServiceLocator.getInstance().getLocalHome(ICertificateStoreSessionLocalHome.COMP_NAME);
            }
          } catch(Exception e){
             throw new java.io.IOException("Authorization Denied");
          }
          return storehome;
    }
      

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
        log.debug(">doPost()");
        doGet(req, res);
        log.debug("<doPost()");
    } //doPost

    public void doGet(HttpServletRequest req,  HttpServletResponse res) throws java.io.IOException, ServletException {
        log.debug(">doGet()");

        // Check if authorized
        EjbcaWebBean ejbcawebbean= (se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean)
                                   req.getSession().getAttribute("ejbcawebbean");
        if ( ejbcawebbean == null ){
          try {
            ejbcawebbean = (se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean) java.beans.Beans.instantiate(this.getClass().getClassLoader(), "se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean");
           } catch (ClassNotFoundException exc) {
               throw new ServletException(exc.getMessage());
           }catch (Exception exc) {
               throw new ServletException (" Cannot create bean of class "+"se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean", exc);
           }
           req.getSession().setAttribute("ejbcawebbean", ejbcawebbean);
        }

        try{
          ejbcawebbean.initialize(req, "/ca_functionality/basic_functions");
        } catch(Exception e){
           throw new java.io.IOException("Authorization Denied");
        }

        String issuerdn = null; 
        if(req.getParameter(ISSUER_PROPERTY) != null){
          issuerdn = java.net.URLDecoder.decode(req.getParameter(ISSUER_PROPERTY),"UTF-8");
        }
        
        String command;
        // Keep this for logging.
        String remoteAddr = req.getRemoteAddr();
        command = req.getParameter(COMMAND_PROPERTY_NAME);
        if (command == null)
            command = "";
        if (command.equalsIgnoreCase(COMMAND_CRL) && issuerdn != null) {
            try {
                Admin admin = new Admin(((X509Certificate[]) req.getAttribute( "javax.servlet.request.X509Certificate" ))[0]);
                ICertificateStoreSessionLocal store = getStoreHome().create();
                byte[] crl = store.getLastCRL(admin, issuerdn);
                X509CRL x509crl = CertTools.getCRLfromByteArray(crl);
                String dn = CertTools.getIssuerDN(x509crl);
                String filename = CertTools.getPartFromDN(dn,"CN")+".crl";
                // We must remove cache headers for IE
                ServletUtils.removeCacheHeaders(res);
                res.setHeader("Content-disposition", "attachment; filename=" +  filename);
                res.setContentType("application/pkix-crl");
                res.setContentLength(crl.length);
                res.getOutputStream().write(crl);
                log.info("Sent latest CRL to client at " + remoteAddr);
            } catch (Exception e) {
                log.error("Error sending latest CRL to " + remoteAddr, e);
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Error getting latest CRL.");
                return;
            }
        }

    } // doGet

}
