package com.inventory.dao;

import com.inventory.config.databaseConfig;
import com.inventory.exception.databaseException;
import com.inventory.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public Item create(Item item) {
        String sql = """
                INSERT INTO items
                (item_name, weight, stock_on_hand, committed_stock,available_stock,
                 cost_price, selling_price)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection= databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, item.getName());
            statement.setInt(2, item.getWeight());
            statement.setInt(3, item.getStockOnHand());
            statement.setInt(4,item.getCommittedStock());
            statement.setBigDecimal(5, item.getCostPrice());
            statement.setBigDecimal(6, item.getSellingPrice());
            statement.setInt(7,item.getAvailableStock());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItemId(keys.getInt(1));
                }
            }

            return item;

        } catch (SQLException e) {
            throw new databaseException("Failed to create item", e);
        }
    }


    public Item findById(int itemId) {

        String sql = """
                SELECT item_id,
                       item_name,
                       weight,
                       stock_on_hand,
                       committed_stock,
                       cost_price,
                       selling_price
                FROM items
                WHERE item_id = ?
                """;

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, itemId);
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

    public List<Item> findAll() {

        String sql = """
                SELECT item_id,
                       item_name,
                       weight,
                       stock_on_hand,
                       committed_stock,
                       cost_price,
                       selling_price
                FROM items
                ORDER BY item_id
                """;

        List<Item> items = new ArrayList<>();

        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                ResultSet result =statement.executeQuery()
        ) {

            while (result.next()) {
                items.add(mapResultSet(result));
            }

            return items;

        } catch (SQLException e) {
            throw new databaseException("Failed to fetch items", e);
        }
    }

    public boolean updateStock(int itemId,int quant, String str) {
        String sql = """
                UPDATE items
                SET stock_on_hand=?,
                    committed_stock=?
                WHERE item_id=?
                """;

        String sql2 = """
                SELECT stock_on_hand,
                       committed_stock
                FROM items
                WHERE item_id=?
                """;

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm = connection.prepareStatement(sql2)
        ) {
            stm.setInt(1, itemId);
            int stock_on_hand = 0;
            int committed_stock=0;
            try (ResultSet res = stm.executeQuery()) {
                if (res.next()) {
                    stock_on_hand = res.getInt("stock_on_hand");
                    committed_stock = res.getInt("committed_stock");
                }
            } catch (SQLException e) {
                throw new databaseException("stm error", e);
            }
            if (str.equals("package")) {
                statement.setInt(1, stock_on_hand - quant);
                statement.setInt(2, committed_stock - quant);
            } else {
                statement.setInt(1, stock_on_hand);
                statement.setInt(2, committed_stock + quant);
            }
            statement.setInt(3, itemId);

            int update = statement.executeUpdate();

            if (update == 0) {
                return false;
            }

            return true;
        }


        catch(SQLException e){
            throw new databaseException("Failed to update Stock", e);
        }
    }

    public Item update(Item item) {

        String sql = """
                UPDATE items
                SET item_name=?,
                    weight=?,
                    cost_price=?,
                    selling_price=?
                WHERE item_id = ?
                """;

        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, item.getName());
            statement.setInt(2, item.getWeight());
            statement.setBigDecimal(3, item.getCostPrice());
            statement.setBigDecimal(4, item.getSellingPrice());
            statement.setInt(5, item.getItemId());

            int updated = statement.executeUpdate();

            if (updated == 0) {
                return null;
            }

            return item;

        } catch (SQLException e) {
            throw new databaseException("Failed to update item", e);
        }
    }

    public boolean delete(int itemId) {

        String sql = """
                DELETE FROM items
                WHERE item_id = ?
                """;

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, itemId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new databaseException("Failed to delete item", e);
        }
    }

    private Item mapResultSet(ResultSet resultSet) throws SQLException {

        Item item = new Item();

        item.setItemId(resultSet.getInt("item_id"));
        item.setName(resultSet.getString("item_name"));
        item.setWeight(resultSet.getInt("weight"));
        item.setStockOnHand(resultSet.getInt("stock_on_hand"));
        item.setCommittedStock(resultSet.getInt("committed_stock"));
        item.setAvailableStock();
        item.setCostPrice(resultSet.getBigDecimal("cost_price"));
        item.setSellingPrice(resultSet.getBigDecimal("selling_price"));

        return item;
    }
}