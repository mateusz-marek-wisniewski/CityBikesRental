/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "bike_repair")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BikeRepair.findAll", query = "SELECT r FROM BikeRepair r"),
    @NamedQuery(name = "BikeRepair.findById", query = "SELECT r FROM BikeRepair r WHERE r.id = :id"),
    @NamedQuery(name = "BikeRepair.findByBikeRepairCost", query = "SELECT r FROM BikeRepair r WHERE r.repairCost = :repairCost"),
    @NamedQuery(name = "BikeRepair.findByBikeRepairLog", query = "SELECT r FROM BikeRepair r WHERE r.repairLog = :repairLog"),
    @NamedQuery(name = "BikeRepair.findByStartDate", query = "SELECT r FROM BikeRepair r WHERE r.startDate = :startDate"),
    @NamedQuery(name = "BikeRepair.findByEndDate", query = "SELECT r FROM BikeRepair r WHERE r.endDate = :endDate")})
public class BikeRepair implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="bike_repair_id_seq", sequenceName="bike_repair_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="bike_repair_id_seq")
    private Long id;
    
    @Min(0)
    @Column(name = "repair_cost", precision = 19, scale = 2)
    private BigDecimal repairCost;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "repair_log", nullable = false, length = 255)
    private String repairLog;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Version
    private long version;
    
    @JoinColumn(name = "bike_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Bike bike;
    

    public BikeRepair() {
    }

    public BikeRepair(Long id) {
        this.id = id;
    }

    public BikeRepair(Long id, String repairLog, Date startDate, long version) {
        this.id = id;
        this.repairLog = repairLog;
        this.startDate = startDate;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(BigDecimal repairCost) {
        this.repairCost = repairCost;
    }

    public String getRepairLog() {
        return repairLog;
    }

    public void setRepairLog(String repairLog) {
        this.repairLog = repairLog;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
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
        if (!(object instanceof BikeRepair)) {
            return false;
        }
        BikeRepair other = (BikeRepair) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.BikeRepair[ id=" + id + " ]";
    }
    
}
