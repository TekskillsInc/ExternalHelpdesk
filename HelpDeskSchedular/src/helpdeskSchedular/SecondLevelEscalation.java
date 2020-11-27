package helpdeskSchedular;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SecondLevelEscalation {
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
	public final static long MILLIS_DOUBLE_DAY = 60 * 60 * 60 * 1000L;

	public static void main(String args[]) throws SQLException, Exception {
		try {
			FileInputStream input = null;
			Properties prop = new Properties();
			input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			prop.load(input);
			DbConnection db = new DbConnection();
			String hpcl_mail = prop.getProperty("hpcl_mail");
			String desc=null;
			SendMail sendmail = new SendMail();
			String toName = "Team";
			String subject = "2nd Level Ticket Escalation";
			String emailid =null;
			List<String> toMailList =  new ArrayList<String>();
			String fromMail = hpcl_mail;
			ResultSet rs1=db.executeQuery("select emailid from Tbl_Hpcl_Hdesk_Users where roleId=1 and status='Y'");
			while (rs1.next()) {
				emailid = rs1.getString("emailid");
				toMailList.add(emailid);
			}
			
			List<String> ccMailids = new ArrayList<String>();
			ccMailids.add(prop.getProperty("AshishEmail"));
			ccMailids.add(prop.getProperty("AjayEmail"));
			
			String AshishEmail=prop.getProperty("AshishEmail");
			String AjayEmail=prop.getProperty("AjayEmail");
			
	/*		if(AshishEmail!=null) {ccMailids.add(prop.getProperty("AshishEmail"));}
			else {ccMailids.add("ashishsingh@hpcl.in");}
			
			if(AjayEmail!=null) {ccMailids.add(prop.getProperty("AjayEmail"));}
			else {ccMailids.add("ajay.samudra@hpcl.in");}*/
			
			String detailedDesc="Escalation has been raised for ticket not closed in specified time, Please find below details";
			ResultSet rs = db.executeQuery("Exec MailEscalations");
			StringBuilder buf = new StringBuilder();
			//cellpadding='5' 
			buf.append("<html><body><table cellspacing='0' border='1'><tr><th>Ticket number</th><th>Category</th><th>Sub-Category</th><th>Description</th><th>Logged on</th><th>Time for closure(In hours)</th><th>Timeline exceeded by(In hours)</th><th>Responsible person @ Helpdesk</th><th>Responsible person @ Data Center</th></tr>");
			while (rs.next()) {
				int ticketId = rs.getInt("ticketId");
				String categoryName = rs.getString("categoryName");
				String subcategoryName = rs.getString("subcategoryName");
				String tktownedby = rs.getString("tktownedby");
				String contactToName = rs.getString("contactToName");
				String tktDescription = rs.getString("tktDescription");
				String tktRaisedOn = rs.getString("tktRaisedOn");
				int resolutiontime = rs.getInt("resolutiontime");
				int timelineExceeded1 = rs.getInt("timelineExceeded");
				
				int timelineExceeded=0;
				if(timelineExceeded1<resolutiontime) {
					timelineExceeded=0;
				}else {
					timelineExceeded=timelineExceeded1;
				}			 
				buf.append("<tr><td>").append(ticketId).append("</td><td>").append(categoryName)
				.append("</td><td>").append(subcategoryName).append("</td><td>").append(tktDescription)
				.append("</td><td>").append(tktRaisedOn).append("</td><td>").append(resolutiontime)
				.append("</td><td>").append(timelineExceeded).append("</td><td>").append(tktownedby)
				.append("</td><td>").append(contactToName).append("</td></tr>");
			}
			buf.append("</table>" + "</body>" + "</html>");
			desc = buf.toString();
			sendmail.escalationMail(toMailList, fromMail, toName, subject, desc, ccMailids, detailedDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
