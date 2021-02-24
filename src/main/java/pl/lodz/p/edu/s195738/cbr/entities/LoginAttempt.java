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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "login_attempt")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginAttempt.findAll", query = "SELECT l FROM LoginAttempt l"),
    @NamedQuery(name = "LoginAttempt.findById", query = "SELECT l FROM LoginAttempt l WHERE l.id = :id"),
    @NamedQuery(name = "LoginAttempt.findBySucceded", query = "SELECT l FROM LoginAttempt l WHERE l.succeded = :succeded"),
    @NamedQuery(name = "LoginAttempt.findByLoginDate", query = "SELECT l FROM LoginAttempt l WHERE l.loginDate = :loginDate"),
    @NamedQuery(name = "LoginAttempt.findByIpAddress", query = "SELECT l FROM LoginAttempt l WHERE l.ipAddress = :ipAddress")})
public class LoginAttempt implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="login_attempt_id_seq", sequenceName="login_attempt_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="login_attempt_id_seq")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean succeded;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "login_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginDate;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;
    
    @Version
    private long version;
    
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    
    public LoginAttempt() {
    }

    public LoginAttempt(Long id) {
        this.id = id;
    }

    public LoginAttempt(Long id, boolean succeded, Date loginDate, String ipAddress, long version) {
        this.id = id;
        this.succeded = succeded;
        this.loginDate = loginDate;
        this.ipAddress = ipAddress;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getSucceded() {
        return succeded;
    }

    public void setSucceded(boolean succeded) {
        this.succeded = succeded;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        if (!(object instanceof LoginAttempt)) {
            return false;
        }
        LoginAttempt other = (LoginAttempt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.LoginAttempt[ id=" + id + " ]";
    }
    
}
