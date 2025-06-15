
package com.cal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.cal package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CalculateBodyFat_QNAME = new QName("http://cal.org/", "calculateBodyFat");
    private final static QName _CalculateBodyFatResponse_QNAME = new QName("http://cal.org/", "calculateBodyFatResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.cal
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CalculateBodyFat }
     * 
     */
    public CalculateBodyFat createCalculateBodyFat() {
        return new CalculateBodyFat();
    }

    /**
     * Create an instance of {@link CalculateBodyFatResponse }
     * 
     */
    public CalculateBodyFatResponse createCalculateBodyFatResponse() {
        return new CalculateBodyFatResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateBodyFat }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cal.org/", name = "calculateBodyFat")
    public JAXBElement<CalculateBodyFat> createCalculateBodyFat(CalculateBodyFat value) {
        return new JAXBElement<CalculateBodyFat>(_CalculateBodyFat_QNAME, CalculateBodyFat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateBodyFatResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cal.org/", name = "calculateBodyFatResponse")
    public JAXBElement<CalculateBodyFatResponse> createCalculateBodyFatResponse(CalculateBodyFatResponse value) {
        return new JAXBElement<CalculateBodyFatResponse>(_CalculateBodyFatResponse_QNAME, CalculateBodyFatResponse.class, null, value);
    }

}
