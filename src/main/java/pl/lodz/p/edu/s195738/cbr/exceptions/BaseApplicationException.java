/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Mateusz Wiśniewski
 */
@ApplicationException(rollback = true)
public class BaseApplicationException extends Exception {

    public BaseApplicationException(Throwable thrwbl) {
        super(thrwbl);
    }
}
