package backend.endpoints.quotationEndpoint;

import backend.connectionBD.ConnectionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuotationEndpoint {

    public Map<Boolean, ResultSet> getQuotationsForTableView(){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT PW.first_name AS seller_firstname, PW.last_name AS seller_lastname, Customer.first_name AS customer_firstname, Customer.last_name AS customer_lastname," +
                    "H.name AS headquarter_name, *" +
                    "FROM quotation Quotation JOIN headquarter H USING(id_headquarter)" +
                    "JOIN worker USING (id_worker) JOIN person PW USING(id_person)" +
                    "JOIN person Customer ON(Quotation.id_customer = Customer.id_person)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getQuotation(Integer idQuotation){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT PW.first_name AS seller_firstname, PW.last_name AS seller_lastname, Customer.first_name AS customer_firstname, Customer.last_name AS customer_lastname,\n" +
                    "H.name AS headquarter_name, PW.cc AS seller_cc, Customer.cc AS customer_cc, PW.email AS seller_email, Customer.email AS customer_email, *\n" +
                    "FROM quotation Quotation JOIN headquarter H USING(id_headquarter)\n" +
                    "JOIN worker USING (id_worker) JOIN person PW USING(id_person)\n" +
                    "JOIN person Customer ON(Quotation.id_customer = Customer.id_person)\n" +
                    "JOIN car USING(id_car)\n" +
                    "JOIN model USING(id_model)\n" +
                    "WHERE id_quotation = ?");
            preparedStatement.setInt(1, idQuotation);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
}
