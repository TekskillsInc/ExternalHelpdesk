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

public class UserReport {
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
			//String statusTable =  getDayWiseTicketReport(sqlpreviousDate);
			//String categoryTable = getDayWisecategoryReport(sqlpreviousDate);
			String userTable = getDayWiseUserReport(sqlpreviousDate);
			String categoryTable="",statusTable="";
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
		String beforeDay = new SimpleDateFormat("dd/MM/yyyy").format(CurrentDate);
		
		try {
			DbConnection db = new DbConnection();
			String sql_open = "select count(1) as opencount  from Tbl_Ticket_Details where statusid=1";
			String sql_closed = "select count(1) as closedcount  from Tbl_Ticket_Details where statusid=4";
			String sql_wip = "select count(1) as wipcount  from Tbl_Ticket_Details where statusid in (2,10,3)";
			String sql_tr = "select count(1) as trcount  from Tbl_Ticket_Details where statusid=7";
			String sql_reqClarity = "select count(1) as reqClaritycount  from Tbl_Ticket_Details where statusid=9";
			/*String sql_canceled = "select count(1) as cancelledcount  from Tbl_Ticket_Details where statusid=8";*/
			String sql_total = "select count(1) as total  from Tbl_Ticket_Details where statusid in (1,2,4,7,9,10,3)";
			String sql_Todayclosed = "select count(1) as Todayclosedcount  from Tbl_Ticket_Details where statusid=4 and convert(date,raisedDate)=convert(date,getdate())";

			ResultSet rs_open = db.executeQuery(sql_open);
			ResultSet rs_closed = db.executeQuery(sql_closed);
			ResultSet rs_wip = db.executeQuery(sql_wip);
			ResultSet rs_tr = db.executeQuery(sql_tr);
			ResultSet rs_reqClarity = db.executeQuery(sql_reqClarity);
			//ResultSet rs_canceled = db.executeQuery(sql_canceled);
			ResultSet rs_total = db.executeQuery(sql_total);
			ResultSet Todayclosedcount = db.executeQuery(sql_Todayclosed);

			int opencount = 0, closedcount = 0, wipcount = 0, trcount = 0, reqClaritycount = 0, canceledcount = 0,
					total = 0,todaycount = 0;

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

	
	public static String getDayWiseUserReport(Date CurrentDate) {
		Map<Integer, String> getUserList = getUserList();
		String html = null;
		StringBuilder buf = new StringBuilder();
		String username = null;
		buf.append(
				"<html><body><table cellpadding='5' cellspacing='0' border='1'><tr><th>Category</th><th>Subcategory</th>");
		for (Map.Entry<Integer, String> entry : getUserList.entrySet()) {
			username = entry.getValue();
			buf.append("<th>").append(username).append("</th>");

		}
		buf.append("<th>").append("Total").append("</th>");

		int sumopencount = 0, sumtotal = 0;int grandtotal = 0;
		try {
			DbConnection db = new DbConnection();
			

				for (Map.Entry<Integer, String> entry : getUserList.entrySet()) {
					String sqlCategory = "SELECT distinct c.category_id as categoryid,c.category_name as categoryName,s.subcategoryId as subCategoryId,s.subcategoryName as subCategoryName	from TBL_Hpcl_Category c with(nolock)  inner join Tbl_Hpcl_SubCategories  s  WITH(NOLOCK)  on c.category_id=s.category_id inner join Tbl_Ticket_Details t on t.subcategoryId=s.subcategoryId";
					ResultSet rs = db.executeQuery(sqlCategory);
					while (rs.next()) {

						String categoryName = rs.getString("categoryName");
						String subcategoryName = rs.getString("subCategoryName");
						int categoryid = rs.getInt("categoryid");
						
						int subcategoryid = rs.getInt("subCategoryId");
					int userid = entry.getKey();
					String sql_user_own = "select count(1) as ownedcount  from Tbl_Ticket_Details t left join Tbl_Hpcl_AssignHistory a on t.tblpk=a.TicketId  where a.status=2 and subcategoryId=" + subcategoryid + " and a.AssignedTo=" + userid + "";

					ResultSet rsusrOwn = db.executeQuery(sql_user_own);
					int opencount = 0;

					if (rsusrOwn.next()) {
						opencount = rsusrOwn.getInt("ownedcount");
						sumopencount = sumopencount + opencount;
					}else {
						opencount = 0;
						sumopencount =0;
					}
					sumtotal = sumtotal + opencount;
					grandtotal = grandtotal + sumtotal;
			
					buf.append("<tr><td>").append(categoryName).append("</td><td>").append(subcategoryName)
							.append("</td><td>").append(opencount).append("</td><td>").append(sumopencount).append("</td></tr>");

				}
			
				}
				buf.append("<tr><td>").append("GrandTotal").append("</td><td>").append("--");
				buf.append("</td><td>").append(sumtotal).append("</td><td>").append(grandtotal).append("</td></tr>");
				buf.append("</table>" + "</body>" + "</html>");
			html = buf.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}

	private static Map<Integer, String> getUserList() {
		DbConnection db = new DbConnection();

		Map<Integer, String> map = new HashMap<Integer, String>();
		try {
			String userQuery = "select userid as userid,name as userName  from Tbl_Hpcl_Hdesk_Users where roleId=3 and status='Y'";
			ResultSet rsusr = db.executeQuery(userQuery);
			while (rsusr.next()) {
				int userid = rsusr.getInt("userid");
				String userName = rsusr.getString("userName");
				map.put(userid, userName);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
