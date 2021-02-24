/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.exceptions.mow;

/**
 *
 * @author Mateusz Wiśniewski
 */
public class BikeStationConcurrentEditException extends MOWException {

    public BikeStationConcurrentEditException(Throwable thrwbl) {
        super(thrwbl);
    }
}
