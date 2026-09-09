package com.inventory.dao;

import com.inventory.model.Pack;
import com.inventory.config.databaseConfig;
import com.inventory.model.packItem;
import com.inventory.exception.databaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class packageDAO {

    private final orderItemDAO orderItemDAO;
    private final ItemDAO ItemDAO;

    public packageDAO(){
        this.orderItemDAO=new orderItemDAO();
        this.ItemDAO=new ItemDAO();
    }

    public Pack createPackage(Pack pack) {

        validatePackage(pack);

        String sql = """
                INSERT INTO package
                (order_id,order_date,quantity,package_date,status,d_height,d_width,d_length,weight)
                VALUES (?, ?, ?, ?, ?, ? ,? ,? ,?)
                """;

        String sql2= """
                INSERT INTO packageitems
                (package_id,order_id,item_id,item_quantity)
                VALUES (?,?,?,?)
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setInt(1, pack.getOrder_id());
            statement.setString(2, pack.getOrder_date());
            statement.setInt(3, pack.getQuantity());
            statement.setString(4, pack.getPackage_date());
            statement.setString(5, pack.getStatus());
            statement.setInt(6,pack.getD_height());
            statement.setInt(7,pack.getD_width());
            statement.setInt(8,pack.getD_length());
            statement.setInt(9,pack.getWeight());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    pack.setPackage_id(keys.getInt(1));
                }
            }
            try (
                    PreparedStatement stm=connection.prepareStatement(sql2)
            ){
                for(packItem item:pack.getItems()){
                    stm.setInt(1,pack.getPackage_id());
                    stm.setInt(2,pack.getOrder_id());
                    stm.setInt(3,item.getItem_id());
                    stm.setInt(4,item.getItem_quantity());

                    stm.executeUpdate();

                    orderItemDAO.update(pack.getOrder_id(),item.getItem_id(), item.getItem_quantity());
                    ItemDAO.updateStock(item.getItem_id(), item.getItem_quantity(),"package");
                }
            }
            catch (SQLException e){
                throw new databaseException("Failed to create package items",e);
            }
            return pack;

        } catch (SQLException e) {
            throw new databaseException("Failed to create package", e);
        }
    }

    public Pack findPackageById(int packId) {
        String sql = """
                SELECT package_id,
                       order_id,
                       order_date,
                       quantity,
                       package_date,
                       status,
                       d_height,
                       d_width,
                       d_length,
                       weight
                FROM package
                WHERE package_id = ?
                """;

        String sql2= """
                SELECT item_id,
                item_quantity
                
                FROM packageitems
                WHERE package_id=?
                """;

        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement((sql2))
        ) {
            statement.setInt(1, packId);
            stm.setInt(1,packId);
            try (
                    ResultSet result=statement.executeQuery();
                    ResultSet res=stm.executeQuery()
            ) {

                if(result.next()) {
                    Pack pack= mapResultSet(result);
                    List<packItem> items=new ArrayList<>();
                    while(res.next()){
                        items.add(mapPackItemSet(res));
                    }
                    pack.setItems(items);
                    return pack;
                }
                return null;
            }


        } catch (SQLException e) {
            throw new databaseException("Failed to find Package", e);
        }
    }

    public List<Pack> findAllPackages() {

        String sql = """
                SELECT package_id,
                       order_id,
                       order_date,
                       quantity,
                       package_date,
                       status,
                       d_height,
                       d_width,
                       d_length,
                       weight
                FROM package
                ORDER BY package_id
                """;

        String sql2= """
                SELECT item_id,
                item_quantity
                
                FROM packageitems
                WHERE package_id=?
                """;

        List<Pack> packs=new ArrayList<>();
        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2);
                ResultSet result =statement.executeQuery()
        ) {

            while (result.next()) {
                Pack pack = mapResultSet(result);
                List<packItem> items = new ArrayList<>();
                stm.setInt(1, pack.getPackage_id());
                ResultSet res = stm.executeQuery();
                while (res.next()) {
                    items.add(mapPackItemSet(res));
                }
                pack.setItems(items);
                packs.add(pack);
            }

            return packs;
        } catch (SQLException e) {
            throw new databaseException("Failed to fetch items", e);
        }
    }

    public void updatePackage(int pack_id) {


        String sql = """
                UPDATE package
                SET status=?
                WHERE package_id = ? 
                """;

        try (
                Connection connection=databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, "shipped");
            statement.setInt(2,pack_id);

            int updated = statement.executeUpdate();

            if (updated == 0) {
                throw new IllegalArgumentException("no updates to perform");
            }


        } catch (SQLException e) {
            throw new databaseException("Failed to update item", e);
        }
    }

    public boolean deletePackage(int packId) {
        String sql = """
                DELETE FROM package
                WHERE package_id = ?
                """;

        String sql2= """
                DELETE FROM packageitems
                WHERE package_id = ?
                """;
        try (
                Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement stm=connection.prepareStatement(sql2)
        ) {
            statement.setInt(1, packId);
            stm.setInt(1,packId);
            return statement.executeUpdate() > 0 && stm.executeUpdate()>0;

        } catch (SQLException e) {
            throw new databaseException("Failed to delete package", e);
        }
    }

    public void validatePackage(Pack pack){

        String q= """
                SELECT order_type
                FROM salesorder
                WHERE order_id=?
                """;

        String sql= """
                SELECT 1
                FROM salesorder
                WHERE order_id=?
                LIMIT 1;
                """;

        String sql2= """
                SELECT quant_ordered,
                quant_issued
                FROM orderitems
                WHERE order_id=? and item_id=?
                """;

        String sql3= """
                SELECT 1
                FROM orderitems
                WHERE item_id=? and order_id=?
                LIMIT 1
                """;

        try(
            Connection connection=databaseConfig.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql);
            PreparedStatement query=connection.prepareStatement(q)
        ){
            query.setInt(1,pack.getOrder_id());
            ResultSet qres= query.executeQuery();
            if(qres.next() && (qres.getString("order_type")).equals("services")){
                throw new IllegalArgumentException("Services cannot be packed");
            }
            statement.setInt(1,pack.getOrder_id());
            ResultSet result=statement.executeQuery();
            if(!result.next()){
                throw new IllegalArgumentException("the order_id does not exist");
            }

            try (
                    PreparedStatement stm= connection.prepareStatement(sql2);
                    PreparedStatement stm2=connection.prepareStatement(sql3)
            ) {
                for (packItem item : pack.getItems()) {
                    stm2.setInt(1,item.getItem_id());
                    stm2.setInt(2,pack.getOrder_id());
                    ResultSet exists= stm2.executeQuery();
                    if(!exists.next()) {
                        throw new IllegalArgumentException("the item_id does not exist in salesorder");
                    }
                    stm.setInt(1, pack.getOrder_id());
                    stm.setInt(2,item.getItem_id());
                    ResultSet res=stm.executeQuery();
                    if(res.next()){
                        int quant=res.getInt("quant_ordered")-res.getInt("quant_issued");
                        if(quant<item.getItem_quantity()){
                            throw new IllegalArgumentException("the item quantity must not exceed the ordered quantity");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new databaseException("items cannot be verified", e);
            }
        } catch (SQLException e) {
            throw new databaseException("the order_id cannot be verified",e);
        }
    }

    private Pack mapResultSet(ResultSet result) throws SQLException {

        Pack pack=new Pack();

        pack.setPackage_id(result.getInt("package_id"));
        pack.setOrder_id(result.getInt("order_id"));
        pack.setOrder_date(result.getString("order_date"));
        pack.setQuantity(result.getInt("quantity"));
        pack.setPackage_date(result.getString("package_date"));
        pack.setStatus(result.getString("status"));
        pack.setD_height(result.getInt("d_height"));
        pack.setD_width(result.getInt("d_width"));
        pack.setD_length(result.getInt("d_length"));
        pack.setWeight(result.getInt("weight"));

        return pack;
    }

    private packItem mapPackItemSet(ResultSet res) throws SQLException{

        packItem item=new packItem();

        item.setItem_id(res.getInt("item_id"));
        item.setItem_quantity(res.getInt("item_quantity"));

        return item;
    }
}
