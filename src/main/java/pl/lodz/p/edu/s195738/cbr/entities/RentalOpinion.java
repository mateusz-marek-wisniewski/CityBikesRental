/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pl.lodz.p.edu.s195738.cbr.entities.roles.CustomerRole;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "rental_opinion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RentalOpinion.findAll", query = "SELECT r FROM RentalOpinion r"),
    @NamedQuery(name = "RentalOpinion.findById", query = "SELECT r FROM RentalOpinion r WHERE r.id = :id"),
    @NamedQuery(name = "RentalOpinion.findByContent", query = "SELECT r FROM RentalOpinion r WHERE r.content = :content")})
public class RentalOpinion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(nullable = false, length = 500)
    private String content;
    
    @Basic(optional = false)
    @NotNull
    @Max(5)
    @Min(1)
    @Column(nullable = false)
    private int rating;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "added_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;
    
    @Version
    private long version;
    
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private CustomerRole customerRole;
    

    public RentalOpinion() {
    }

    public RentalOpinion(Long id) {
        this.id = id;
    }

    public RentalOpinion(Long id, String content, long version) {
        this.id = id;
        this.content = content;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public CustomerRole getCustomerRole() {
        return customerRole;
    }

    public void setCustomerRole(CustomerRole customerRole) {
        this.customerRole = customerRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RentalOpinion)) {
            return false;
        }
        RentalOpinion other = (RentalOpinion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.RentalOpinion[ id=" + id + " ]";
    }
    
}
