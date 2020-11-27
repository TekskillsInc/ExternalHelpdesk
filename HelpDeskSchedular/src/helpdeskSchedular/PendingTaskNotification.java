package helpdeskSchedular;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class PendingTaskNotification {
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
	public final static long MILLIS_DOUBLE_DAY = 60 * 60 * 60 * 1000L;

	public static void main(String args[]) throws SQLException, Exception {
		FileInputStream input = null;
		try {
			Properties prop = new Properties();
			input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			prop.load(input);
			String from_mail = prop.getProperty("hpcl_mail");
			String mailSql = "select * from Tbl_Hpcl_Hdesk_Users where status='Y' and roleId in (1,3,7)";
			DbConnection db = new DbConnection();
			ResultSet rsmail = db.executeQuery(mailSql);
			
			while (rsmail.next()) {
				String mail = rsmail.getString("emailid");
				int userid = rsmail.getInt("userid");
				int roleId = rsmail.getInt("roleId");
				int count =  getStatusWiseCount(userid,roleId);
				String name = rsmail.getString("name");
				
				SendMail sendmail = new SendMail();
				Date cDate=new Date();
				java.sql.Date todayDate = new java.sql.Date(cDate.getTime());
				String subject = "Pending tickets "+todayDate+"";
				sendmail.wipNotificationMail(from_mail, mail, subject,count,name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  int getStatusWiseCount(int userid, int roleId) {
		int total =0;
		try {
			DbConnection db = new DbConnection();
			String sql_total = null;
			if(roleId==1) {
				 sql_total = "select count(1) as ticketcount from Tbl_Ticket_Details t join Tbl_Hpcl_AssignHistory a on t.tblpk=a.TicketId where a.TicketStatus='Active' and t.statusid in(2,9,10,11,7) and t.statusid=a.Status and a.AssignedTo="+userid+"";
			}else {
				 sql_total = "select count(1) as ticketcount from Tbl_Ticket_Details t join Tbl_Hpcl_AssignHistory a on t.tblpk=a.TicketId where a.TicketStatus='Active' and t.statusid in(2,9,10,11) and t.statusid=a.Status and a.AssignedTo="+userid+"";
			}
			
			ResultSet rs_total = db.executeQuery(sql_total);
			if (rs_total.next()) {
				total = rs_total.getInt("ticketcount");
				}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}
		
}
