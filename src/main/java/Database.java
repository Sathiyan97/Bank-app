//import java.sql.*;
//
//class App {
//    public static void main(String[] args) {
//        App app = new App();
//app.AccDetails(1);
//    }

//    int accNum5;
//    public String AccDetails(int acc) {
//      String details = null;
//        this.accNum5 = acc;
//
//        try {
//
//            String user = "postgres";
//            String password = "0000";
//            String url = "jdbc:postgresql://localhost:8001/sampleDB";
//            Connection conn = DriverManager.getConnection(url, user, password);
//
//            String getAmt = "select *\n" +
//                    "from \"bank\".customers\n" +
//                    "where  \"AccountNo\"= ?;";
//            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
//            preparedStmt1.setInt(1, accNum5);
//            ResultSet rs =preparedStmt1.executeQuery();
//
//            if (rs.next())
//            {
//                System.out.println("Name : " + rs.getString(1));
//                System.out.println("NIC : " + rs.getString(2));
//                System.out.println("Address :" + rs.getString(3));
//                System.out.println("AccNo :" + rs.getString(4));
//                System.out.println("Amount :" + rs.getString(5));
//                details = "Account Name : " + rs.getString(1) + "\n NIC Number : " +  rs.getString(2) + "\n Address : " + rs.getString(3) +
//                        "\n Account Number : " + rs.getString(4) + "\n Balance : " + rs.getString(5);
//            }
//            else
//            {
//                details = "No such user id is already registered";
//            }
//
//
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//}


///            String sql_query = "select *\n" +
//                    "from \"bank\".customers\n" +
//                    "where  \"AccountNo\"= 1;";
//
//
//
//            ResultSet rs = stmt.executeQuery(sql_query);
//
//            if (rs.next())
//            {
//                System.out.println("Name : " + rs.getString(1));
//                System.out.println("NIC : " + rs.getString(2));
//                System.out.println("Address :" + rs.getString(3));
//                System.out.println("AccNo :" + rs.getString(4));
//                System.out.println("Amount :" + rs.getString(5));
//            }
//            else
//            {
//                System.out.println("No such user id is already registered");
//            }