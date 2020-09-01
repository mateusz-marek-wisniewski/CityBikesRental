/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entites;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siwy
 */
@Entity
@Table(name = "account_role", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_name", "account_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountRole.findAll", query = "SELECT a FROM AccountRole a"),
    @NamedQuery(name = "AccountRole.findById", query = "SELECT a FROM AccountRole a WHERE a.id = :id"),
    @NamedQuery(name = "AccountRole.findByRoleName", query = "SELECT a FROM AccountRole a WHERE a.roleName = :roleName"),
    @NamedQuery(name = "AccountRole.findByActive", query = "SELECT a FROM AccountRole a WHERE a.active = :active"),
    @NamedQuery(name = "AccountRole.findByVersion", query = "SELECT a FROM AccountRole a WHERE a.version = :version")})
public class AccountRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "role_name", nullable = false, length = 16)
    private String roleName;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long version;
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account accountId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "accountRole", fetch = FetchType.LAZY)
    private EmployeeData employeeData;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "accountRole", fetch = FetchType.LAZY)
    private CustomerData customerData;

    public AccountRole() {
    }

    public AccountRole(Long id) {
        this.id = id;
    }

    public AccountRole(Long id, String roleName, boolean active, long version) {
        this.id = id;
        this.roleName = roleName;
        this.active = active;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Account getAccountId() {
        return accountId;
    }

    public void setAccountId(Account accountId) {
        this.accountId = accountId;
    }

    public EmployeeData getEmployeeData() {
        return employeeData;
    }

    public void setEmployeeData(EmployeeData employeeData) {
        this.employeeData = employeeData;
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
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
        if (!(object instanceof AccountRole)) {
            return false;
        }
        AccountRole other = (AccountRole) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.AccountRole[ id=" + id + " ]";
    }
    
}
