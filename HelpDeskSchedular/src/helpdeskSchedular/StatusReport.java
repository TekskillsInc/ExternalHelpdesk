package helpdeskSchedular;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StatusReport {
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
	public final static long MILLIS_DOUBLE_DAY = 60 * 60 * 60 * 1000L;

	public static void main(String args[]) throws SQLException, Exception {
		FileInputStream input = null;
		try {
			Properties prop = new Properties();
			 input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			prop.load(input);
			String from_mail = prop.getProperty("fromMail");
			java.util.Date utilDate = new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
			//java.util.Date utilDate = new Date();
			java.sql.Date sqlpreviousDate = new java.sql.Date(utilDate.getTime());
			String mailSql = "select * from Tbl_Hpcl_MailRecipients where mailId not in ('')";
			DbConnection db = new DbConnection();
			ResultSet rsmail = db.executeQuery(mailSql);
			String statusTable =  getDayWiseTicketReport(sqlpreviousDate);
			while (rsmail.next()) {
				String mail = rsmail.getString("mailId");
				SendMail sendmail = new SendMail();
				String subject = "TICKET STATUS REPORT";
				sendmail.dayWiseSendMail(from_mail, mail, subject,statusTable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  String getDayWiseTicketReport(Date CurrentDate) {
		String statusTable =null;
		try {
			DbConnection db = new DbConnection();
			String sql_open = "select count(1) as opencount  from Tbl_Ticket_Details where statusid=1";
			String sql_closed = "select count(1) as closedcount  from Tbl_Ticket_Details where statusid=4";
			String sql_wip = "select count(1) as wipcount  from Tbl_Ticket_Details where statusid in (2,10,3)";
			String sql_tr = "select count(1) as trcount  from Tbl_Ticket_Details where statusid=7";
			String sql_reqClarity = "select count(1) as reqClaritycount  from Tbl_Ticket_Details where statusid=9";
			/*String sql_canceled = "select count(1) as cancelledcount  from Tbl_Ticket_Details where statusid=8";*/
			String sql_total = "select count(1) as total  from Tbl_Ticket_Details where statusid in (1,2,4,7,9,10,3)";
			String sql_Todayclosed ="SELECT count(1) as Todayclosedcount FROM Tbl_Hpcl_AssignHistory a,Tbl_Ticket_Details t where t.tblpk=a.TicketId and t.statusid=4 and CONVERT(date,a.statusUpdatedOn)=CONVERT(date,getdate())";

			ResultSet rs_open = db.executeQuery(sql_open);
			ResultSet rs_closed = db.executeQuery(sql_closed);
			ResultSet rs_wip = db.executeQuery(sql_wip);
			ResultSet rs_tr = db.executeQuery(sql_tr);
			ResultSet rs_reqClarity = db.executeQuery(sql_reqClarity);
			//ResultSet rs_canceled = db.executeQuery(sql_canceled);
			ResultSet rs_total = db.executeQuery(sql_total);
			ResultSet Todayclosedcount = db.executeQuery(sql_Todayclosed);

			int opencount = 0, closedcount = 0, wipcount = 0, trcount = 0, reqClaritycount = 0, canceledcount = 0,	total = 0,todaycount = 0;
			if (rs_open.next()) {
				opencount = rs_open.getInt("opencount");
			}
			if (rs_closed.next()) {
				closedcount = rs_closed.getInt("closedcount");
			}
			if (rs_wip.next()) {
				wipcount = rs_wip.getInt("wipcount");
			}
			if (rs_tr.next()) {
				trcount = rs_tr.getInt("trcount");
			}
			if (rs_reqClarity.next()) {
				reqClaritycount = rs_reqClarity.getInt("reqClaritycount");
			}
			if (rs_total.next()) {
				total = rs_total.getInt("total");
			}
			if (Todayclosedcount.next()) {
				todaycount = Todayclosedcount.getInt("Todayclosedcount");
			}
			 statusTable = "<html><body><table cellpadding='5' cellspacing='0' border='1'><tr><th>DATE</th><th>OPEN</th><th>CLOSED</th><th>WIP</th><th>REQUIRE CLARITY</th><th>TOTAL</th><th font='color:blue'>Closed Today</th></tr><tr><td>"
					+ "Till Today" + "</td><td>" + opencount + "</td><td>" + closedcount + "</td><td>" + wipcount + "</td><td>"
					+ reqClaritycount + "</td><td>"+ total+"</td><td>"+ todaycount+"</td></tr></table></body></html>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusTable;
	}
		
}
