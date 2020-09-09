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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "account_password", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"account_id", "password"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountPassword.findAll", query = "SELECT a FROM AccountPassword a"),
    @NamedQuery(name = "AccountPassword.findById", query = "SELECT a FROM AccountPassword a WHERE a.id = :id"),
    @NamedQuery(name = "AccountPassword.findByPassword", query = "SELECT a FROM AccountPassword a WHERE a.password = :password")})
public class AccountPassword implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="account_password_id_seq", sequenceName="account_password_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="account_password_id_seq")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(nullable = false, length = 64)
    private String password;
    
    @Version
    private long version;
    
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    
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
