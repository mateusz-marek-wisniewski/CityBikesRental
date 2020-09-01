/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entites;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siwy
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rent.findAll", query = "SELECT r FROM Rent r"),
    @NamedQuery(name = "Rent.findById", query = "SELECT r FROM Rent r WHERE r.id = :id"),
    @NamedQuery(name = "Rent.findByStartDate", query = "SELECT r FROM Rent r WHERE r.startDate = :startDate"),
    @NamedQuery(name = "Rent.findByEndDate", query = "SELECT r FROM Rent r WHERE r.endDate = :endDate"),
    @NamedQuery(name = "Rent.findByCharge", query = "SELECT r FROM Rent r WHERE r.charge = :charge"),
    @NamedQuery(name = "Rent.findByVersion", query = "SELECT r FROM Rent r WHERE r.version = :version")})
public class Rent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 19, scale = 2)
    private BigDecimal charge;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long version;
    @JoinColumn(name = "bike_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Bike bikeId;
    @JoinColumn(name = "start_station_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BikeStation startStationId;
    @JoinColumn(name = "end_station_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BikeStation endStationId;
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerData customerId;

    public Rent() {
    }

    public Rent(Long id) {
        this.id = id;
    }

    public Rent(Long id, Date startDate, long version) {
        this.id = id;
        this.startDate = startDate;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Bike getBikeId() {
        return bikeId;
    }

    public void setBikeId(Bike bikeId) {
        this.bikeId = bikeId;
    }

    public BikeStation getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(BikeStation startStationId) {
        this.startStationId = startStationId;
    }

    public BikeStation getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(BikeStation endStationId) {
        this.endStationId = endStationId;
    }

    public CustomerData getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerData customerId) {
        this.customerId = customerId;
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
        if (!(object instanceof Rent)) {
            return false;
        }
        Rent other = (Rent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.Rent[ id=" + id + " ]";
    }
    
}
