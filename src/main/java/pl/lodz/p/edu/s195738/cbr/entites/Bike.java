/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entites;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Siwy
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"identifier"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bike.findAll", query = "SELECT b FROM Bike b"),
    @NamedQuery(name = "Bike.findById", query = "SELECT b FROM Bike b WHERE b.id = :id"),
    @NamedQuery(name = "Bike.findByIdentifier", query = "SELECT b FROM Bike b WHERE b.identifier = :identifier"),
    @NamedQuery(name = "Bike.findByBikeStatus", query = "SELECT b FROM Bike b WHERE b.bikeStatus = :bikeStatus"),
    @NamedQuery(name = "Bike.findByDamageDesc", query = "SELECT b FROM Bike b WHERE b.damageDesc = :damageDesc")})
public class Bike implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name="bike_id_seq", sequenceName="bike_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="bike_id_seq")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int identifier;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "bike_status", nullable = false, length = 10)
    private String bikeStatus;
    
    @Size(max = 255)
    @Column(name = "damage_desc", length = 255)
    private String damageDesc;
    
    @Version
    private long version;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bike", fetch = FetchType.LAZY)
    private Collection<Repair> repairCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bike", fetch = FetchType.LAZY)
    private Collection<Rent> rentCollection;
    
    @JoinColumn(name = "bike_station_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BikeStation bikeStation;

    
    public Bike() {
    }

    public Bike(Long id) {
        this.id = id;
    }

    public Bike(Long id, int identifier, String bikeStatus, long version) {
        this.id = id;
        this.identifier = identifier;
        this.bikeStatus = bikeStatus;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getBikeStatus() {
        return bikeStatus;
    }

    public void setBikeStatus(String bikeStatus) {
        this.bikeStatus = bikeStatus;
    }

    public String getDamageDesc() {
        return damageDesc;
    }

    public void setDamageDesc(String damageDesc) {
        this.damageDesc = damageDesc;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @XmlTransient
    public Collection<Repair> getRepairCollection() {
        return repairCollection;
    }

    public void setRepairCollection(Collection<Repair> repairCollection) {
        this.repairCollection = repairCollection;
    }

    @XmlTransient
    public Collection<Rent> getRentCollection() {
        return rentCollection;
    }

    public void setRentCollection(Collection<Rent> rentCollection) {
        this.rentCollection = rentCollection;
    }

    public BikeStation getBikeStation() {
        return bikeStation;
    }

    public void setBikeStation(BikeStation bikeStation) {
        this.bikeStation = bikeStation;
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
        if (!(object instanceof Bike)) {
            return false;
        }
        Bike other = (Bike) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.Bike[ id=" + id + " ]";
    }
    
}
