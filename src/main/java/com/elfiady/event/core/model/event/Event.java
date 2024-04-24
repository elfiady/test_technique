package com.elfiady.event.core.model.event;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "event")
public class Event implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected long id;
	protected String name;
	@XmlElement(required = true)
	protected String description;

	/**
	 * Obtient la valeur de la propriété id.
	 *
	 */
	public long getId() {
		return id;
	}

	/**
	 * Définit la valeur de la propriété id.
	 *
	 */
	public void setId(final long value) {
		this.id = value;
	}

	/**
	 * Obtient la valeur de la propriété name.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Définit la valeur de la propriété name.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setName(final String value) {
		this.name = value;
	}

	/**
	 * Obtient la valeur de la propriété description.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Définit la valeur de la propriété description.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	public void setDescription(final String value) {
		this.description = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Event [id=" + id + ", name=" + name + ", description=" + description + ", getId()=" + getId()
		+ ", getName()=" + getName() + ", getDescription()=" + getDescription() + ", getClass()=" + getClass()
		+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
