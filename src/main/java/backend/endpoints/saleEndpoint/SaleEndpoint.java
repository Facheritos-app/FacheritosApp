package backend.endpoints.saleEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.inventoryDTO.VehicleDTO;
import backend.dto.saleDTO.SaleConfirmationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SaleEndpoint {
    public Map<Boolean, ResultSet> getSales() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, sale.name_seller, sale.name_client, sale.payment_method , headquarter.name\n" +
                    "from (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, sale.name_seller, sale.name_client, payment.payment_method\n" +
                    "from (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\tsale.sale_date,sale.price, sale.name_seller, (person.first_name || ' ' || person.last_name) AS name_client \n" +
                    "\tfrom (select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date,sale.price, (person.first_name || ' ' || person.last_name) AS name_seller\n" +
                    "\t\t\t\tfrom sale join person on sale.id_worker = person.id_person) as sale join person ON sale.id_customer = person.id_person) as sale join payment on sale.id_payment_method = payment.id_payment) as sale join headquarter using (id_headquarter)");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public Map<Boolean, ResultSet> getSaleById(Integer idSale){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("Select Sale.id_sale, Sale.id_worker, Sale.id_customer, Sale.id_headquarter,Sale.id_payment_method, Sale.id_confirmation,Sale.sale_date,Sale.price, \n" +
                    "\tHeadq.name as name_headq, \n" +
                    "\t\tClient.cc as cc_client, (Client.first_name || ' ' || Client.last_name) AS name_client, Client.cellphone as cellphone_client, Client.email as email_client,\n" +
                    "\t\t\tSeller.cc as cc_seller, (Seller.first_name || ' ' ||Seller.last_name) AS name_seller, Seller.cellphone as cellphone_seller, Seller.email as email_seller,\n" +
                    "\t\t\t\tpayment.payment_method as name_method,\n" +
                    "\t\t\t\t\tconfirmation.confirmation_status\n" +
                    "from sale as Sale join headquarter as Headq using(id_headquarter) join person as Client on Sale.id_customer = Client.id_person join person as Seller on Sale.id_worker = Seller.id_person join payment on Sale.id_payment_method = payment.id_payment join confirmation on Sale.id_confirmation = confirmation.id_confirmation WHERE id_sale = ?");
            preparedStatement.setInt(1, idSale);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }
    public Map<Boolean, ResultSet> getSaleRequestById(Integer idSale){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM sale JOIN (SELECT id_worker, id_person FROM worker) AS workers USING(id_worker) JOIN person USING(id_person)" +
                                        "JOIN headquarter USING(id_headquarter) JOIN payment ON id_payment_method=id_payment JOIN confirmation USING(id_confirmation)" +
                                       "JOIN (SELECT first_name AS customer_firstname, last_name AS customer_lastname, id_person AS id_customer FROM person) AS customers USING(id_customer) WHERE id_sale=? ");
            preparedStatement.setInt(1, idSale);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getTableInSaleRequestById(Integer idSale){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM sale_car JOIN car USING(id_car)" +
                    "JOIN (SELECT id_model, description, price FROM model) AS models USING(id_model) WHERE id_sale = ?");
            preparedStatement.setInt(1, idSale);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getQunantityById(Integer idSale){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement(" select sum(quantity)\n" +
                    "  from sale_car WHERE id_sale = ?");
            preparedStatement.setInt(1, idSale);
            resultSet = preparedStatement.executeQuery();
            resultSet.next(); //show the row data
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    public Map<Boolean, ResultSet> getSalesCar(Integer idSale) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("select sale_car.id_car, car.id_model,color.color, model.price, sale_car.quantity\n" +
                    " from sale_car join car using(id_car) join color using (id_color) join model using (id_model) WHERE id_sale = ?");
            preparedStatement.setInt(1, idSale);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public Map<Boolean, ResultSet> getSaleByIdCustomer(Integer idCustomer){
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("SELECT * FROM sale JOIN headquarter USING(id_headquarter) " +
                    "JOIN payment ON sale.id_payment_method = payment.id_payment WHERE id_customer = ?");
            preparedStatement.setInt(1, idCustomer);
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            response.put(false, resultSet);
        }
        return response;
    }

    /**
     * getSaleRequests: ConfirmationDTO -> Boolean
     * Purpose: This method connects to the DB and brings the confirmation of the sale
     */
    public Map<Boolean, ResultSet> getSaleRequests() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("SELECT * FROM sale JOIN (SELECT id_worker, id_person FROM worker) AS workers USING(id_worker) JOIN person USING(id_person)" +
                    "JOIN headquarter USING(id_headquarter) JOIN payment ON id_payment_method=id_payment JOIN confirmation USING(id_confirmation)" +
                    "JOIN (SELECT first_name AS customer_firstname, last_name AS customer_lastname, id_person AS id_customer FROM person) AS customers USING(id_customer) WHERE id_confirmation=2 ");
            resultSet = preparedStatement.executeQuery();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    /**
     * changeStateConfirmation: ConfirmationDTO -> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean changeStateConfirmation(Integer idSale){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer idCar = null;
        Integer idHeadquarter = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE sale SET id_confirmation=1" +
                    "WHERE id_sale = ?");
            preparedStatement.setInt(1,idSale);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
                e.printStackTrace();
                return false;
        }
    }
}