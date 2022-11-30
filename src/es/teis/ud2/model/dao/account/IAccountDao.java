/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package es.teis.ud2.model.dao.account;

import java.math.BigDecimal;

/**
 *
 * @author David Marín
 */
public interface IAccountDao {
    public boolean transferir(int accIdOrigen, int accIdDestino, BigDecimal amount);
}
