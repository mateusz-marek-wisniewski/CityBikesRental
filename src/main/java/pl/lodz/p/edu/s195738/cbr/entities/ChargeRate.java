/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "charge_rate", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"time_limit"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ChargeRate.findAll", query = "SELECT c FROM ChargeRate c"),
    @NamedQuery(name = "ChargeRate.findById", query = "SELECT c FROM ChargeRate c WHERE c.id = :id"),
    @NamedQuery(name = "ChargeRate.findByTimeLimit", query = "SELECT c FROM ChargeRate c WHERE c.timeLimit = :timeLimit"),
    @NamedQuery(name = "ChargeRate.findByChargeValue", query = "SELECT c FROM ChargeRate c WHERE c.chargeValue = :chargeValue")})
public class ChargeRate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="charge_rate_id_seq", sequenceName="charge_rate_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="charge_rate_id_seq")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "time_limit", nullable = false)
    private int timeLimit;
    
    @Min(0)
    @Basic(optional = false)
    @NotNull
    @Column(name = "charge_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal chargeValue;
    
    @Version
    private long version;

    
    public ChargeRate() {
    }

    public ChargeRate(Long id) {
        this.id = id;
    }

    public ChargeRate(Long id, int timeLimit, BigDecimal chargeValue, long version) {
        this.id = id;
        this.timeLimit = timeLimit;
        this.chargeValue = chargeValue;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        if (!(object instanceof ChargeRate)) {
            return false;
        }
        ChargeRate other = (ChargeRate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.ChargeRate[ id=" + id + " ]";
    }
    
}
