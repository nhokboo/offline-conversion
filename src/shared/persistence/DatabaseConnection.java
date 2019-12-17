package shared.persistence;

import gomhangvn.data.model.GomhangProduct;
import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnProduct;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class DatabaseConnection {
    private Connection connection = null;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection makeDbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nhanhvnstorage", "root", "langthanG*1992");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void persistNhanhvnProducts(List<NhanhvnProduct> products) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_product_list (idNhanh, productName, parentId)" +
                    "VALUES(?,?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int totalChanges=0;
            for(NhanhvnProduct productElement: products) {
                String idNhanh = productElement.getIdNhanh();
                String name = productElement.getName();
                String parentId = productElement.getParentId();
                preparedStatement.setString(1, idNhanh);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, parentId);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting nhanhvnproducts: " + products.size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistGomhangvnProducts(List<GomhangProduct> products) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO gomhangvn_product_list (id, productName)" +
                    "VALUES(?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int totalChanges=0;
            for(GomhangProduct productElement: products) {
                String id = productElement.getId();
                String name = productElement.getName();
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting gomhangvnproducts: " + products.size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistNhanhvnBills(List<NhanhvnBill> bills) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_bills (id, customerName, customerMobile, createdDateTime, money)" +
                    "VALUES(?,?,?,?,?)" +
                    "ON DUPLICATE KEY UPDATE" + 
                    " customerName = VALUES(customerName)," + 
                    " customerMobile = VALUES(customerMobile)," +
            		" createdDateTime = VALUES(createdDateTime)," +
            		" money = VALUES(money);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int totalChanges=0;
            for(NhanhvnBill billElement: bills) {
                String id = billElement.getId();
                String customerName = billElement.getCustomerName();
                String customerMobile = billElement.getCustomerMobile();
                String createdDateTime = billElement.getCreatedDateTime();
                double money = billElement.getMoney();

                if(customerName == null) {
                    customerName = "";
                }
                if(customerMobile == null) {
                    customerMobile = "";
                }

                preparedStatement.setString(1, id);
                preparedStatement.setString(2, customerName);
                preparedStatement.setString(3, customerMobile);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(createdDateTime));
                preparedStatement.setDouble(5, money);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting bills: " + bills.size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + " ==================");
            connection.close();
        }
    }
    
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseConnection dbConn = new DatabaseConnection();
        ProductDataService productDataService = new ProductDataService();

        GomhangProductService gomhangProductService = new GomhangProductService();

        BillDataService billDataService = new BillDataService();
    }
}
