/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.teis.ud2.model;

import java.math.BigDecimal;

/**
 *
 * @author David Marín
 */
public class Account {
    private int accountId;
    private Empleado empleado;
    private BigDecimal montante;

    public Account(int accountId, Empleado empleado, BigDecimal montante) {
        this.accountId = accountId;
        this.empleado = empleado;
        this.montante = montante;
    }

    public Account() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public BigDecimal getMontante() {
        return montante;
    }

    public void setMontante(BigDecimal montante) {
        this.montante = montante;
    }
    
}
