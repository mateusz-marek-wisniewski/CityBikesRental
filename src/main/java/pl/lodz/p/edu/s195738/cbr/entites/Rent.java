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
import javax.persistence.Version;
import javax.validation.constraints.Min;
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
    @NamedQuery(name = "Rent.findByCharge", query = "SELECT r FROM Rent r WHERE r.charge = :charge")})
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
    
    @Min(0)
    @Column(precision = 19, scale = 2)
    private BigDecimal charge;
    
    @Version
    private long version;
    
    @JoinColumn(name = "bike_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Bike bike;
    
    @JoinColumn(name = "start_station_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BikeStation rentStation;
    
    @JoinColumn(name = "end_station_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BikeStation returnStation;
    
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerData customerData;
    

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

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public BikeStation getRentStation() {
        return rentStation;
    }

    public void setRentStation(BikeStation rentStation) {
        this.rentStation = rentStation;
    }

    public BikeStation getReturnStation() {
        return returnStation;
    }

    public void setReturnStation(BikeStation returnStation) {
        this.returnStation = returnStation;
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
