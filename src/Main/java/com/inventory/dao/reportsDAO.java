package com.inventory.dao;

import java.sql.Connection;
import com.inventory.config.databaseConfig;
import com.inventory.exception.databaseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class reportsDAO {

    public ResultSet partialPack(){
        String sql= """
                
                SELECT DISTINCT(order_id),
                customer_name,
                order_date,
                expected_ship_date,
                address
                
                FROM orderitems
                LEFT JOIN
                salesorder
                ON orderitems.order_id=salesorder.order_id
                
                WHERE quant_ordered>quant_issued
                """;

        try(
                Connection connection= databaseConfig.getConnection();
                PreparedStatement statement= connection.prepareStatement(sql);
            ){

                ResultSet result=statement.executeQuery();
                if(!result.next()){
                    throw new IllegalArgumentException("There are no incomplete sales orders");
                }
                return result;
        } catch (Exception e) {
            throw new databaseException("couldn't generate the report",e);
        }
    }

}
