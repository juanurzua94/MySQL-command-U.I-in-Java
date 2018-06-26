import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JButton;
public class BookStoreDB extends JFrame
{
	static final String URL = "jdbc:mysql://localhost:3306/bookstore";
	static final String user = "root";
	private String pw = "root";
	static final String DEFAULT_QUERY = "SELECT * FROM author";
	private ResultSetTable tableMod;
	private JTextArea queryArea;
	
	public BookStoreDB()
	{
		//Constructor for setting up the JFRAME
		super("Book Store DataBase");
		
		try
		{
			tableMod = new ResultSetTable(URL, user, pw, DEFAULT_QUERY);
			
			queryArea = new JTextArea(DEFAULT_QUERY, 3, 100);
			queryArea.setLineWrap(true);
			queryArea.setWrapStyleWord(true);
			
			JScrollPane scroll = new JScrollPane(queryArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JButton submitButton = new JButton("Submit");
			Box northBox = Box.createHorizontalBox();
			northBox.add(scroll);
			northBox.add(submitButton);
			
			JTable resultTable = new JTable(tableMod);
			resultTable.setFont(new Font("Calibri", Font.BOLD,  14));

			JLabel filterLabel = new JLabel("filter");
			final JTextField filter = new JTextField();
			JButton filterButton = new JButton("Apply Filter");
			
			Box southBox = Box.createHorizontalBox();
			
			southBox.add(filterLabel);
			southBox.add(filter);
			southBox.add(filterButton);
			
			add(northBox, BorderLayout.NORTH);
			add(new JScrollPane(resultTable), BorderLayout.CENTER);
			add(southBox, BorderLayout.SOUTH);
			//Action event when user clicks the "Submit" button. 
			//The action performed is the extraction of the text within the queryArea 
			//which should be an executable mysql statement for obtaining data from the database
			//which is then displayed in the JTable
			submitButton.addActionListener(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent event)
						{
							try
							{
								tableMod.setQuery(queryArea.getText());
							}
							catch(SQLException e)
							{
								JOptionPane.showMessageDialog(null, e.getMessage(), "DataBase Error", JOptionPane.ERROR_MESSAGE);
								try
								{
									tableMod.setQuery(DEFAULT_QUERY);
									queryArea.setText(DEFAULT_QUERY);
								}
								catch(SQLException ex)
								{
									JOptionPane.showMessageDialog(null, ex.getMessage(), "DataBase Error", JOptionPane.ERROR_MESSAGE);
									tableMod.disconnectFromDB();
									
									System.exit(ERROR);
								}
							}
						}
					}
					);
			
			//sorter is used when the user clicks on the "Filter" button. It filters
			//out the JTable to display text matching what the user inputted
			//in the "filter" text area
			
			final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableMod);
			resultTable.setRowSorter(sorter);
			
			setSize(500, 300);
			setVisible(true);
			//This is the events performed when the user clicks the "filter" button
			filterButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event)
						{
							String text = filter.getText();
							if(text.length() == 0)
								sorter.setRowFilter(null);
							else
							{
								try
								{
									sorter.setRowFilter(RowFilter.regexFilter(text));
								}
								catch(Exception e)
								{
									JOptionPane.showMessageDialog(null, e.getMessage(), "DataBase Error", JOptionPane.ERROR_MESSAGE);
									
								}
							}
						}
					}
					);
			
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "DataBase Error", JOptionPane.ERROR_MESSAGE);
			tableMod.disconnectFromDB();
			
			System.exit(ERROR);
		}
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// When the user closes the window, the database connection is destroyed
		addWindowListener(
				
				new WindowAdapter()
				{
					public void windowClosed( WindowEvent event)
					{
						tableMod.disconnectFromDB();
						System.exit(ERROR);
					}
				}
				
				);
		
		
	}
	public static void main(String[] args)
	{
		//Use a different l&f for the display
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    	if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		          
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		new BookStoreDB();
	}
}
