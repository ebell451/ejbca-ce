/*
 * Generated by XDoclet - Do not edit!
 */
package se.anatom.ejbca.keyrecovery;

/**
 * Local interface for KeyRecoverySession.
 */
public interface IKeyRecoverySessionLocal
   extends javax.ejb.EJBLocalObject
{
   /**
    * Adds a certificates keyrecovery data to the database.
    * @param admin the administrator calling the function
    * @param certificate the certificate used with the keypair.
    * @param username of the administrator
    * @param keypair the actual keypair to save.
    * @return false if the certificates keyrecovery data already exists.
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean addKeyRecoveryData( se.anatom.ejbca.log.Admin admin,java.security.cert.X509Certificate certificate,java.lang.String username,java.security.KeyPair keypair ) ;

   /**
    * Updates keyrecovery data
    * @param admin DOCUMENT ME!
    * @param certificate DOCUMENT ME!
    * @param markedasrecoverable DOCUMENT ME!
    * @param keypair DOCUMENT ME!
    * @return false if certificates keyrecovery data doesn't exists
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean changeKeyRecoveryData( se.anatom.ejbca.log.Admin admin,java.security.cert.X509Certificate certificate,boolean markedasrecoverable,java.security.KeyPair keypair ) ;

   /**
    * Removes a certificates keyrecovery data from the database.
    * @param admin the administrator calling the function
    * @param certificate the certificate used with the keys about to be removed.
    * @throws EJBException if a communication or other error occurs.
    */
   public void removeKeyRecoveryData( se.anatom.ejbca.log.Admin admin,java.security.cert.X509Certificate certificate ) ;

   /**
    * Removes a all keyrecovery data saved for a user from the database.
    * @param admin DOCUMENT ME!
    * @param username DOCUMENT ME!
    * @throws EJBException if a communication or other error occurs.
    */
   public void removeAllKeyRecoveryData( se.anatom.ejbca.log.Admin admin,java.lang.String username ) ;

   /**
    * Returns the keyrecovery data for a user. Observe only one certificates key can be recovered for every user at the time.
    * @param admin DOCUMENT ME!
    * @param username DOCUMENT ME!
    * @return the marked keyrecovery data or null if no recoverydata can be found.
    * @throws EJBException if a communication or other error occurs.
    */
   public se.anatom.ejbca.keyrecovery.KeyRecoveryData keyRecovery( se.anatom.ejbca.log.Admin admin,java.lang.String username ) ;

   /**
    * Marks a users newest certificate for key recovery. Newest means certificate with latest not before date.
    * @param admin the administrator calling the function
    * @param username or the user.
    * @return true if operation went successful or false if no certificates could be found for user, or user already marked.
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean markNewestAsRecoverable( se.anatom.ejbca.log.Admin admin,java.lang.String username ) ;

   /**
    * Marks a users certificate for key recovery.
    * @param admin the administrator calling the function
    * @param certificate the certificate used with the keys about to be removed.
    * @return true if operation went successful or false if certificate couldn't be found.
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean markAsRecoverable( se.anatom.ejbca.log.Admin admin,java.security.cert.X509Certificate certificate ) ;

   /**
    * Resets keyrecovery mark for a user,
    * @param admin DOCUMENT ME!
    * @param username DOCUMENT ME!
    * @throws EJBException if a communication or other error occurs.
    */
   public void unmarkUser( se.anatom.ejbca.log.Admin admin,java.lang.String username ) ;

   /**
    * Returns true if a user is marked for key recovery.
    * @param admin DOCUMENT ME!
    * @param username DOCUMENT ME!
    * @return true if user is already marked for key recovery.
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean isUserMarked( se.anatom.ejbca.log.Admin admin,java.lang.String username ) ;

   /**
    * Returns true if specified certificates keys exists in database.
    * @param admin the administrator calling the function
    * @param certificate the certificate used with the keys about to be removed.
    * @return true if user is already marked for key recovery.
    * @throws EJBException if a communication or other error occurs.
    */
   public boolean existsKeys( se.anatom.ejbca.log.Admin admin,java.security.cert.X509Certificate certificate ) ;

}
