package com.inventory.dao;

import com.inventory.config.databaseConfig;
import com.inventory.exception.databaseException;
import com.inventory.model.orderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class orderItemDAO {

    public orderItem findItemOrdersById(int itemId) {
        String sql = """
                SELECT order_id,
                       item_id,
                       quant_ordered,
                       quant_issued,
                       amount
                FROM orderitems
                WHERE item_id=?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, itemId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapItemSet(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new databaseException("Failed to find item order", e);
        }
    }

    public List<orderItem> findAllItemOrders() {

        String sql = """
                SELECT order_id,
                       item_id,
                       quant_ordered,
                       quant_issued,
                       amount
                FROM orderitems
                ORDER BY item_id
                """;

        List<orderItem> items = new ArrayList<>();

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                items.add(mapItemSet(result));
            }

            return items;

        } catch (SQLException e) {
            throw new databaseException("Failed fetch ordered items", e);
        }
    }

    public boolean update(int orderId,int itemId,int quant_issued) {

        String sql = """
                UPDATE orderitems
                SET quant_issued=?
                WHERE item_id = ? and order_id=?
                """;

        String sql2= """
                SELECT quant_issued
                FROM orderitems
                WHERE item_id=? and order_id=?
                """;

        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2)
        ) {
            stm.setInt(1,itemId);
            stm.setInt(2,orderId);
            ResultSet res=stm.executeQuery();
            int quant=0;
            if(res.next()){
                quant=res.getInt("quant_issued");}
            statement.setInt(1, quant+quant_issued);
            statement.setInt(2, itemId);
            statement.setInt(3, orderId);
            int updated = statement.executeUpdate();

            if(updated == 0) {
                return false;
            }

            return true;

        } catch (SQLException e) {
            throw new databaseException("Failed to update item", e);
        }
    }


    public orderItem mapItemSet(ResultSet res) throws SQLException{

        orderItem item=new orderItem();

        item.setOrder_id(res.getInt("order_id"));
        item.setItem_id(res.getInt("item_id"));
        item.setQuantity(res.getInt("quant_ordered"));
        item.setQuant_issued(res.getInt("quant_issued"));
        item.setAmount(res.getInt("amount"));

        return item;
    }
}
