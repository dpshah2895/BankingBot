/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBCTutorial;

/**
 *
 * @author Kiner Shah
 */
import java.sql.*;
public class JDBCFirst {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/kdadmin", "root", "");
            Statement myStatement = con.createStatement();
            ResultSet rs = myStatement.executeQuery("select * from usersinfo");
            while(rs.next()) {
                System.out.println(rs.getString("Fname") + " " + rs.getString("Lname"));
            }
            /*Statement myStatement1 = con.createStatement();
            ResultSet rs1 = myStatement1.executeQuery("select * from accountholder where FName='Kiner'");
            while(rs1.next()) {
                System.out.println(rs1.getString("FName") + " " + rs1.getString("LName") + " " + rs1.getString("Email"));
            }*/
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
