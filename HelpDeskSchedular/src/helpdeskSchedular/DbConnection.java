package helpdeskSchedular;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DbConnection {

	private static String m_connectionString = null;
	private static String m_userName = null;
	private static String m_password = null;

	ResultSet rs = null;
	Connection con = null;
	Statement statement = null;

	public static void getConnectionString() {
		Properties prop = new Properties();
		FileInputStream input =null;
		try {
			 //input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			 input = new FileInputStream("C:\\HPCLConfig\\config.properties");

			//input = DbConnection.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			m_connectionString = prop.getProperty("m_connectionString");
			m_userName = prop.getProperty("m_userName");
			m_password = prop.getProperty("m_password");
		
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		Connection con = null;
		Statement statement = null;
		DbConnection.getConnectionString();
		try {

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(m_connectionString, m_userName, m_password);
			statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;

	}

	public Connection updateQuery(String sqlQuery) {

		DbConnection.getConnectionString();
		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			System.out.println("Inside ResultSet");
			con = DriverManager.getConnection(m_connectionString, m_userName, m_password);
			/*
			 * statement = con.createStatement(); String sql = null;
			 * rs=statement.executeQuery(sql);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;

	}

	public int UpdateQuery(String sqlQuery) {
		int i = 0;
		DbConnection.getConnectionString();
		Connection con = null;
		Statement statement = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(m_connectionString, m_userName, m_password);
			statement = con.createStatement();
			i = statement.executeUpdate(sqlQuery);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return i;
	}

	public Connection getConnection() {
		// DbConnection dbConnection=new DbConnection();
		DbConnection.getConnectionString();
		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(m_connectionString, m_userName, m_password);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}


}
