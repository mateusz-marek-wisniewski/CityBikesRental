/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
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
@DiscriminatorColumn(name = "role_name", discriminatorType = DiscriminatorType.STRING)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountRole.findAll", query = "SELECT a FROM AccountRole a"),
    @NamedQuery(name = "AccountRole.findById", query = "SELECT a FROM AccountRole a WHERE a.id = :id"),
    @NamedQuery(name = "AccountRole.findByRoleName", query = "SELECT a FROM AccountRole a WHERE a.roleName = :roleName"),
    @NamedQuery(name = "AccountRole.findByActive", query = "SELECT a FROM AccountRole a WHERE a.active = :active")})
public class AccountRole implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="account_role_id_seq", sequenceName="account_role_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="account_role_id_seq")
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
    
    @Version
    private long version;
    
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
