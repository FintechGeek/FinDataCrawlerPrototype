package org.charlestech.fin.prototype.common;

import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Barry Zhu on 14-2-6.
 */
public class ConnectionPool {

    private static ConnectionPool poolInstance = null;

    private static synchronized void synInit() {
        if (null == poolInstance) {
            poolInstance = new ConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://115.28.140.140:3306/fin", "root", "gulliver", "fin_test");
        }
    }

    public static ConnectionPool getPoolInstance() {
        if (null == poolInstance) {
            synInit();
        }

        return poolInstance;
    }

    private String jdbcDriver = ""; // JDBC driver

    private String dbUrl = ""; // Database url

    private String dbUsername = ""; // Database username

    private String dbPassword = ""; // Database password

    private String testTable = ""; // Database test table

    private int initialConnections = 10; // Initial size of connection pool

    private int incrementalConnections = 5;// The size of auto increment for connection pool

    private int maxConnections = 50; // The max size for connection pool

    private Vector connections = null; // Connection pool vector

    /**
     * @param jdbcDriver
     * @param dbUrl
     * @param dbUsername
     * @param dbPassword
     */
    private ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername,
                           String dbPassword, String testTable) {

        this.jdbcDriver = jdbcDriver;

        this.dbUrl = dbUrl;

        this.dbUsername = dbUsername;

        this.dbPassword = dbPassword;

        this.testTable = testTable;

        try {
            createPool();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a connection pool;
     */

    private synchronized void createPool() throws Exception {

        // If the connection pool was not created, then create one.

        // Ensure the connection pool is not null.

        if (connections != null) {

            return; // If the pool exists, return immediately.

        }

        // instantiate JDBC Driver

        Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());

        DriverManager.registerDriver(driver); // Register JDBC driver

        // Create the connection pool vector

        connections = new Vector();

        // Create the connection pool according to

        createConnections(this.initialConnections);

        System.out.println(" Connection pool is successfully created. ");

    }

    /**
     * Create numConnections connections and insert them into vector
     *
     * @param numConnections the number of connections to create
     */
    private void createConnections(int numConnections) throws SQLException {

        // Iteratively create connections

        for (int x = 0; x < numConnections; x++) {

            // if maxConnections is less than or equal to 0, then the number of connections is unlimited.

            if (this.maxConnections > 0
                    && this.connections.size() >= this.maxConnections) {

                break;

            }

            //add a new PooledConnection object to connections vector

            try {

                connections.addElement(new PooledConnection(newConnection()));

            } catch (SQLException e) {

                System.out.println(" Failed to create connection :" + e.getMessage());

                throw new SQLException();

            }

            System.out.println(" One connection is created ......");

        }

    }

    /**
     * Create a Db connection and return it
     *
     * @return DB connection
     */

    private Connection newConnection() throws SQLException {

        // Create one DB connection

        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

        // If it's the first time to create connection, then check the max allowed number of connections is supported

        // by the Database.

        //connections.size()==0 indicate no connection is created.

        if (connections.size() == 0) {

            DatabaseMetaData metaData = conn.getMetaData();

            int driverMaxConnections = metaData.getMaxConnections();

            if (driverMaxConnections > 0
                    && this.maxConnections > driverMaxConnections) {

                this.maxConnections = driverMaxConnections;

            }

        }

        return conn;

    }

    /**
     * getFreeConnection() will return an available DB connection,
     * <p/>
     * If there is no available DB connection, then try to get one sometime later.
     *
     * @return an available DB connection,
     */

    public synchronized Connection getConnection() throws SQLException {

        // Ensure the connection is created.

        if (connections == null) {

            return null;

        }

        Connection conn = getFreeConnection(); // Get a free connection

        while (conn == null) {

            wait(250);

            conn = getFreeConnection();


        }

        return conn;

    }

    /**
     * Get an available connection, if none, then create some connections into vector.
     * <p/>
     * Even so there is no available connection, then return null.
     *
     * @return an available Database connection.
     */

    private Connection getFreeConnection() throws SQLException {

        // Find a free connection in connection pool.

        Connection conn = findFreeConnection();

        if (conn == null) {

            createConnections(incrementalConnections);

            // Find the free connection again.

            conn = findFreeConnection();

            if (conn == null) {

                // Return null, if there is still no free connections.

                return null;

            }

        }

        return conn;

    }

    /**
     * Find a free connection. If none, return null.
     *
     * @return a free connection
     */

    private Connection findFreeConnection() throws SQLException {

        Connection conn = null;

        PooledConnection pConn = null;

        Enumeration enumerate = connections.elements();

        while (enumerate.hasMoreElements()) {

            pConn = (PooledConnection) enumerate.nextElement();

            if (!pConn.isBusy()) {

                // If the connection is not busy, then set it as busy.

                conn = pConn.getConnection();

                pConn.setBusy(true);

                // Test whether this connection is usable

                if (!testConnection(conn)) {

                    // If this connection is not usable, then create a new one.

                    try {

                        conn = newConnection();

                    } catch (SQLException e) {

                        System.out.println(" Failed to create the connection: " + e.getMessage());

                        return null;

                    }

                    pConn.setConnection(conn);

                }

                break; // Found a connection

            }

        }

        return conn;

    }

    /**
     * Test a connection. If it is not usable, close it and return false,
     * <p/>
     * otherwise return true.
     *
     * @param conn connection to be tested.
     * @return boolean value to indicate  whether this connection is usable or not.
     */

    private boolean testConnection(Connection conn) {

        try {

            // testTable is set or not.

            if (testTable.equals("")) {

                conn.setAutoCommit(true);

            } else {

                Statement stmt = conn.createStatement();

                stmt.execute("select count(*) from " + testTable);

            }

        } catch (SQLException e) {

            closeConnection(conn);

            return false;

        }

        return true;

    }

    /**
     * release one connection back to the pool, and set it as not busy.
     *
     * @param conn
     */

    public void releaseConnection(Connection conn) {

        // If the connection pool doesn't exist, return immediately.

        if (connections == null) {

            System.out.println(" The connection pool doesn't exist.");

            return;

        }

        PooledConnection pConn = null;

        Enumeration enumerate = connections.elements();

        // Find the connection in the pool,and set it as not busy.

        while (enumerate.hasMoreElements()) {

            pConn = (PooledConnection) enumerate.nextElement();

            if (conn == pConn.getConnection()) {

                pConn.setBusy(false);

                break;

            }

        }

    }

    /**
     * Refresh connections in the pool.
     */

    public synchronized void refreshConnections() throws SQLException {

        // If the connection pool doesn't exist, return immediately.

        if (connections == null) {

            System.out.println(" The connection pool doesn't exist.");

            return;

        }

        PooledConnection pConn = null;

        Enumeration enumerate = connections.elements();

        while (enumerate.hasMoreElements()) {

            pConn = (PooledConnection) enumerate.nextElement();

            if (pConn.isBusy()) {

                wait(5000); // wait 5 seconds.

            }

            // Close current connection, then create a new one and replace it.

            closeConnection(pConn.getConnection());

            pConn.setConnection(newConnection());

            pConn.setBusy(false);

        }

    }

    /**
     * Close all connections in the pool, and clear the pool.
     */

    public synchronized void closeConnectionPool() throws SQLException {


        // If the connection pool doesn't exist, return immediately.

        if (connections == null) {

            System.out.println(" The connection pool doesn't exist.");

            return;

        }

        PooledConnection pConn = null;

        Enumeration enumerate = connections.elements();

        while (enumerate.hasMoreElements()) {

            pConn = (PooledConnection) enumerate.nextElement();

            if (pConn.isBusy()) {

                wait(5000); // wait 5 seconds.

            }

            closeConnection(pConn.getConnection());

            connections.removeElement(pConn);

        }

        connections = null;

    }

    /**
     * Close one DB conenction
     *
     * @param conn
     */

    private void closeConnection(Connection conn) {

        try {

            conn.close();

        } catch (SQLException e) {

            System.out.println(" Encounter errors when closing connection ： " + e.getMessage());

        }

    }

    /**
     * @param mSeconds
     */

    private void wait(int mSeconds) {

        try {

            Thread.sleep(mSeconds);

        } catch (InterruptedException e) {
            System.out.println(" Encounter errors when calling Thread.sleep(" + mSeconds + ")" + e.getMessage());
        }

    }

    /**
     * Inner class
     */

    class PooledConnection {

        Connection connection = null;// DB connection

        boolean busy = false; // Busy flag

        public PooledConnection(Connection connection) {

            this.connection = connection;

        }

        public Connection getConnection() {

            return connection;

        }

        public void setConnection(Connection connection) {

            this.connection = connection;

        }


        public boolean isBusy() {

            return busy;

        }

        public void setBusy(boolean busy) {

            this.busy = busy;

        }

    }

    public int getInitialConnections() {
        return initialConnections;
    }

    public void setInitialConnections(int initialConnections) {
        this.initialConnections = initialConnections;
    }

    public String getTestTable() {
        return testTable;
    }

    public void setTestTable(String testTable) {
        this.testTable = testTable;
    }

    public int getIncrementalConnections() {
        return incrementalConnections;
    }

    public void setIncrementalConnections(int incrementalConnections) {
        this.incrementalConnections = incrementalConnections;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public Vector getConnections() {
        return connections;
    }

    public void setConnections(Vector connections) {
        this.connections = connections;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }


    public static void main(String[] args) throws Exception {
        ConnectionPool pool = ConnectionPool.getPoolInstance();
        Connection conn = pool.getConnection();
        String sql = "SELECT * FROM fin_test";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("+++++++++++++++++++++++Ye++++++++++++++++++++++++++++++++++++++");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
