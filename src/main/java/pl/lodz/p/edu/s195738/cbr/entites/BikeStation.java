/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entites;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Siwy
 */
@Entity
@Table(name = "bike_station", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"identifier"}),
    @UniqueConstraint(columnNames = {"street_name", "geoloc_latitude", "geoloc_longitude"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BikeStation.findAll", query = "SELECT b FROM BikeStation b"),
    @NamedQuery(name = "BikeStation.findById", query = "SELECT b FROM BikeStation b WHERE b.id = :id"),
    @NamedQuery(name = "BikeStation.findByIdentifier", query = "SELECT b FROM BikeStation b WHERE b.identifier = :identifier"),
    @NamedQuery(name = "BikeStation.findByCity", query = "SELECT b FROM BikeStation b WHERE b.city = :city"),
    @NamedQuery(name = "BikeStation.findByStreetName", query = "SELECT b FROM BikeStation b WHERE b.streetName = :streetName"),
    @NamedQuery(name = "BikeStation.findByGeolocLatitude", query = "SELECT b FROM BikeStation b WHERE b.geolocLatitude = :geolocLatitude"),
    @NamedQuery(name = "BikeStation.findByGeolocLongitude", query = "SELECT b FROM BikeStation b WHERE b.geolocLongitude = :geolocLongitude")})
public class BikeStation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(nullable = false, length = 5)
    private String identifier;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String city;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "street_name", nullable = false, length = 100)
    private String streetName;
    
    @Basic(optional = false)
    @Min(-90)@Max(90)
    @NotNull
    @Column(name = "geoloc_latitude", nullable = false, precision = 10, scale = 6)
    private BigDecimal geolocLatitude;
    
    @Basic(optional = false)
    @Min(-180)@Max(180)
    @NotNull
    @Column(name = "geoloc_longitude", nullable = false, precision = 10, scale = 6)
    private BigDecimal geolocLongitude;
    
    @Version
    private long version;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rentStation", fetch = FetchType.LAZY)
    private Collection<Rent> rentCollection;
    
    @OneToMany(mappedBy = "returnStation", fetch = FetchType.LAZY)
    private Collection<Rent> returnCollection;
    
    @OneToMany(mappedBy = "bikeStation", fetch = FetchType.LAZY)
    private Collection<Bike> bikeCollection;

    
    public BikeStation() {
    }

    public BikeStation(Long id) {
        this.id = id;
    }

    public BikeStation(Long id, String identifier, String city, String streetName, BigDecimal geolocLatitude, BigDecimal geolocLongitude, long version) {
        this.id = id;
        this.identifier = identifier;
        this.city = city;
        this.streetName = streetName;
        this.geolocLatitude = geolocLatitude;
        this.geolocLongitude = geolocLongitude;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public BigDecimal getGeolocLatitude() {
        return geolocLatitude;
    }

    public void setGeolocLatitude(BigDecimal geolocLatitude) {
        this.geolocLatitude = geolocLatitude;
    }

    public BigDecimal getGeolocLongitude() {
        return geolocLongitude;
    }

    public void setGeolocLongitude(BigDecimal geolocLongitude) {
        this.geolocLongitude = geolocLongitude;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @XmlTransient
    public Collection<Rent> getRentCollection() {
        return rentCollection;
    }

    public void setRentCollection(Collection<Rent> rentCollection) {
        this.rentCollection = rentCollection;
    }

    @XmlTransient
    public Collection<Rent> getReturnCollection() {
        return returnCollection;
    }

    public void setReturnCollection(Collection<Rent> returnCollection) {
        this.returnCollection = returnCollection;
    }

    @XmlTransient
    public Collection<Bike> getBikeCollection() {
        return bikeCollection;
    }

    public void setBikeCollection(Collection<Bike> bikeCollection) {
        this.bikeCollection = bikeCollection;
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
        if (!(object instanceof BikeStation)) {
            return false;
        }
        BikeStation other = (BikeStation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.edu.s195738.cbr.entites.BikeStation[ id=" + id + " ]";
    }
    
}
