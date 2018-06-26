import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.Statement;

//ResultSetTable extends AbstractTableModel to enhance Table capabilities.
public class ResultSetTable extends AbstractTableModel
{
	private Connection connection;
	private Statement statement;
	private Statement insert;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	
	private boolean connected = false;
	
	//Create connection with the database and create Statment used throughout program
	public ResultSetTable(String URL, String user, String pw, String query) throws SQLException
	{
		connection = DriverManager.getConnection(URL, user, pw);
		
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		connected = true;
		
		setQuery(query);
		
	}
	//Get the class name of the type for the column names
	public Class getColumnClass(int column) throws IllegalStateException
	{
		if(!connected)
			throw new IllegalStateException("Not connected to DataBase");
		
		try {
			String className = metaData.getColumnClassName(column + 1);
			
			return Class.forName(className);
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		
		return Object.class;
	}
	
	//Obtains the number of columns through a MetaData object
	public int getColumnCount() throws IllegalStateException
	{
		if (!connected)
			throw new IllegalStateException("Not connected to DataBase");
		
		try 
		{
			return metaData.getColumnCount();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	// gets the name of the column
	public String getColumnName(int column) throws IllegalStateException
	{
		if (!connected)
			throw new IllegalStateException("Not connected to DataBase");
		
		try 
		{
			return metaData.getColumnName(column + 1);
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
		}
		return "";
	}
	// gets the number of rows 
	public int getRowCount() throws IllegalStateException
	{
		if (!connected)
			throw new IllegalStateException("Not connected to DataBase");
		return numberOfRows;
		
	}
	// returns the object located at a specifc row/column
	public Object getValueAt(int row, int col) throws IllegalStateException
	{
		if (!connected)
			throw new IllegalStateException("Not connected to DataBase");
		try 
		{
			resultSet.absolute(row + 1);
			return resultSet.getObject(col + 1);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return "";
	}
	//This function executes the query typed in by the user
	public void setQuery(String query) throws SQLException, IllegalStateException
	{
		if (!connected)
			throw new IllegalStateException("Not connected to DataBase");
		if (query.contains("INSERT") || query.contains("insert"))
		{
			statement.executeUpdate(query);
		}
		else
		{
			resultSet = statement.executeQuery(query);
		
			metaData = resultSet.getMetaData();
		
			resultSet.last();
		
			numberOfRows = resultSet.getRow();
		
			fireTableStructureChanged();
		}
	}
	//

	// Disconnects program from the database.
	public void disconnectFromDB()
	{
		if (connected)
		{
			try 
			{
				connection.close();
				statement.close();
				resultSet.close();
				
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				connected = false;
			}
		}
	}

}
