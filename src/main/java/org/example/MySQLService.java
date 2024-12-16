package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLService {

    private final String url = "jdbc:mysql://localhost:3306/kaina24_database";
    private final String username = "root";
    private final String password = "Your_Strong_Password1!";

    public void registerCategoryURL(String category_url){

        String sql = "INSERT INTO category_url (url)\n" +
                "VALUES (?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,category_url);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            if (e.getErrorCode() == 1062) System.out.println("URL " + category_url + " already in database!");
            else System.out.println("Database connection failed");
            return;
        }
        System.out.println("URL " + category_url + " was added!");
    }


    public List<String> getAllURLs(){
        List<String> orderList = new ArrayList<>();
        String sql = "SELECT * FROM category_url";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {
                orderList.add(resultSet.getString("url"));
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
        return orderList;
    }

    public void registerProduct(Product product){

        String sql = "INSERT INTO products (product_name, shop, price, image_url)\n" +
                "VALUES (?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,product.getName());
            preparedStatement.setString(2,product.getShop());
            preparedStatement.setDouble(3,product.getPrice());
            preparedStatement.setString(4,product.getImage_Url());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println("fail to add:" + product.toString());
        }

        System.out.println(product.toString() + " added.");

    }







}
