/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBCTutorial;
import java.sql.*;
/**
 *
 * @author Kiner Shah
 */
public class BankingOperations {
    public void viewTransactions(String acNo) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankingbotdb", "root", "");
        Statement myStatement = con.createStatement();
        ResultSet rs = myStatement.executeQuery("select * from transactions where SenderAcNo=" + acNo + " or ReceiverAcNo=" + acNo);
        while(rs.next()) System.out.println(rs.getString("TransId") + " " + rs.getString("Balance"));
    }
    public static void main(String[] args) throws Exception {
        BankingOperations bo = new BankingOperations();
        bo.viewTransactions("11111111111");
    }
}
