/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities.roles;

import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import pl.lodz.p.edu.s195738.cbr.entities.AccountRole;
import pl.lodz.p.edu.s195738.cbr.entities.Rent;
import pl.lodz.p.edu.s195738.cbr.entities.RentalOpinion;

/**
 *
 * @author Siwy
 */
@Entity
@DiscriminatorValue("CUSTOMER")
@SecondaryTable(name = "customer_data", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id",referencedColumnName = "id"))
public class CustomerRole extends AccountRole {
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_time", nullable = false, table = "customer_data")
    private long totalTime;

    @OneToMany(mappedBy = "customerRole", fetch = FetchType.LAZY)
    private Collection<Rent> rentCollection;
    
    @OneToMany(mappedBy = "customerRole", fetch = FetchType.LAZY)
    private Collection<RentalOpinion> rentalOpinionCollection;

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @XmlTransient
    public Collection<Rent> getRentCollection() {
        return rentCollection;
    }

    public void setRentCollection(Collection<Rent> rentCollection) {
        this.rentCollection = rentCollection;
    }

    @XmlTransient
    public Collection<RentalOpinion> getRentalOpinionCollection() {
        return rentalOpinionCollection;
    }

    public void setRentalOpinionCollection(Collection<RentalOpinion> rentalOpinionCollection) {
        this.rentalOpinionCollection = rentalOpinionCollection;
    }
}
