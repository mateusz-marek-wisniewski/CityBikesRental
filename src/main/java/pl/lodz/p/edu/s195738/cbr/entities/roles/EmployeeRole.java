/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities.roles;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pl.lodz.p.edu.s195738.cbr.entities.AccountRole;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
@SecondaryTable(name = "employee_data", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id",referencedColumnName = "id"))
public class EmployeeRole extends AccountRole {
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(nullable = false, table = "employee_data")
    private String phone;

    public EmployeeRole() {
        super.setRoleName("EMPLOYEE");
        super.setActive(true);
    }    
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
