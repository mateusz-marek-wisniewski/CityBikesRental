/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities.roles;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import pl.lodz.p.edu.s195738.cbr.entities.AccountRole;
import pl.lodz.p.edu.s195738.cbr.entities.CustomerData;

/**
 *
 * @author Siwy
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class CustomerRole extends AccountRole {
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "accountRole", fetch = FetchType.LAZY)
    private CustomerData customerData;

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
    }
}
