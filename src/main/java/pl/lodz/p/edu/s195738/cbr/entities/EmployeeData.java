/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siwy
 */
@Entity
@Table(name = "employee_data")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EmployeeData.findAll", query = "SELECT e FROM EmployeeData e"),
    @NamedQuery(name = "EmployeeData.findById", query = "SELECT e FROM EmployeeData e WHERE e.id = :id"),
    @NamedQuery(name = "EmployeeData.findByPhone", query = "SELECT e FROM EmployeeData e WHERE e.phone = :phone")})
public class EmployeeData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int phone;
    
    @Version
    private long version;
    
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private AccountRole accountRole;
    

    public EmployeeData() {
    }

    public EmployeeData(Long id) {
        this.id = id;
    }

    public EmployeeData(Long id, int phone, long version) {
        this.id = id;
        this.phone = phone;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        if (!(object instanceof EmployeeData)) {
            return false;
        }
        EmployeeData other = (EmployeeData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.EmployeeData[ id=" + id + " ]";
    }
    
}
