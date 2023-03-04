/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   public String user_type = null;
   public String user_login = null;
   public String current_order = null;

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(String.format("%-20.20s",rsmd.getColumnName(i).trim()) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (String.format("%-16.16s",rs.getString (i).trim()) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count number of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();

      Cafe esql = null;
      try{

         // use postgres JDBC driver.

          Class.forName("org.postgresql.Driver").newInstance();

         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         String pwd = "123";
         esql = new Cafe (dbname, dbport, user, pwd);

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
              if(esql.user_type.compareTo("Manager") == 0)
                System.out.println("MAIN MENU (MANAGER)");
              else if(esql.user_type.compareTo("Employee") == 0)
                  System.out.println("MAIN MENU (Employee)");
              else System.out.println("MAIN MENU (Customer)");
                System.out.println("---------");
                System.out.println("1. Goto Menu");
                if(esql.user_type.compareTo("Manager") == 0)
                    System.out.println("2. Update Profiles");
                else
                    System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println("5. Browse Order History");
                if(esql.user_type.compareTo("Manager") == 0 || esql.user_type.compareTo("Employee") == 0)
                    System.out.println("6. Mark Order as Paid");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql); break;
                   case 2: UpdateProfile(esql); break;
                   case 3: PlaceOrder(esql); break;
                   case 4: UpdateOrder(esql); break;
                   case 5: BrowseOrders(esql); break;
                   case 6: MarkAsPaid(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            System.out.println();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	    String type="Customer";
	    String favItems="";

				 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!\n");


      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         List<List<String>> temp = esql.executeQueryAndReturnResult(String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password));
	 if (userNum > 0)
        //public member
        esql.user_type = temp.get(0).get(4).trim();
        esql.user_login = temp.get(0).get(0).trim();
        System.out.println();
		return login;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

  public static void Menu(Cafe esql){
      try{

        //System.out.println(esql.user_type);

        //options only avaliable to manager
        if(esql.user_type.compareTo("Manager") == 0){
            System.out.println("---------");
            System.out.println("1. Add item to Menu");
            System.out.println("2. Update item from Menu");
            System.out.println("3. Delete item on Menu");
            System.out.println("4. Search for itemname or type");
            System.out.println("5. View all menu items");
            System.out.println("6. Quit");
                    switch (readChoice()){

                       case 1:
                           // Add an Item
                          System.out.print("\titemname: ");
                          String itemname = in.readLine();

                          //check if item already exists
                          if (esql.executeQuery(String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",itemname)) > 0){
                          System.out.println("Item already exists.\n");
                          return;
                          }

                            // Add a type
                          System.out.print("\ttype: ");
                          String type = in.readLine();
                            // Add a price
                          System.out.print("\tprice: ");
                          String price = in.readLine();
                            // Add a description
                          System.out.print("\tdescription: ");
                          String description = in.readLine();
                            //Add an imageurl
                          System.out.print("\timageurl: ");
                          String imageurl = in.readLine();

                          esql.executeUpdate(String.format("Insert Into Menu (itemname, type, price, description, imageurl )VALUES ('%s','%s','%s','%s','%s')",itemname,type,price,description,imageurl));
                            System.out.println("Add Successful\n");
                          break;
                       case 2:
                           //Update an item
                           System.out.print("\tSelect itemname to update: ");
                           String item_update = in.readLine();
                           String query2 = String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",item_update);
                           List<List<String>> update = esql.executeQueryAndReturnResult(query2);

                           //check if item name exists
                           if (update.size() == 0){
                           System.out.println("Invalid Item Name.\n");
                           return;
                           }
                           else{

                           //manager is able to update one field at a time
                           System.out.println("Select a field to update: ");
                           System.out.println("---------");
                           System.out.println("1. itemname");
                           System.out.println("2. type");
                           System.out.println("3. price");
                           System.out.println("4. description");
                           System.out.println("5. imageurl");
                           System.out.println("6. Quit");
                           switch(readChoice()){

                             //New itemname
                             case 1:
                             System.out.print("\tNew itemname: ");
                             String Uitemname = in.readLine();

                             esql.executeUpdate(String.format("Update Menu SET itemname = '%s' Where itemname = '%s'",Uitemname, item_update));
                             System.out.println("Update Successful.\n");
                             break;

                              //New type
                             case 2:
                             System.out.print("\tNew type: ");
                             String Utype = in.readLine();

                             esql.executeUpdate(String.format("Update Menu SET type = '%s' Where itemname = '%s'",Utype, item_update));
                             System.out.println("Update Successful.\n");
                             break;

                              //New price
                             case 3:
                             System.out.print("\tNew price: ");
                             String Uprice = in.readLine();

                             esql.executeUpdate(String.format("Update Menu SET price = '%s' Where itemname = '%s'",Uprice, item_update));
                             System.out.println("Update Successful.\n");
                             break;

                              //New description
                             case 4:
                             System.out.print("\tNew description: ");
                             String Udescription = in.readLine();

                             esql.executeUpdate(String.format("Update Menu SET description = '%s' Where itemname = '%s'",Udescription, item_update));
                             System.out.println("Update Successful.\n");
                             break;

                            //New imageurl
                             case 5:
                             System.out.print("\tNew imageurl: ");
                             String Uimageurl = in.readLine();

                             esql.executeUpdate(String.format("Update Menu SET imageurl = '%s' Where itemname = '%s'",Uimageurl, item_update));
                             System.out.println("Update Successful.\n");
                             break;

                             //do nothing
                             case 6:
                             break;}
                           }
                           break;
                       case 3:
                          //Search itemname to delete
                          System.out.print("\tSelect itemname to delete: ");
                          String Ditemname = in.readLine();

                          List<List<String>> delete = esql.executeQueryAndReturnResult(String.format("Select * From Menu Where itemname = '%s'",Ditemname));

                            //check if itemname is valid
                          if (delete.size() == 0){
                          System.out.println("Invalid Name.\n");
                          return;
                          }
                          else{
                           esql.executeUpdate(String.format("Delete from Menu Where itemname = '%s'",Ditemname));
                           System.out.println("Delete Successful.\n");
                           }

                       break;

                       case 4:
                         //search by name or type without specification
                         System.out.print("\tSearch by name or type: ");
                         String search2 = in.readLine();

                         //search by type or itemname
                         String query11 = String.format("SELECT * FROM Menu M WHERE M.type = '%s'",search2);
                         int qt = 0;
                         String query22 = String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",search2);
                         int qn = 0;

                         //query by type
                         qt = esql.executeQuery(query11);

                         //check if either of the two queries are valid
                         if (qt == 0){
                            //query by name since query by type is empty
                            qn = esql.executeQuery(query22);
                            if (qn == 0){
                                System.out.println("No results found.");
                                return;
                            }
                            else esql.executeQueryAndPrintResult(String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",search2));
                            }
                            else esql.executeQueryAndPrintResult(String.format("SELECT * FROM Menu M WHERE M.type = '%s'",search2));
                            System.out.println();
                           break;

                       case 5:
                           //Print whole menu
                           esql.executeQueryAndPrintResult("Select * From Menu");
                           System.out.println();
                           break;
                       case 6:
                            return;
                       default : System.out.println("Unrecognized choice!"); break;
                       }

        }
        //Options for a customer/employee
        else{
        System.out.println("---------");
        System.out.println("1. Search");
        System.out.println("2. View all menu items");
      switch (readChoice()){
         case 1:
         System.out.print("\tSearch by name or type: ");
         String search = in.readLine();

         //search by type or itemname
         String query1 = String.format("SELECT * FROM Menu M WHERE M.type = '%s'",search);
         int qt = 0;
         String query2 = String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",search);
         int qn = 0;

         //check if queried by type first
         qt = esql.executeQuery(query1);

         //check if the queries are valid
         if (qt == 0){
            //query by name
            qn = esql.executeQuery(query2);
            if (qn == 0){
                System.out.println("No results found.\n");
                return;
            }
            else esql.executeQueryAndPrintResult(String.format("SELECT * FROM Menu M WHERE M.itemname = '%s'",search));
            }
            else esql.executeQueryAndPrintResult(String.format("SELECT * FROM Menu M WHERE M.type = '%s'",search));
            System.out.println();
      break;

      case 2:
      //Print whole menu
      esql.executeQueryAndPrintResult("Select * From Menu");
      System.out.println();
      break;

      default : System.out.println("Unrecognized choice!\n"); break;
      }
}
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
  }

  public static void UpdateProfile(Cafe esql){
  try{

  if(esql.user_type.compareTo("Manager") == 0){

      //ask for login of user
      System.out.print("Enter login of user: ");
      String users_login = in.readLine();
      List<List<String>> user_change = esql.executeQueryAndReturnResult(String.format("Select * From Users Where login = '%s'",users_login));

      //check if user exists
      if(user_change.size() == 0){
          System.out.println("User not Found.\n");
          return;
      }

        //login is not changable for security reasons
        System.out.println("Select a field to update: ");
        System.out.println("---------");
        System.out.println("1. phonenum");
        System.out.println("2. password");
        System.out.println("3. favitems");
        System.out.println("4. type");
        System.out.println("5. Quit");

          switch(readChoice()){

              //update phonenum
              case 1:
              System.out.print("new phonenum: ");
              String phonenum = in.readLine();
              esql.executeUpdate(String.format("Update Users Set phonenum = '%s' Where login = '%s'",phonenum,users_login));
              System.out.println("Update Successful.\n");
              break;

              //update password
              case 2:
              System.out.print("new password: ");
              String password = in.readLine();
              System.out.print("Confirm password: ");
              String confirmed_password = in.readLine();

              //make user type password twice
              if(password.compareTo(confirmed_password)!=0){
              System.out.println("Passwords do not match.\n");
              return;
              }

              esql.executeUpdate(String.format("Update Users Set password = '%s' Where login = '%s'",password,users_login));
              System.out.println("Update Successful.\n");
              break;

               //update favitems
              case 3:
              System.out.print("new favitems: ");
              String favItems = in.readLine();
              esql.executeUpdate(String.format("Update Users Set favItems= '%s' Where login = '%s'",favItems,users_login));
              System.out.println("Update Successful.\n");
              break;

              case 4:
              System.out.print("new type: ");
              String type = in.readLine();
              esql.executeUpdate(String.format("Update Users Set type= '%s' Where login = '%s'",type,users_login));
              System.out.println("Update Successful.\n");
              break;

              //leave updateProfile
              case 5:
              return;

              default : System.out.println("Unrecognized choice!\n"); break;
         }
  }
  else
{       System.out.println("Select a field to update: ");
        System.out.println("---------");
        System.out.println("1. phonenum");
        System.out.println("2. password");
        System.out.println("3. favitems");
        System.out.println("4. Quit");

          switch(readChoice()){

              //update phonenum
              case 1:
              System.out.print("new phonenum: ");
              String phonenum = in.readLine();
              esql.executeUpdate(String.format("Update Users Set phonenum = '%s' Where login = '%s'",phonenum,esql.user_login));
              System.out.println("Update Successful.\n");
              break;

              //update password
              case 2:
              System.out.print("new password: ");
              String password = in.readLine();
              System.out.print("Confirm password: ");
              String confirmed_password = in.readLine();

              //make user type password twice
              if(password.compareTo(confirmed_password)!=0){
              System.out.println("Passwords do not match.\n");
              return;
              }

              esql.executeUpdate(String.format("Update Users Set password = '%s' Where login = '%s'",password,esql.user_login));
              System.out.println("Update Successful.\n");
              break;

               //update favitems
              case 3:
              System.out.print("new favitems: ");
              String favItems = in.readLine();
              esql.executeUpdate(String.format("Update Users Set favItems= '%s' Where login = '%s'",favItems,esql.user_login));
              System.out.println("Update Successful.\n");
              break;

              //leave updateProfile
              case 4:
              return;

              default : System.out.println("Unrecognized choice!\n"); break;
         }
         }
     }
 catch(Exception e){
 System.err.println (e.getMessage ());
 }
  }

  public static void PlaceOrder(Cafe esql){
  try
      {
      //add up the total value of order

      if(esql.current_order == null){
            List<List<String>> empty_order = esql.executeQueryAndReturnResult(String.format("Select * From Orders where login = '%s' and Total = '0.0'",esql.user_login));
            if(empty_order.size() == 0){
                //create an empty order, so use that as order use trigger to fill in other values
                esql.executeUpdate(String.format("Insert Into Orders(login, paid) VALUES ('%s','f')",esql.user_login));
                empty_order = esql.executeQueryAndReturnResult(String.format("Select * From Orders where login = '%s' and Total = '0.0'",esql.user_login));
                esql.current_order = empty_order.get(0).get(0);
             }
            else{
            //empty order already exists, so use that as current order
            esql.current_order = empty_order.get(0).get(0);
            esql.executeUpdate("Update orders set timestamprecieved = 'Now()'");
                }
      }

      List<List<String>> Oquery = esql.executeQueryAndReturnResult(String.format("Select * From Orders Where orderid = '%s'",esql.current_order));

       //Print whole menu
       esql.executeQueryAndPrintResult("Select * From Menu");
       System.out.println();

      while(true){
          //get new total each time
          Oquery = esql.executeQueryAndReturnResult(String.format("Select * From Orders Where orderid = '%s'",esql.current_order));

            //print user menu
          System.out.println("---------");
          System.out.println("1. Add to Order");
          System.out.println("2. Delete from Order");
          System.out.println("3. Place Order");
          System.out.println("4. Quit");
          switch(readChoice()){
          case 1:
               System.out.print("\titemname to add: ");
               String to_add = in.readLine();
               List<List<String>> Mquery = esql.executeQueryAndReturnResult(String.format("Select * From Menu Where itemname = '%s'",to_add));

               //check if user inputs valid itemname
               if (Mquery.size() == 0){
                System.out.println("Itemname does not exist.\n");
                return;
               }

               //insert item status
               esql.executeUpdate(String.format("insert into ItemStatus (orderid,itemName,lastUpdated,status,comments) Values ('%s','%s',Now(),'','')",esql.current_order,to_add));

               float price = Float.parseFloat(Mquery.get(0).get(2).trim());
               float total = Float.parseFloat(Oquery.get(0).get(4));

               String new_total  = Float.toString(total+price);

               //update the total price in orders
               esql.executeUpdate(String.format("Update orders set total = '%s' where orderid = '%s' ",new_total,esql.current_order));

               //print all items on order
               esql.executeQueryAndPrintResult(String.format("select * from itemstatus where orderid in (select orderid from orders where orderid = '%s')",esql.current_order));

               //print total price
               System.out.println("Total is: "+new_total+"\n");

          break;

          //delete items from order
          case 2:
             System.out.print("\titemname to add: ");
             String to_remove = in.readLine();

             Mquery = esql.executeQueryAndReturnResult(String.format("Select * From Menu Where itemname = '%s'",to_remove));
             List<List<String>> Dquery = esql.executeQueryAndReturnResult(String.format("Select * From ItemStatus Where itemname = '%s' and orderid = '%s'",to_remove,esql.current_order));

            //check if user inputs valid itemname
            if (Mquery.size() == 0){
                 System.out.println("Itemname does not exist.\n");
                 return;
            }

             //check if item is on order
             if (Dquery.size() == 0){
                  System.out.println("Item is not on Order.\n");
                  return;
             }

         price = Float.parseFloat(Mquery.get(0).get(2).trim());
         total = Float.parseFloat(Oquery.get(0).get(4));

         new_total  = Float.toString(total-price);

         //delete the item_status
         esql.executeUpdate(String.format("Delete from ItemStatus where itemname = '%s' and orderId = '%s'",to_remove,esql.current_order));

         //update the total price in orders
         esql.executeUpdate(String.format("Update orders set total = '%s' where orderid = '%s' ",new_total,esql.current_order));


         //print all items on order
         esql.executeQueryAndPrintResult(String.format("select * from itemstatus where orderid in (select orderid from orders where orderid = '%s')",esql.current_order));

         //print total price
         System.out.println("Total is: "+new_total+"\n");
          break;

          //stop modifying current order and then quit
          case 3:
              System.out.println(String.format("Order#%s placed!\n",esql.current_order));
              esql.current_order = null;
              return;
        //      update orders

          //quit modifying order
          case 4:
          return;

          default : System.out.println("Unrecognized choice!\n"); break;
          }
        //   Insert into Orders (login,paid,timestamprecieved,total) Values ('Bob','f',Now(),2.0)
        }
      }
 catch(Exception e){
 System.err.println (e.getMessage ());
 }
  }

  public static void UpdateOrder(Cafe esql){
    try
    {
      System.out.print("\tOrderID: ");
      String OrderId = in.readLine();
      List<List<String>> Oquery = esql.executeQueryAndReturnResult(String.format("Select * From Orders Where orderid = '%s'",OrderId));

    //check if order is valid
      if(Oquery.size() == 0)
    {      System.out.println("Invalid OrderId.\n");
          return;
    }

    //check if order is placed by another user
      if( esql.user_login.compareTo(Oquery.get(0).get(1).trim())!=0)
    {      System.out.println(String.format ("Order#%s is placed by another user.\n",OrderId));
          return;
    }

    //Check if order is already paid for
      if(Oquery.get(0).get(2).trim().compareTo("t") == 0)
    {      System.out.println(String.format ("Order#%s is already paid.\n",OrderId));
          return;
    }
    //print all of the items on current order
    System.out.println(String.format("Items on order %s: ",OrderId));
    esql.executeQueryAndPrintResult(String.format("select * from itemstatus I where I.orderid in (Select orderid from orders where orderid = %s)",OrderId));

    //prompt the user
    System.out.println("---------");
    System.out.println("1. Add to Order");
    System.out.println("2. Delete from Order");
    System.out.println("3. Cancel Order");
    System.out.println("4. Quit");
    switch(readChoice()){

    //add to order
    case 1:
        System.out.print("\titemname to add: ");
        String to_add = in.readLine();
        List<List<String>> Mquery = esql.executeQueryAndReturnResult(String.format("Select * From Menu Where itemname = '%s'",to_add));

        //check if user inputs valid itemname
        if (Mquery.size() == 0){
         System.out.println("Itemname does not exist.\n");
         return;
        }

        float price = Float.parseFloat(Mquery.get(0).get(2).trim());
        float total = Float.parseFloat(Oquery.get(0).get(4));

        String new_total  = Float.toString(total+price);
        System.out.println("Original total is : "+total);
        System.out.println("New total is: "+new_total);


        //add a new item_status
        esql.executeUpdate(String.format("insert into ItemStatus (orderid,itemName,lastUpdated,status,comments) Values ('%s','%s',Now(),'','')",OrderId,to_add));

        //update the total price in orders
        esql.executeUpdate(String.format("Update orders set total = '%s' where orderid = '%s' ",new_total,OrderId));

        System.out.println(String.format ("Order#%s: %s successfully added.\n",OrderId,to_add));
    break;

    //remove from order
    case 2:
        System.out.print("\titemname to remove: ");
        String to_remove = in.readLine();

        List<List<String>> Dquery = esql.executeQueryAndReturnResult(String.format("Select * From ItemStatus Where itemname = '%s' and orderid = '%s'",to_remove,OrderId));
        Mquery = esql.executeQueryAndReturnResult(String.format("Select * From Menu Where itemname = '%s'",to_remove));

        //check if user inputs valid itemname
        if (Mquery.size() == 0){
         System.out.println("Itemname does not exist.\n");
         return;
        }

        //check if user inputs valid itemname
        if (Dquery.size() == 0){
         System.out.println("Item is not on Order.\n");
         return;
        }

        price = Float.parseFloat(Mquery.get(0).get(2).trim());
        total = Float.parseFloat(Oquery.get(0).get(4));

        new_total  = Float.toString(total-price);
        System.out.println("Original total is : "+total);
        System.out.println("New total is: "+new_total);

        //delete the item_status
        esql.executeUpdate(String.format("Delete from ItemStatus where itemname = '%s' and orderId = '%s'",to_remove,OrderId));

        //update the total price in orders
        esql.executeUpdate(String.format("Update orders set total = '%s' where orderid = '%s' ",new_total,OrderId));

        //If order is empty, then delete the order
        if (Float.parseFloat(new_total) == 0){
            System.out.println("Order is empty, now deleting order.\n");
            esql.executeUpdate(String.format("Delete from orders where orderid = '%s'",OrderId));
            }
        System.out.println(String.format ("Order#%s: Update is Successful.\n",OrderId));

    break;

    //cancel order
    case 3:
        esql.executeUpdate(String.format("Delete from orders where orderid = '%s'",OrderId));
        System.out.println(String.format ("Order#%s is now canceled.\n",OrderId));
    break;

    //do nothing
    case 4:
        return;

    default : System.out.println("Unrecognized choice!\n"); break;
    }
  }
    catch(Exception e){
    System.err.println (e.getMessage ());
    }
  }
  public static void BrowseOrders(Cafe esql){
  try{
    //print all unpaid orders within 24 hours
    if(esql.user_type.compareTo("Manager") == 0 || esql.user_type.compareTo("Employee") == 0){
    System.out.println("Viewing all unpaid orders within last 24hrs.");
    esql.executeQueryAndPrintResult("Select * From Orders Where paid = 'f' and timestamprecieved >= NOW() - '1 day'::INTERVAL");
    System.out.println();}

    //prints the top 5 orders
    else{
    System.out.println("Viewing last 5 orders. ");
    esql.executeQueryAndPrintResult(String.format("Select * From Orders O Where O.login in (Select U.login From Users U Where U.login = '%s') Order By timestamprecieved DESC Limit 5",esql.user_login));
    System.out.println();}
    }
      catch(Exception e){
      System.err.println (e.getMessage ());
      }
  }

  public static void MarkAsPaid(Cafe esql){
    try
        {

        //only employee or manager can mark as paid
        if(esql.user_type.compareTo("Manager") == 0 || esql.user_type.compareTo("Employee") == 0)
        {
            ;
        }
        else{
            System.out.println("Unrecognized choice!");
            return;
        }

          System.out.print("\tOrderID: ");
          String OrderId = in.readLine();
          List<List<String>> Oquery = esql.executeQueryAndReturnResult(String.format("Select * From Orders Where orderid = '%s'",OrderId));

        //check if order is valid
          if(Oquery.size() == 0)
        {      System.out.println("Invalid OrderId.\n");
              return;
        }
          System.out.println(String.format ("Mark Order#%s as:\n",OrderId));
          System.out.println("1. paid ");
          System.out.println("2. not paid ");
          System.out.println("3. Quit.");
          System.out.print("\tOrderID: ");

          switch(readChoice()){
            case 1:
            //paid
                esql.executeUpdate(String.format("Update orders set paid = 't' where orderId = '%s'",OrderId));
                System.out.println(String.format ("Order#%s marked as paid.\n",OrderId));
            break;

            case 2:
            //unpaid
                esql.executeUpdate(String.format("Update orders set paid = 'f' where orderId = '%s'",OrderId));
                System.out.println(String.format ("Order#%s marked as unpaid.\n",OrderId));
            break;

            case 3:
            break;

          default : System.out.println("Unrecognized choice!\n"); break;
          }

        }

       catch(Exception e){
       System.err.println (e.getMessage ());
       }
       }


}//end Cafe

