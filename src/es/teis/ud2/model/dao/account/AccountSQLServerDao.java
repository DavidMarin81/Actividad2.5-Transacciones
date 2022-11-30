/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.teis.ud2.model.dao.account;

import es.teis.ud2.data.DBCPDataSourceFactory;
import es.teis.ud2.exceptions.InstanceNotFoundException;
import es.teis.ud2.model.Account;
import es.teis.ud2.model.Empleado;
import es.teis.ud2.model.dao.AbstractGenericDao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.sql.DataSource;

/**
 *
 * @author David Marín
 */
public class AccountSQLServerDao extends AbstractGenericDao<Account> implements IAccountDao {

    private DataSource dataSource;

    public AccountSQLServerDao() {
        this.dataSource = DBCPDataSourceFactory.getDataSource();
    }

    @Override
    public Account create(Account entity) {

        try (
                 Connection conexion = this.dataSource.getConnection();  PreparedStatement pstmt = conexion.prepareStatement(
                "INSERT INTO [dbo].[ACCOUNT]\n"
                + "           ([EMPNO]\n"
                + "           ,[AMOUNT])\n"
                + "     VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS
        );) {

            pstmt.setInt(1, entity.getEmpleado().getEmpleadoId());
            pstmt.setBigDecimal(2, entity.getMontante());

            // Devolverá 0 para las sentencias SQL que no devuelven nada o el número de filas afectadas
            int result = pstmt.executeUpdate();

            ResultSet clavesResultado = pstmt.getGeneratedKeys();

            if (clavesResultado.next()) {
                int accountId = clavesResultado.getInt(1);
                entity.setAccountId(accountId);
            } else {
                entity = null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());
            entity = null;
        }
        return entity;
    }

    @Override
    public Account read(int id) throws InstanceNotFoundException {

        int accountNo;
        int empno;
        BigDecimal amount;
        int contador;
        Account cuenta = null;

        try (
                 Connection conexion = this.dataSource.getConnection();  PreparedStatement sentencia
                = conexion.prepareStatement("SELECT  [ACCOUNTNO]\n"
                        + "      ,[EMPNO]\n"
                        + "      ,[AMOUNT]\n"
                        + "  FROM [empresa].[dbo].[ACCOUNT]"
                        + "WHERE ACCOUNTNO=?");) {
            sentencia.setInt(1, id);

            ResultSet result = sentencia.executeQuery();
            if (result.next()) {
                contador = 0;

                accountNo = result.getInt(++contador);
                empno = result.getInt(++contador);
                amount = result.getBigDecimal(++contador);

                Empleado empleado = new Empleado();
                empleado.setEmpleadoId(empno);
                cuenta = new Account(accountNo, empleado, amount);

            } else {
                throw new InstanceNotFoundException(id, getEntityClass());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());

        }
        return cuenta;
    }

    @Override
    public boolean update(Account entity) {
        boolean actualizado = false;
        //no vamos a actualizar el empledo
        try (
                 Connection conexion = this.dataSource.getConnection();  PreparedStatement pstmt = conexion.prepareStatement(
                "UPDATE [dbo].[ACCOUNT]\n"
                + "   SET [AMOUNT] = ? \n"
                + " WHERE ACCOUNTNO = ?")) {

            pstmt.setBigDecimal(1, entity.getMontante());
            pstmt.setInt(2, entity.getAccountId());

            int result = pstmt.executeUpdate();
            actualizado = (result == 1);

            // Devolverá 0 para las sentencias SQL que no devuelven nada o el número de filas afectadas
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());

        }
        return actualizado;
    }

    @Override
    public boolean delete(int id) {
        int result = 0;
        try (
                 Connection conexion = this.dataSource.getConnection();  PreparedStatement pstmt = conexion.prepareStatement("DELETE FROM ACCOUNT WHERE ACCOUNTNO=?");) {

            pstmt.setInt(1, id);

            result = pstmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());

        }
        return (result == 1);
    }

    @Override
    public boolean transferir(int accIdOrigen, int accIdDestino, BigDecimal amount) {
        boolean transferencia = false;
        try (
                 Connection conexion = this.dataSource.getConnection();  PreparedStatement updateCuentaOrigen
                = conexion.prepareStatement("UPDATE [dbo].[ACCOUNT]\n"
                        + "   SET [AMOUNT] = [AMOUNT] - ?\n"
                        + " WHERE ACCOUNTNO = ?");  PreparedStatement updateCuentaDestino
                = conexion.prepareStatement("UPDATE [dbo].[ACCOUNT]\n"
                        + "   SET [AMOUNT] = [AMOUNT] + ?\n"
                        + " WHERE ACCOUNTNO = ?");  PreparedStatement crearMovimiento
                = conexion.prepareStatement("INSERT INTO [dbo].[ACC_MOVEMENT]\n"
                        + "           ([ACCOUNT_ORIGIN_ID]\n"
                        + "           ,[ACCOUNT_DEST_ID]\n"
                        + "           ,[AMOUNT]\n"
                        + "           ,[DATETIME])\n"
                        + "     VALUES\n"
                        + "           (?\n"
                        + "           ,?\n"
                        + "           ,?\n"
                        + "           ,?)");) {
            //Modificando la cantidad en la cuenta de origen
            updateCuentaOrigen.setBigDecimal(1, amount);
            System.out.println(amount.toString());
            updateCuentaOrigen.setInt(2, accIdOrigen);
            //Modificando la cantidad en la cuenta de destino
            updateCuentaDestino.setBigDecimal(1, amount);
            updateCuentaDestino.setInt(2, accIdDestino);

            int resultado = updateCuentaOrigen.executeUpdate();
            int resultado1 = updateCuentaDestino.executeUpdate();

            //Se crea el movimiento
            Date fecha = new Date(System.currentTimeMillis());
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formato.format(fecha);
            java.sql.Date date1 = java.sql.Date.valueOf(formattedDate);
            System.out.println(date1);

            crearMovimiento.setInt(1, accIdOrigen);
            crearMovimiento.setInt(2, accIdDestino);
            crearMovimiento.setBigDecimal(3, amount);
            crearMovimiento.setDate(4, date1);

            crearMovimiento.executeUpdate();

            System.out.println("Transferencia hecha con exito");

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Ha ocurrido una excepción: " + ex.getMessage());
        }
        return transferencia;
    }

}
