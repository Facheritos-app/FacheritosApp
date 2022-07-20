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
                    "SELECT job_order.id_job_order, worker_data.worker_name, headquarter.name AS seat, person.cc," +
                            "job_order.due_date, state.name AS status FROM person JOIN job_order " +
                            "ON person.id_person = job_order.id_customer JOIN state USING(id_state) JOIN headquarter " +
                            "USING(id_headquarter) JOIN " +
                            "(SELECT (person.first_name || ' ' || person.last_name) AS worker_name, worker_person.id_worker " +
                            "FROM (SELECT worker.id_person, worker.id_worker FROM job_order JOIN worker " +
                            "USING(id_worker)) as worker_person JOIN person ON worker_person.id_person = person.id_person) " +
                            "as worker_data USING(id_worker)");
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
     * Purpose: gets a customer from the database according to an input Id.
     */
    public Map<Boolean, ResultSet> getOrderById(Integer idOrder){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT *, headquarter.name AS seat,  state.name AS status, " +
                    "part.name AS partname, job_order.created_at AS createdOrder, job_order.price AS jobPrice FROM person " +
                    "JOIN job_order ON person.id_person = job_order.id_customer JOIN state USING(id_state) JOIN headquarter U" +
                    "SING(id_headquarter) JOIN job_order_part USING(id_job_order) JOIN part USING(id_part)  WHERE id_job_order = ?");
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