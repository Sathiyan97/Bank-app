package main.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This program is a very simple Web server. When it receives a HTTP request it
 * sends the request back as the reply. This can be of interest when you want to
 * see just what a Web client is requesting, or what data is being sent when a
 * form is submitted, for example.
 */
class HttpMirror {


    int accNum5;
    String name;
    String nic;
    String address;
    int accNum;
    double amt;
    int accNum1;
    double amt1;
    int fromaccNum;
    int toaccNum;
    double amt2;

    public static void main(String[] args) throws IOException {

        HttpMirror obj = new HttpMirror();
        final String indexFilePath = "/Users/sathiyan/Documents/Bank-app/src/main/java/main/java/web/web.html";


        ServerSocket serverSocket = new ServerSocket(8000);


        while (true) {
            final Socket socket = serverSocket.accept();


            new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        while (!socket.isClosed()) {


                            try {

                                String urlPathAndMethod = reader.readLine();

                                String[] pathArgs = urlPathAndMethod.split(" ");

                                String method = pathArgs[0];
                                String path = pathArgs[1];

                                Map<String, String> headers = new HashMap<>();

                                boolean headersDone = false;
                                while (!headersDone) {
                                    String headerEntry = reader.readLine();

                                    if (headerEntry.equals("")) {
                                        headersDone = true;
                                    } else {
                                        String[] headerArgs = headerEntry.split(":");

                                        headers.put(headerArgs[0].trim(), headerArgs[1].trim());

                                    }
                                }
//                                System.out.println("headers = " + headers);
                                if (method.equals("GET")) {
                                    String indexContent = doGetIndexHtml();
                                    writeResponse(writer, "text/html", indexContent);

                                } else if (method.equals("POST")) {
                                    String postContent = readPostContent(reader, headers);
                                    System.out.println("postContent = " + postContent);

                                    String[] Data = postContent.split(",");
                                    String[] data1 = Data[0].split("\"");
                                    String[] data2 = Data[1].split("\"");
                                    String[] data3 = Data[2].split("\"");

//                                    System.out.println("data1 = " + data1[3]);
//                                    System.out.println("data2 = " + data2[3]);
//                                    System.out.println("data3 = " + data3[3]);
                                    System.out.println("URI : " + path);
                                    switch (path) {
                                        case "/show":
                                            String str = obj.AccDetails(Integer.parseInt(data2[3]));
                                            writeResponse(writer, "application/json", str);
                                            break;
                                        case "/create":
                                            String str1 = obj.CreateAcc(data1[3], data2[3], data3[3]);
                                            writeResponse(writer, "application/json", str1);

                                            break;
                                        case "/deposit":
                                            int accNum = Integer.parseInt(data2[3]);
                                            double amount = Double.parseDouble(data3[3]);
                                            String str2 = obj.AmountDepo(accNum, amount);
                                            writeResponse(writer, "application/json", str2);
                                            break;
                                        case "/withdrawal":
                                            int accNum1 = Integer.parseInt(data2[3]);
                                            double amount1 = Double.parseDouble(data3[3]);
                                            String str3 = obj.AmountWith(accNum1, amount1);
                                            writeResponse(writer, "application/json", str3);
                                            break;

                                        case "/transfer":
                                            int fromAccNum = Integer.parseInt(data1[3]);
                                            int toAccNum = Integer.parseInt(data2[3]);
                                            double amount2 = Double.parseDouble(data3[3]);
                                            String str4 = obj.AmountTransfer(fromAccNum, toAccNum, amount2);
                                            writeResponse(writer, "application/json", str4);
                                            break;

                                        default:
                                            System.out.println("terminate");
                                            break;
                                    }
                                }
                                writer.close();
                                reader.close();
                                socket.close();

                            } catch (Exception e) {
                                e.printStackTrace();

                                socket.close();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private void writeResponse(BufferedWriter writer, String contentType, String contentBody) throws IOException {

                    writer.write("HTTP/1.1 200 OK\r\n");
                    writer.write("Content-Type: " + contentType + "\r\n");
//                    System.out.println("indexContent = " + contentBody);
                    writer.write("Content-Length: " + contentBody.length() + "\r\n");
                    writer.write("\r\n");
                    writer.write(contentBody);
                }

                private String readPostContent(BufferedReader reader, Map<String, String> headers) throws IOException {
                    if (headers.containsKey("Content-Length")) {
                        String contentLength = headers.get("Content-Length");
                        char[] cbuf = new char[Integer.parseInt(contentLength)];
                        reader.read(cbuf);
                        return new String(cbuf);
                    } else {
                        return "";
                    }
                }

                private String doGetIndexHtml() throws IOException {
                    File file = new File(indexFilePath);
                    return new String(Files.readAllBytes(file.toPath()));
                }

            }.start();

        }

    }


    public String AccDetails(int acc) {
        String details = null;
        this.accNum5 = acc;

        try {

            String user = "postgres";
            String password = "0000";
            String url = "jdbc:postgresql://localhost:8001/sampleDB";
            Connection conn = DriverManager.getConnection(url, user, password);

            String getAmt = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
            preparedStmt1.setInt(1, accNum5);
            ResultSet rs = preparedStmt1.executeQuery();

            if (rs.next()) {
                System.out.println("Name : " + rs.getString(1));
                System.out.println("NIC : " + rs.getString(2));
                System.out.println("Address :" + rs.getString(3));
                System.out.println("AccNo :" + rs.getString(4));
                System.out.println("Amount :" + rs.getString(5));

                details = "Account Name : " + rs.getString(1) + "\r\n NIC Number : " + rs.getString(2) + "\r\n Address : " + rs.getString(3) +
                        "\r\n Account Number : " + rs.getString(4) + "\r\n Balance : " + rs.getString(5);
            } else {
                details = "No such user id is already registered";
            }


            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    public String CreateAcc(String name, String nic, String address) {
        this.name = name;
        this.nic = nic;
        this.address = address;
        String AccCreated = null;

        try {

            String user = "postgres";
            String password = "0000";
            String url = "jdbc:postgresql://localhost:8001/sampleDB";
            Connection conn = DriverManager.getConnection(url, user, password);
//            System.out.println("Connected to the PostgreSQL server successfully.");


            Statement stmt = conn.createStatement();

            // the mysql insert statement
            String query = " insert into \"bank\".customers (\"Name\", \"NIC\", \"Address\")"
                    + " values (?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, nic);
            preparedStmt.setString(3, address);

            String getAmt = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"Name\"= ?;";
            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
            preparedStmt1.setString(1, name);
            ResultSet rs = preparedStmt1.executeQuery();

            if (rs.next()) {
                System.out.println("Name : " + rs.getString(1));
                System.out.println("NIC : " + rs.getString(2));
                System.out.println("Address :" + rs.getString(3));
                System.out.println("AccNo :" + rs.getString(4));
                System.out.println("Amount :" + rs.getString(5));

                AccCreated = "Account Name : " + rs.getString(1) + "\r\n NIC Number : " + rs.getString(2) + "\r\n Address : " + rs.getString(3) +
                        "\r\n Account Number : " + rs.getString(4) + "\r\n Balance : " + rs.getString(5);
            } else {
                AccCreated = "No such user id is already registered";
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AccCreated;

    }

    public String AmountDepo(int acc, double amt1) {
        String Depo = null;
        double newAmt = 0;
        this.accNum = acc;
        this.amt = amt1;
        try {

            String user = "postgres";
            String password = "0000";
            String url = "jdbc:postgresql://localhost:8001/sampleDB";
            Connection conn = DriverManager.getConnection(url, user, password);

            String getAmt = "select \"Amount\"\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
            preparedStmt1.setInt(1, accNum);
            ResultSet rs = preparedStmt1.executeQuery();


            if (rs.next()) {

                newAmt = rs.getDouble(1);
                System.out.println(newAmt);
            }


            // create the java mysql update preparedstatement
            String query = "UPDATE bank.customers\n" +
                    "\tSET  \"Amount\"=?\n" +
                    "\tWHERE \"AccountNo\" = ?;";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setDouble(1, (amt + newAmt));
            preparedStmt.setInt(2, accNum);

            // execute the java preparedstatement
            preparedStmt.executeUpdate();

            String getAmt1 = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt11 = conn.prepareStatement(getAmt1);
            preparedStmt11.setInt(1, accNum);
            ResultSet rs1 = preparedStmt11.executeQuery();

            if (rs1.next()) {
                System.out.println("Name : " + rs1.getString(1));
                System.out.println("NIC : " + rs1.getString(2));
                System.out.println("Address :" + rs1.getString(3));
                System.out.println("AccNo :" + rs1.getString(4));
                System.out.println("Amount :" + rs1.getString(5));

                Depo = "Account Name : " + rs1.getString(1) + "\r\n NIC Number : " + rs1.getString(2) + "\r\n Address : " + rs1.getString(3) +
                        "\r\n Account Number : " + rs1.getString(4) + "\r\n Balance : " + rs1.getString(5);
            } else {
                Depo = "No such user id is already registered";
            }


            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Depo;
    }

    public String AmountWith(int acc, double amt1) {
        String Withdraw = null;
        double newAmt = 0;
        this.accNum1 = acc;
        this.amt1 = amt1;
        try {

            String user = "postgres";
            String password = "0000";
            String url = "jdbc:postgresql://localhost:8001/sampleDB";
            Connection conn = DriverManager.getConnection(url, user, password);

            String getAmt = "select \"Amount\"\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
            preparedStmt1.setInt(1, accNum1);
            ResultSet rs = preparedStmt1.executeQuery();


            if (rs.next()) {

                newAmt = rs.getDouble(1);

            }


            String query = "UPDATE bank.customers\n" +
                    "\tSET  \"Amount\"=?\n" +
                    "\tWHERE \"AccountNo\" = ?;";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setDouble(1, (newAmt - amt1));
            preparedStmt.setInt(2, accNum1);
            preparedStmt.executeUpdate();


            String getAmt1 = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt11 = conn.prepareStatement(getAmt1);
            preparedStmt11.setInt(1, accNum1);
            ResultSet rs1 = preparedStmt11.executeQuery();

            if (rs1.next()) {
                System.out.println("Name : " + rs1.getString(1));
                System.out.println("NIC : " + rs1.getString(2));
                System.out.println("Address :" + rs1.getString(3));
                System.out.println("AccNo :" + rs1.getString(4));
                System.out.println("Amount :" + rs1.getString(5));

                Withdraw = "Account Name : " + rs1.getString(1) + "\r\n NIC Number : " + rs1.getString(2) + "\r\n Address : " + rs1.getString(3) +
                        "\r\n Account Number : " + rs1.getString(4) + "\r\n Balance : " + rs1.getString(5);
            } else {
                Withdraw = "No such user id is already registered";
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  Withdraw;
    }

    public String AmountTransfer(int acc, int acc2, double amt1) {
        String Transfer1 = null;
        String Transfer2 = null;
        double newAmtto = 0;
        double newAmtfrom = 0;
        this.toaccNum = acc2;
        this.fromaccNum = acc;
        this.amt2 = amt1;

        try {

            String user = "postgres";
            String password = "0000";
            String url = "jdbc:postgresql://localhost:8001/sampleDB";
            Connection conn = DriverManager.getConnection(url, user, password);

            String getAmt = "select \"Amount\"\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt1 = conn.prepareStatement(getAmt);
            preparedStmt1.setInt(1, fromaccNum);
            ResultSet rs = preparedStmt1.executeQuery();


            if (rs.next()) {

                newAmtfrom = rs.getDouble(1);

            }


            String query = "UPDATE bank.customers\n" +
                    "\tSET  \"Amount\"=?\n" +
                    "\tWHERE \"AccountNo\" = ?;";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setDouble(1, (newAmtfrom - amt2));
            preparedStmt.setInt(2, fromaccNum);


            preparedStmt.executeUpdate();


            String getAmt1 = "select \"Amount\"\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(getAmt1);
            preparedStmt2.setInt(1, toaccNum);
            ResultSet rs1 = preparedStmt2.executeQuery();


            if (rs1.next()) {

                newAmtto = rs1.getDouble(1);

            }


            String query1 = "UPDATE bank.customers\n" +
                    "\tSET  \"Amount\"=?\n" +
                    "\tWHERE \"AccountNo\" = ?;";
            PreparedStatement preparedStmt3 = conn.prepareStatement(query1);
            preparedStmt3.setDouble(1, (newAmtto + amt2));
            preparedStmt3.setInt(2, toaccNum);
            preparedStmt3.executeUpdate();

            String getAmt2 = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt11 = conn.prepareStatement(getAmt2);
            preparedStmt11.setInt(1, fromaccNum);
            ResultSet rs11 = preparedStmt11.executeQuery();
            if (rs11.next()) {

                Transfer1 = "Account Name : " + rs11.getString(1) + "\r\n NIC Number : " + rs11.getString(2) + "\r\n Address : " + rs11.getString(3) +
                        "\r\n Account Number : " + rs11.getString(4) + "\r\n Balance : " + rs11.getString(5)
                        + "\r\n \r\n" ;
            }

            String getAmt3 = "select *\n" +
                    "from \"bank\".customers\n" +
                    "where  \"AccountNo\"= ?;";
            PreparedStatement preparedStmt111 = conn.prepareStatement(getAmt3);
            preparedStmt111.setInt(1, toaccNum);
            ResultSet rs111 = preparedStmt111.executeQuery();

            if (rs111.next()) {


                Transfer2 = " \r\n" + "Account Name : " + rs111.getString(1) + "\r\n NIC Number : " + rs111.getString(2) + "\r\n Address : " + rs111.getString(3) +
                        "\r\n Account Number : " + rs111.getString(4) + "\r\n Balance : " + rs111.getString(5) ;
            }




            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  Transfer1 + Transfer2;
    }



}