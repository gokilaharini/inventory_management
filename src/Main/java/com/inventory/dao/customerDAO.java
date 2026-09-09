package com.inventory.dao;
import com.inventory.exception.databaseException;
import com.inventory.model.Customer;
import com.inventory.config.databaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class customerDAO {

    public Customer createcustomer(Customer cus) {
        String sql = """
                INSERT INTO customer
                (customer_name,cus_created,mobile_number,location)
                VALUES (?, ?, ?, ?)
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, cus.getCustomer_name());
            statement.setString(2,cus.getCus_created());
            statement.setBigDecimal(3,cus.getMobile_number());
            statement.setString(4, cus.getLocation());

            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    cus.setCustomer_id(keys.getInt(1));
                }
            }

            return cus;

        } catch (SQLException e) {
            throw new databaseException("Failed to create item", e);
        }
    }

    public Customer getCustomer(int customer_id){
        String sql = """
                SELECT customer_id,
                       customer_name,
                       cus_created,
                       mobile_number,
                       location
                FROM customer
                WHERE customer_id = ?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, customer_id);
            try (ResultSet result=statement.executeQuery()) {
                if(result.next()) {
                    return mapResultSet(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new databaseException("Failed to find item", e);
        }
    }

    public List<Customer> findAllCustomers() {
        String sql = """
                SELECT customer_id,
                       customer_name,
                       cus_created,
                       mobile_number,
                       location
                FROM customer
                ORDER BY customer_id
                """;

        List<Customer> customers = new ArrayList<>();
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                ResultSet result =statement.executeQuery()
        ) {
            while (result.next()) {
                customers.add(mapResultSet(result));
            }
            return customers;

        } catch (SQLException e) {
            throw new databaseException("Failed to fetch items", e);
        }
    }



    public Customer mapResultSet(ResultSet result) throws SQLException{
        Customer cus=new Customer();

        cus.setCustomer_id(result.getInt("customer_id"));
        cus.setCustomer_name(result.getString("customer_name"));
        cus.setCus_created(result.getString("cus_created"));
        cus.setMobile_number(result.getBigDecimal("mobile_number"));
        cus.setLocation(result.getString("location"));

        return cus;
    }
}
