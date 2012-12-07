package org.linagora.linshare.webservice.test.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-20T17:29:03.140+01:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://webservice.linshare.linagora.org/", name = "ShareSoapService")
@XmlSeeAlso({ObjectFactory.class})
public interface ShareSoapService {

    @RequestWrapper(localName = "sharedocument", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.Sharedocument")
    @WebMethod
    @ResponseWrapper(localName = "sharedocumentResponse", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.SharedocumentResponse")
    public void sharedocument(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        int arg2
    ) throws BusinessException_Exception;
}
