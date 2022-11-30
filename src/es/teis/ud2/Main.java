/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.teis.ud2;

import es.teis.ud2.controller.DepartamentoController;
import es.teis.ud2.controller.EmpleadoController;
import es.teis.ud2.exceptions.InstanceNotFoundException;
import es.teis.ud2.model.Departamento;
import es.teis.ud2.model.Empleado;
import es.teis.ud2.model.dao.account.AccountSQLServerDao;
import es.teis.ud2.model.dao.departamento.DepartamentoSQLServerDao;
import es.teis.ud2.model.dao.departamento.IDepartamentoDao;
import es.teis.ud2.model.dao.empleado.EmpleadoSQLServerDao;
import es.teis.ud2.model.dao.empleado.IEmpleadoDao;
import es.teis.ud2.services.departamento.DepartamentoService;
import es.teis.ud2.services.departamento.IDepartamentoService;
import es.teis.ud2.services.empleado.EmpleadoService;
import es.teis.ud2.services.empleado.IEmpleadoServicio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author maria
 */
public class Main {

    public static void main(String[] args) {

        //mostrarDepartamentos();
        //probar a encontrar un departamento que no existe
        //     verDetalleDepartamento(666);
        //getDepartmentNamesByLoc("DALLAS");
        //createEmpleado();
        transferencia(1, 2, new BigDecimal(250));
    }
    
    private static boolean transferencia(int accIdOrigen, int accIdDestino, BigDecimal amount) {
        AccountSQLServerDao transfer = new AccountSQLServerDao();
        return transfer.transferir(accIdOrigen, accIdDestino, amount);
    }

    private static void mostrarDepartamentos() {
        IDepartamentoDao departamentoDao = new DepartamentoSQLServerDao();
        IDepartamentoService departamentoServicio = new DepartamentoService(departamentoDao);

        DepartamentoController controlador = new DepartamentoController(departamentoServicio);
        ArrayList<Departamento> departamentos = controlador.findAll();

        for (Departamento departamento : departamentos) {
            System.out.println("Departamento: " + departamento);

        }

    }

    private static void verDetalleDepartamento(int id) {

        IDepartamentoDao departamentoDao = new DepartamentoSQLServerDao();
        IDepartamentoService departamentoServicio = new DepartamentoService(departamentoDao);

        DepartamentoController controlador = new DepartamentoController(departamentoServicio);

        String mensaje = controlador.verDetalles(id);
        System.out.println(mensaje);

    }

    private static void getDepartmentNamesByLoc(String ubicacion) {

        IDepartamentoDao departamentoDao = new DepartamentoSQLServerDao();
        IDepartamentoService departamentoServicio = new DepartamentoService(departamentoDao);

        DepartamentoController controlador = new DepartamentoController(departamentoServicio);

        ArrayList<String> nombres = controlador.getDepartamentNamesByLoc(ubicacion);
        for (String nombre : nombres) {
            System.out.println("Nombre dept: " + nombre);
        }

    }

    private static void createEmpleado() {

        try {
            IDepartamentoDao departamentoDao = new DepartamentoSQLServerDao();
            Departamento departamento = departamentoDao.read(30);

            IEmpleadoDao empleadoDao = new EmpleadoSQLServerDao();
            Empleado jefe = empleadoDao.read(7499);

            Empleado empleado = new Empleado("Pepe", "Comercial", jefe, LocalDate.of(2022, 10, 1), new BigDecimal(30000), BigDecimal.ZERO, departamento);

            IEmpleadoServicio empleadoServicio = new EmpleadoService(empleadoDao);
            EmpleadoController empleadoController = new EmpleadoController(empleadoServicio);

            empleado = empleadoController.create(empleado);

            System.out.println("Se ha creado el empleado: " + empleado);

        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());
        }

    }

}
