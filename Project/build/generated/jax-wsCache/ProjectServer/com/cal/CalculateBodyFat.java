
package com.cal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calculateBodyFat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calculateBodyFat">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="age" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="weightKg" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="heightCm" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calculateBodyFat", propOrder = {
    "gender",
    "age",
    "weightKg",
    "heightCm"
})
public class CalculateBodyFat {

    protected String gender;
    protected int age;
    protected double weightKg;
    protected double heightCm;

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the age property.
     * 
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     */
    public void setAge(int value) {
        this.age = value;
    }

    /**
     * Gets the value of the weightKg property.
     * 
     */
    public double getWeightKg() {
        return weightKg;
    }

    /**
     * Sets the value of the weightKg property.
     * 
     */
    public void setWeightKg(double value) {
        this.weightKg = value;
    }

    /**
     * Gets the value of the heightCm property.
     * 
     */
    public double getHeightCm() {
        return heightCm;
    }

    /**
     * Sets the value of the heightCm property.
     * 
     */
    public void setHeightCm(double value) {
        this.heightCm = value;
    }

}
