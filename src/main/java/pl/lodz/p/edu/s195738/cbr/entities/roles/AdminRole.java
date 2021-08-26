/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.entities.roles;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import pl.lodz.p.edu.s195738.cbr.entities.Account;
import pl.lodz.p.edu.s195738.cbr.entities.AccountRole;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@DiscriminatorValue("ADMIN")
public class AdminRole extends AccountRole {

    public AdminRole() {
        super.setRoleName("ADMIN");
        super.setActive(true);
    }
}
