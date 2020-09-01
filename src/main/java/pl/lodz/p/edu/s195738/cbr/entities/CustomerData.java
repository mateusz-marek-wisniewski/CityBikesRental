/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Siwy
 */
@Entity
@Table(name = "customer_data")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CustomerData.findAll", query = "SELECT c FROM CustomerData c"),
    @NamedQuery(name = "CustomerData.findById", query = "SELECT c FROM CustomerData c WHERE c.id = :id"),
    @NamedQuery(name = "CustomerData.findByTotalTime", query = "SELECT c FROM CustomerData c WHERE c.totalTime = :totalTime")})
public class CustomerData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_time", nullable = false)
    private long totalTime;
    
    @Version
    private long version;
    
    @OneToMany(mappedBy = "customerData", fetch = FetchType.LAZY)
    private Collection<Rent> rentCollection;
    
    @OneToMany(mappedBy = "customerData", fetch = FetchType.LAZY)
    private Collection<RentalOpinion> rentalOpinionCollection;
    
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private AccountRole accountRole;

    
    public CustomerData() {
    }

    public CustomerData(Long id) {
        this.id = id;
    }

    public CustomerData(Long id, long totalTime, long version) {
        this.id = id;
        this.totalTime = totalTime;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @XmlTransient
    public Collection<Rent> getRentCollection() {
        return rentCollection;
    }

    public void setRentCollection(Collection<Rent> rentCollection) {
        this.rentCollection = rentCollection;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
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
        if (!(object instanceof CustomerData)) {
            return false;
        }
        CustomerData other = (CustomerData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.CustomerData[ id=" + id + " ]";
    }
    
}
