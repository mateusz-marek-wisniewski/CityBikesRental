/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entites;

import java.io.Serializable;
import javax.persistence.Basic;
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
@Table(name = "account_password", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"account_id", "password"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountPassword.findAll", query = "SELECT a FROM AccountPassword a"),
    @NamedQuery(name = "AccountPassword.findById", query = "SELECT a FROM AccountPassword a WHERE a.id = :id"),
    @NamedQuery(name = "AccountPassword.findByPassword", query = "SELECT a FROM AccountPassword a WHERE a.password = :password"),
    @NamedQuery(name = "AccountPassword.findByVersion", query = "SELECT a FROM AccountPassword a WHERE a.version = :version")})
public class AccountPassword implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(nullable = false, length = 64)
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long version;
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account accountId;

    public AccountPassword() {
    }

    public AccountPassword(Long id) {
        this.id = id;
    }

    public AccountPassword(Long id, String password, long version) {
        this.id = id;
        this.password = password;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountPassword)) {
            return false;
        }
        AccountPassword other = (AccountPassword) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.AccountPassword[ id=" + id + " ]";
    }
    
}
