package backend.endpoints.orderEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderEndpoint {

    /**
     * getOrdersForTableView: void -> Map<Boolean, ResultSet>
     * Purpose: this method obtains orders data from the database to fill the orders table.
     */
    public Map<Boolean, ResultSet> getOrdersForTableView(){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT * FROM\n" +
                            "(SELECT (person.first_name || ' ' || person.last_name) AS worker_name, worker.id_worker\n" +
                            "FROM worker JOIN person USING(id_person)) AS worker_data\n" +
                            "JOIN (SELECT job_order.id_job_order, job_order.id_worker, headquarter.name AS seat, person.cc, \n" +
                            "job_order.due_date, state.name AS status FROM person JOIN job_order \n" +
                            "ON person.id_person = job_order.id_customer JOIN state USING(id_state) JOIN headquarter \n" +
                            "USING(id_headquarter)) AS order_data USING(id_worker)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getOrderById: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets an order from the database according to an input ID.
     */
    public Map<Boolean, ResultSet> getOrderById(Integer idOrder){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT job_order.id_job_order, (person.first_name || ' ' || person.last_name) AS person_name, \n" +
                            "person.cc,  person.cellphone, headquarter.id_headquarter, headquarter.name AS headquarter_name,\n" +
                            "job_order.created_at, job_order.due_date, job_order.price, job_order.id_state, \n" +
                            "state.name AS status \n" +
                            "FROM person \n" +
                            "JOIN job_order ON person.id_person = job_order.id_customer\n" +
                            "JOIN state USING(id_state) \n" +
                            "JOIN headquarter \n" +
                            "USING(id_headquarter) \n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1,idOrder);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getOrderParts: Integer -> Map<Boolean, ResultSet>
     * Purpose: obtains the parts of an order from the database.
     */
    public Map<Boolean, ResultSet> getOrderParts(Integer idOrder){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT part.name, part.price, job_order_part.quantity, part.id_part\n" +
                            "FROM job_order \n" +
                            "JOIN job_order_part USING(id_job_order) \n" +
                            "JOIN part USING (id_part)\n" +
                            "WHERE id_job_order = ?");
            preparedStatement.setInt(1, idOrder);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getTotalPriceAndQuantity: Integer -> Map<Boolean, ResultSet>
     * Purpose: gets the total price and the quantity of parts of an order from the database
     * according to an input ID.
     */
    public Map<Boolean, ResultSet> getTotalPriceAndQuantity(Integer idOrder){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(
                    "SELECT (sum(job_order_part.quantity*part.price)+job_order.price) AS total,\n" +
                            "sum(job_order_part.quantity) AS quantity\n" +
                            "FROM job_order\n" +
                            "JOIN job_order_part USING(id_job_order)\n" +
                            "JOIN part USING (id_part)\n" +
                            "WHERE id_job_order = ?\n" +
                            "GROUP BY(id_job_order)");
            preparedStatement.setInt(1,idOrder);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }


}