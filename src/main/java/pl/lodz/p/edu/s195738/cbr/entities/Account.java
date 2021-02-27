/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import pl.lodz.p.edu.s195738.cbr.entities.roles.*;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email_verification_hash"}),
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"login"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
    @NamedQuery(name = "Account.findById", query = "SELECT a FROM Account a WHERE a.id = :id"),
    @NamedQuery(name = "Account.findByLogin", query = "SELECT a FROM Account a WHERE a.login = :login"),
    @NamedQuery(name = "Account.findByPassword", query = "SELECT a FROM Account a WHERE a.password = :password"),
    @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(name = "Account.findByEmailVerificationHash", query = "SELECT a FROM Account a WHERE a.emailVerificationHash = :emailVerificationHash"),
    @NamedQuery(name = "Account.findByConfirmed", query = "SELECT a FROM Account a WHERE a.confirmed = :confirmed"),
    @NamedQuery(name = "Account.findByActive", query = "SELECT a FROM Account a WHERE a.active = :active")})
@SecondaryTable(name = "personal_data", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id",referencedColumnName = "id"))
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="account_id_seq", sequenceName="account_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="account_id_seq")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    private String login;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(nullable = false, length = 64)
    private String password;
    
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Email
    @Size(min = 1, max = 64)
    @Column(nullable = false, length = 64)
    private String email;
    
    @Size(max = 64)
    @Column(name = "email_verification_hash", length = 64)
    private String emailVerificationHash;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean confirmed;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean active;
    
    @Version
    private long version;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private AdminRole adminRole;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private EmployeeRole employeeRole;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private CustomerRole customerRole;
    
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "account", fetch = FetchType.LAZY)
    private Collection<AccountPassword> accountPasswordCollection;
    
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "account", fetch = FetchType.LAZY)
    private Collection<LoginAttempt> loginAttemptCollection;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name", table = "personal_data")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "surname", table = "personal_data")
    private String surname;

    
    public Account() {
    }

    public Account(Long id) {
        this.id = id;
    }

    public Account(Long id, String login, String password, String email, boolean confirmed, boolean active, long version) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.confirmed = confirmed;
        this.active = active;
        this.version = version;
    }
    
    public LoginAttempt getLatestLoginAttemptBySuccess(boolean isSuccessful){
        if (loginAttemptCollection == null) return null;
        return loginAttemptCollection.stream()
                    .sorted((o1, o2) -> o2.getLoginDate().compareTo(o1.getLoginDate()))
                    .filter(loginAttempt -> loginAttempt.getSucceded() == isSuccessful)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
    }
    
    public boolean isPasswordInAccountPasswordCollection(String passwordHash) {
        return getAccountPasswordCollection().stream().anyMatch(accountPassword -> (accountPassword.getPassword().equals(passwordHash)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerificationHash() {
        return emailVerificationHash;
    }

    public void setEmailVerificationHash(String emailVerificationHash) {
        this.emailVerificationHash = emailVerificationHash;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
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

    @XmlTransient
    public Collection<AccountPassword> getAccountPasswordCollection() {
        return accountPasswordCollection;
    }

    public void setAccountPasswordCollection(Collection<AccountPassword> accountPasswordCollection) {
        this.accountPasswordCollection = accountPasswordCollection;
    }

    @XmlTransient
    public Collection<LoginAttempt> getLoginAttemptCollection() {
        return loginAttemptCollection;
    }

    public void setLoginAttemptCollection(Collection<LoginAttempt> loginAttemptCollection) {
        this.loginAttemptCollection = loginAttemptCollection;
    }

    @XmlTransient
    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }

    @XmlTransient
    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }

    @XmlTransient
    public CustomerRole getCustomerRole() {
        return customerRole;
    }

    public void setCustomerRole(CustomerRole customerRole) {
        this.customerRole = customerRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.Account[ id=" + id + " ]";
    }
    
}
