/*
 * UserView.java
 *
 * Created on den 17 april 2002, 23:48
 */

package se.anatom.ejbca.webdist.rainterface;

import org.ietf.ldap.LDAPDN;

import se.anatom.ejbca.SecConst;
import se.anatom.ejbca.ra.UserAdminData;
import se.anatom.ejbca.ra.UserDataRemote;
import se.anatom.ejbca.ra.raadmin.DNFieldExtractor;
import java.util.Date;

/**
 * A class representing an user in the ra user database.
 *
 * @author  Philip Vendil
 */
public class UserView implements java.io.Serializable, Cloneable, Comparable {
    // Public constants.
    public static final int USERNAME          = 0;
    public static final int PASSWORD          = 1;
    public static final int CLEARTEXTPASSWORD = 2;
    public static final int COMMONNAME        = 3;
    public static final int ORGANIZATIONUNIT  = 4;
    public static final int ORGANIZATION      = 5;
    public static final int LOCALE            = 6;
    public static final int STATE             = 7;
    public static final int COUNTRY           = 8;
    public static final int EMAIL             = 9;
    public static final int STATUS            = 10;
    public static final int PROFILE           = 11;
    public static final int CERTIFICATETYPE   = 12;
    public static final int TYPE_MASK         = 13; // Used with database representation.
    public static final int TYPE_INVALID      = 13;
    public static final int TYPE_ENDUSER      = 14;
    public static final int TYPE_CA           = 15;
    public static final int TYPE_RA           = 16;
    public static final int TYPE_ROOTCA       = 17;
    public static final int TYPE_CAADMIN      = 18;
    public static final int TYPE_RAADMIN      = 19;
    public static final int USERDN            = 20;
    
    public static final String TRUE = "T";
    public static final String FALSE = "F";
    
    public static final int NUMBEROF_USERFIELDS=21;
    public static final int NUMBEROF_USERFIELDS_WITHTYPEMASK=14;  
    
    /** Creates a new instance of UserView */
    public UserView() {
      userdata = new String[NUMBEROF_USERFIELDS];   
      for(int i=0; i<  NUMBEROF_USERFIELDS ; i++){
        userdata[i] = "";    
      }
    }
    
    public UserView(UserAdminData newuserdata){
      userdata = new String[NUMBEROF_USERFIELDS];
      for(int i=0; i<  NUMBEROF_USERFIELDS ; i++){
        userdata[i] = "";    
      }      
      setValues(newuserdata);
      this.timecreated= newuserdata.getTimeCreated();
      this.timemodified= newuserdata.getTimeModified();     
    }
    
    public UserView(String[] newuserdata, Date timecreated, Date timemodified){
       userdata = new String[NUMBEROF_USERFIELDS]; 
       setValues(newuserdata);     
       this.timecreated= timecreated;
       this.timemodified= timemodified;
    }
    
    // Public methods.
    /** Method that returns the specific userdata pointed by the parameter, should be on of the constants except TYPE_MASK. */
    public String getValue(int parameter){
      return userdata[parameter];  
    }
    
    /** Metod that sets the value of a specific userdata */
    public void setValue(int parameter, String value){
      userdata[parameter]= new String(value);
    }
    
    /** Method that returns the userdata as a String array */
    public String[] getValues(){
      return userdata;   
    }
    
    /** Method that sets all the values in the userdata. */
    public void setValues(String[] values){
      System.arraycopy(values,0,userdata,0,NUMBEROF_USERFIELDS);  
      userdata[STATUS] = Integer.toString(UserDataRemote.STATUS_NEW);
    }
    
    /** Method to convert the user data model betwwen the one used in web interface and the one used internal*/
    public UserAdminData convertToUserAdminData() throws NumberFormatException{
      String dn = "";
      boolean first = true;
      if(userdata[COMMONNAME]!= null){
        if(!userdata[COMMONNAME].trim().equals("")){  
          dn = dn + LDAPDN.escapeRDN("CN="+ userdata[COMMONNAME]);     
          first=false;
        }  
      }
      if(userdata[ORGANIZATIONUNIT]!= null){ 
        if(!userdata[ORGANIZATIONUNIT].trim().equals("")){
          if(!first)
            dn = dn + ", ";     
          dn = dn + LDAPDN.escapeRDN("OU="+ userdata[ORGANIZATIONUNIT]);
          first = false;
        }  
      }
      if(userdata[ORGANIZATION]!= null){
        if(!userdata[ORGANIZATION].trim().equals("")){
          if(!first)
            dn = dn + ", ";                 
          dn = dn  + LDAPDN.escapeRDN("O="+ userdata[ORGANIZATION]); 
          first = false;
        }  
      }      
      if(userdata[LOCALE]!= null){
        if(!userdata[LOCALE].trim().equals("")){
          if(!first)
            dn = dn + ", ";                
          dn = dn + LDAPDN.escapeRDN("L="+ userdata[LOCALE]);    
          first = false;          
        }  
      } 
      if(userdata[STATE]!= null){
        if(!userdata[STATE].trim().equals("")){
          if(!first)
            dn = dn + ", ";                 
          dn = dn + LDAPDN.escapeRDN("ST="+ userdata[STATE]);   
          first = false;           
        }
      }
      if(userdata[COUNTRY]!= null){
        if(!userdata[COUNTRY].trim().equals("")){
          if(!first)
            dn = dn + ", ";                 
          dn = dn + LDAPDN.escapeRDN("C="+ userdata[COUNTRY].toUpperCase());     
          first = false;           
        }  
      } 
      if(dn.length()>0){
        if(dn.charAt(0) == ','){
          dn=dn.substring(1);
        }
      }
      dn=LDAPDN.normalize(dn);  
      
      UserAdminData returnuser = new UserAdminData(userdata[USERNAME],dn,userdata[EMAIL], 
                                                   Integer.parseInt(userdata[STATUS]), getHexType(), Integer.parseInt(userdata[PROFILE]),
                                                   Integer.parseInt(userdata[CERTIFICATETYPE]), timecreated, timemodified);
      returnuser.setPassword(userdata[PASSWORD]);
        
      return returnuser;
    }
    
    /* Sets the values according to the values in the UserAdminData object.*/ 
    public void setValues(UserAdminData newuserdata){
       int startindex, endindex =0;
       String userdn= newuserdata.getDN(); 
       
       userdata[USERNAME] = newuserdata.getUsername();
       userdata[PASSWORD] = newuserdata.getPassword();
       if(userdata[PASSWORD] != null){
         userdata[CLEARTEXTPASSWORD]= UserView.TRUE;   
       }else{
         userdata[CLEARTEXTPASSWORD]= UserView.FALSE;      
       }
       
       userdata[EMAIL]    = newuserdata.getEmail();
       userdata[STATUS]   = Integer.toString(newuserdata.getStatus());
       
       setHexType(newuserdata.getType());
       
       DNFieldExtractor dn = new DNFieldExtractor(userdn); 
       // Decompose dn.
       userdata[COMMONNAME]         = dn.getField(DNFieldExtractor.COMMONNAME);
       userdata[ORGANIZATIONUNIT]   = dn.getField(DNFieldExtractor.ORGANIZATIONUNIT);
       userdata[ORGANIZATION]       = dn.getField(DNFieldExtractor.ORGANIZATION);
       userdata[LOCALE]             = dn.getField(DNFieldExtractor.LOCALE);
       userdata[STATE]              = dn.getField(DNFieldExtractor.STATE);
       userdata[COUNTRY]            = dn.getField(DNFieldExtractor.COUNTRY);       
       userdata[USERDN]             = userdn; 
       userdata[PROFILE]            = Integer.toString(newuserdata.getProfileId());
       userdata[CERTIFICATETYPE]    = Integer.toString(newuserdata.getCertificateTypeId());
       
    }
    
    /** Converts the representation of userdata type into a int hex value. */ 
    public int getHexType(){
      int returnval=0;
      if(this.userdata[TYPE_ENDUSER] != null && this.userdata[TYPE_ENDUSER].equals(TRUE))
        returnval += SecConst.USER_ENDUSER;
      if(this.userdata[TYPE_CA] != null && this.userdata[TYPE_CA].equals(TRUE))               
        returnval += SecConst.USER_CA;
      if(this.userdata[TYPE_RA] != null && this.userdata[TYPE_RA].equals(TRUE))               
        returnval += SecConst.USER_RA;       
      if(this.userdata[TYPE_ROOTCA] != null && this.userdata[TYPE_ROOTCA].equals(TRUE))               
        returnval += SecConst.USER_ROOTCA;
      if(this.userdata[TYPE_CAADMIN] != null && this.userdata[TYPE_CAADMIN].equals(TRUE))               
        returnval += SecConst.USER_CAADMIN;
      if(this.userdata[TYPE_RAADMIN] != null && this.userdata[TYPE_RAADMIN].equals(TRUE))               
        returnval += SecConst.USER_RAADMIN;       
      
      return returnval;
    }
    
    /** Converts the hex representation of userdata type into the internal representation */
    public void setHexType(int hexval){
      if(hexval == SecConst.USER_INVALID ){
        // Invalid   
          userdata[TYPE_INVALID]=TRUE;
      }
      if((hexval & SecConst.USER_ENDUSER) > 0){
         // End user
          userdata[TYPE_ENDUSER]=TRUE;
      }     
      if((hexval & SecConst.USER_CA) > 0){
         // CA
          userdata[TYPE_CA]=TRUE;
      }
      if((hexval & SecConst.USER_RA) > 0){
         // RA
          userdata[TYPE_RA]=TRUE;
      }
      if((hexval & SecConst.USER_ROOTCA) > 0){
         // RootCA
          userdata[TYPE_ROOTCA]=TRUE;
      }      
      if((hexval & SecConst.USER_CAADMIN) > 0){
         // CA Admin
          userdata[TYPE_CAADMIN]=TRUE;
      }      
      if((hexval & SecConst.USER_RAADMIN) > 0 ){
         // RA Admin
          userdata[TYPE_RAADMIN]=TRUE;
      }
    }
    
    public int compareTo(Object obj) {
      int returnvalue = -1;
      int sortby = this.sortby.getSortBy();
      switch(sortby){
          case SortBy.USERNAME : 
            returnvalue = userdata[USERNAME].compareTo(((UserView) obj).getValue(USERNAME));
            break;  
          case SortBy.PASSWORD : 
            returnvalue = userdata[PASSWORD].compareTo(((UserView) obj).getValue(PASSWORD));            
            break;  
          case SortBy.COMMONNAME : 
            returnvalue = userdata[COMMONNAME].compareTo(((UserView) obj).getValue(COMMONNAME));            
            break;  
          case SortBy.ORGANIZATIONUNIT : 
            returnvalue = userdata[ORGANIZATIONUNIT].compareTo(((UserView) obj).getValue(ORGANIZATIONUNIT));            
            break;  
          case SortBy.ORGANIZATION : 
            returnvalue = userdata[ORGANIZATION].compareTo(((UserView) obj).getValue(ORGANIZATION));            
            break;  
          case SortBy.LOCALE : 
            returnvalue = userdata[LOCALE].compareTo(((UserView) obj).getValue(LOCALE));            
            break;  
          case SortBy.STATE : 
            returnvalue = userdata[STATE].compareTo(((UserView) obj).getValue(STATE));            
            break;  
          case SortBy.COUNTRY : 
            returnvalue = userdata[COUNTRY].compareTo(((UserView) obj).getValue(COUNTRY));            
            break;           
          case SortBy.EMAIL : 
            returnvalue = userdata[EMAIL].compareTo(((UserView) obj).getValue(EMAIL));            
            break;   
          case SortBy.STATUS : 
            returnvalue = userdata[STATUS].compareTo(((UserView) obj).getValue(STATUS));            
            break;
          case SortBy.PROFILE : 
            returnvalue = userdata[PROFILE].compareTo(((UserView) obj).getValue(PROFILE));            
            break;
          case SortBy.CERTIFICATETYPE : 
            returnvalue = userdata[CERTIFICATETYPE].compareTo(((UserView) obj).getValue(CERTIFICATETYPE));            
            break;
          case SortBy.TIMECREATED : 
            returnvalue = timecreated.compareTo(((UserView) obj).getTimeCreated());            
            break;
          case SortBy.TIMEMODIFIED : 
            returnvalue = timemodified.compareTo(((UserView) obj).getTimeModified());            
            break;            
          default:
            returnvalue = userdata[USERNAME].compareTo(((UserView) obj).getValue(USERNAME));             
      }
      if(this.sortby.getSortOrder() == SortBy.DECENDING)
        returnvalue = 0-returnvalue;   
          
      return returnvalue;  
    }
    
    public void setSortBy(SortBy sortby){
      this.sortby=sortby;   
    }
    
    public Date getTimeCreated(){return timecreated;}
    public Date getTimeModified(){return timemodified;}  
    public int getProfileId(){return Integer.parseInt(userdata[PROFILE]);}
    public int getCertificateTypeId(){return Integer.parseInt(userdata[CERTIFICATETYPE]);}
    // Private constants.  
    
    // Private methods.
    private String[] userdata; 
    private SortBy sortby; 
    private Date timecreated;
    private Date timemodified;
}
