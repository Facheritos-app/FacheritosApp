package backend.endpoints.saleEndpoint;

import backend.connectionBD.ConnectionBD;
import backend.dto.inventoryDTO.VehicleDTO;
import backend.dto.saleDTO.SaleConfirmationDTO;
import backend.dto.saleDTO.SaleDTO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SaleEndpoint {
    public Map<Boolean, ResultSet> getSales() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("select sale.id_sale,sale.id_worker,sale.id_customer,sale.id_headquarter,sale.id_payment_method,\n" +
                    "\t\t\tsale.sale_date, sale.price, sale.name_seller, sale.name_client, sale.payment_method , headquarter.name\n" +
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
                    "\tHeadq.name as name_headq,Headq.id_headquarter as id_headquarter, \n" +
                    "\t\tClient.cc as cc_client, (Client.first_name || ' ' || Client.last_name) AS name_client, Client.cellphone as cellphone_client,Client.id_person as idClient , Client.email as email_client,\n" +
                    "\t\t\tSeller.cc as cc_seller, (Seller.first_name || ' ' ||Seller.last_name) AS name_seller, Seller.cellphone as cellphone_seller,Seller.id_person as idSeller , Seller.email as email_seller,\n" +
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
            preparedStatement = conn.prepareStatement("SELECT *, models.price AS modelprice FROM sale_car JOIN sale USING(id_sale) JOIN car USING(id_car)" +
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
            preparedStatement = conn.prepareStatement("select sale_car.id_car,model.description  as description ,car.id_model,color.color, model.price, car.assemble_year,sale_car.quantity\n" +
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


    public Map<Boolean, ResultSet> getCar(Integer idHeadquarter) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("select car.id_car, car.id_model,car.assemble_year, car.id_Color, car_headquarter.id_headquarter, car_headquarter.quantity,\n" +
                    "\tcolor.color, model.price, model.description\n" +
                    "from car join car_headquarter  using (id_car) join model using (id_model) join color using (id_color)\n" +
                    "where id_headquarter =  ? and car.status = 'Activo'");
            preparedStatement.setInt(1, idHeadquarter);
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
     * changeStateConfirmation: Integer -> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean changeStateConfirmation(Integer idSale){
        PreparedStatement preparedStatement = null;
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
    /**
     * changeStateReject: Integer-> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean changeStateReject(Integer idSale){
        PreparedStatement preparedStatement = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE sale SET id_confirmation=3" +
                    "WHERE id_sale = ?");
            preparedStatement.setInt(1,idSale);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * changeCarsQuantityReject: Integer, Integer-> Boolean
     * Purpose: This method connects to the DB and saves a vehicle,
     * if successful, it returns true, if not it returns false
     */
    public Boolean changeCarsQuantityReject(Integer idCar, Integer quantity, Integer idHeadquarter){
        PreparedStatement preparedStatement = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE car_headquarter SET quantity = quantity + ? WHERE id_car = ? AND id_headquarter=?");
            preparedStatement.setInt(1,quantity);
            preparedStatement.setInt(2,idCar);
            preparedStatement.setInt(3,idHeadquarter);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean changeCarsQuantityRestar(Integer idCar, Double quantity, Integer id_headquarter){
        PreparedStatement preparedStatement = null;
        HashMap<Boolean, Integer> response = new HashMap<>();
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("UPDATE car_headquarter SET quantity = quantity - ? WHERE id_car = ? and id_headquarter =?");
            preparedStatement.setDouble(1,quantity);
            preparedStatement.setInt(2,idCar);
            preparedStatement.setInt(3,id_headquarter);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Map<Boolean, ResultSet> insertarVenta(SaleDTO saleDTO){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("insert into sale(id_sale, id_worker, id_customer,id_headquarter,id_payment_method,id_confirmation,sale_date,price) " +
                    "values(default, ? ,?,?,?,?,default,?) returning  id_sale;");
            preparedStatement.setInt(1, saleDTO.getId_worker());
            preparedStatement.setInt(2, saleDTO.getId_customer());
            preparedStatement.setInt(3, saleDTO.getId_headquarter());
            preparedStatement.setInt(4, saleDTO.getId_payment_method());
            preparedStatement.setInt(5, saleDTO.getId_confirmation());
            preparedStatement.setDouble(6, saleDTO.getPrice());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            response.put(true, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            response.put(false, resultSet);
        }

        return response;
    }

    public Boolean updateVenta(SaleDTO saleDTO){
        System.out.println("Hago consulta");
        PreparedStatement preparedStatement = null;

        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("UPDATE sale set id_customer = ? , id_confirmation = ? ,id_payment_method = ? , price = ?  where id_sale = ? ;");
            preparedStatement.setInt(1, saleDTO.getId_customer());
            preparedStatement.setInt(2, saleDTO.getId_confirmation());
            preparedStatement.setInt(3, saleDTO.getId_payment_method());
            preparedStatement.setDouble(4, saleDTO.getPrice());
            preparedStatement.setDouble(5, saleDTO.getId_sale());
            preparedStatement.executeUpdate();
            System.out.println("Hago consulta2");
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }

    }

    public Boolean updateSaleCar(Integer newQuantiy, Integer idCar, Integer idSale){
        PreparedStatement preparedStatement = null;

        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("update sale_car set quantity = ?\n" +
                    "where id_car = ? and id_sale = ?");
            preparedStatement.setInt(1, newQuantiy);
            preparedStatement.setInt(2, idCar);
            preparedStatement.setInt(3,  idSale);
            preparedStatement.executeUpdate();
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }

    public Boolean updateSaleHeadquarter(Integer newQuantiy, Integer idCar, Integer idHead){
        PreparedStatement preparedStatement = null;

        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("update car_headquarter set quantity = ?\n" +
                    "where id_car = ? and id_headquarter = ?");
            preparedStatement.setInt(1, newQuantiy);
            preparedStatement.setInt(2, idCar);
            preparedStatement.setInt(3,  idHead);
            preparedStatement.executeUpdate();
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }

    public Boolean insertCar(Integer id_car,Integer id_sale,Integer quantity){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<Boolean, ResultSet> response = new HashMap<>();
        try (Connection conn = ConnectionBD.connectDB().getConnection()) {
            preparedStatement = conn.prepareStatement("insert into sale_car(id_car, id_sale, quantity) " +
                    "values(?,?,?)");
            preparedStatement.setInt(1, id_car);
            preparedStatement.setInt(2, id_sale);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Boolean deleteCar(SaleDTO saleDTO, Integer idCar){
        PreparedStatement preparedStatement = null;
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            preparedStatement = conn.prepareStatement("delete from sale_car\n" +
                    "  where id_sale= ? and id_car = ?");
            preparedStatement.setInt(1, saleDTO.getId_sale());
            preparedStatement.setInt(2, idCar);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}