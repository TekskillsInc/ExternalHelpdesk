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

public class CategoryReport {
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
			String categoryTable = getDayWisecategoryReport(sqlpreviousDate);
			while (rsmail.next()) {
				String mail = rsmail.getString("mailId");
				SendMail sendmail = new SendMail();
				String subject = "TICKET CATEGORY REPORT";
				sendmail.CategorySendMail(from_mail, mail, subject, categoryTable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static String getDayWisecategoryReport(Date CurrentDate) {
		String html = null;
		StringBuilder buf = new StringBuilder();

		buf.append("<html><body><table cellpadding='5' cellspacing='0' border='1'>"

				+ "<tr><th>Category</th><th>Subcategory</th><th>OPEN</th><th>CLOSED Today</th><th>WIP</th><th>REQUIRE CLARITY</th><th>CANCELLED</th><th>TOTAL PENDING</th><th>CommunicateTO</th></tr>");

		try {
			DbConnection db = new DbConnection();
			String sqlCategory = "SELECT distinct c.category_id as categoryid,c.category_name as categoryName,s.subcategoryId as subCategoryId,s.subcategoryName as subCategoryName,s.communicatTo as communicatTo	from TBL_Hpcl_Category c with(nolock)  inner join Tbl_Hpcl_SubCategories  s  WITH(NOLOCK)  on c.category_id=s.category_id inner join Tbl_Ticket_Details t on t.subcategoryId=s.subcategoryId";
			
			ResultSet rs = db.executeQuery(sqlCategory);
			int sumopencount = 0, sumclosedcount = 0, sumwipcount = 0, sumreqClaritycount = 0, sumcanceledcount = 0,
					sumtotal = 0,todaycount = 0;

			while (rs.next()) {

				String categoryName = rs.getString("categoryName");
				String subcategoryName = rs.getString("subCategoryName");
				int categoryid = rs.getInt("categoryid");
				String communicatTo= rs.getString("communicatTo");
				int subcategoryid = rs.getInt("subCategoryId");
				String sql_communicate = "select  u.name as communicate from Tbl_Hpcl_Hdesk_Users u where u.loginid='"+communicatTo+"'";
				
				String sql_open = "select count(1) as opencount  from Tbl_Ticket_Details where statusid=1 and subcategoryId=" + subcategoryid + "";
				String sql_closed = "select count(1) as closedcount  from Tbl_Ticket_Details where statusid=4 and subcategoryId=" + subcategoryid + "";
				String sql_wip = "select count(1) as wipcount  from Tbl_Ticket_Details where statusid not in (1,4,5,8) and subcategoryId=" + subcategoryid + "";

				String sql_reqClarity = "select count(1) as reqClaritycount  from Tbl_Ticket_Details where statusid=9 and subcategoryId=" + subcategoryid + "";
				String sql_canceled = "select count(1) as canceledcount  from Tbl_Ticket_Details where statusid=8 and subcategoryId=" + subcategoryid + "";
				String sql_total = "select count(1) as total  from Tbl_Ticket_Details where statusid in (1,2,4,7,8,9,10,3) and subcategoryId=" + subcategoryid + "";
				//String sql_Todayclosed = "select count(1) as Todayclosedcount  from Tbl_Ticket_Details where statusid=4 and subcategoryId=" + subcategoryid + " and convert(date,raisedDate)=convert(date,getdate())";
				String sql_Todayclosed ="SELECT count(1) as Todayclosedcount FROM Tbl_Hpcl_AssignHistory a,Tbl_Ticket_Details t where t.tblpk=a.TicketId and t.statusid=4 and subcategoryId=" + subcategoryid + " and CONVERT(date,a.statusUpdatedOn)=CONVERT(date,getdate())";
				
				ResultSet rs_open = db.executeQuery(sql_open);
				ResultSet rs_closed = db.executeQuery(sql_closed);
				ResultSet rs_wip = db.executeQuery(sql_wip);
				ResultSet rs_Todayclosedcount = db.executeQuery(sql_Todayclosed);
				ResultSet rs_reqClarity = db.executeQuery(sql_reqClarity);
				ResultSet rs_canceled = db.executeQuery(sql_canceled);
				ResultSet rs_communicate = db.executeQuery(sql_communicate);
				//ResultSet rs_total = db.executeQuery(sql_total);
				String communicateTo="";
				int opencount = 0, closedcount = 0, wipcount = 0, reqClaritycount = 0, canceledcount = 0, total = 0;
				if (rs_communicate.next()) {
					communicateTo= rs_communicate.getString("communicate");
					}
				if (rs_open.next()) {
					opencount = rs_open.getInt("opencount");
					sumopencount = sumopencount + opencount;
				}
				if (rs_closed.next()) {
					closedcount = rs_closed.getInt("closedcount");
					
				}
				if (rs_wip.next()) {
					wipcount = rs_wip.getInt("wipcount");
					sumwipcount = sumwipcount + wipcount;
				}
				if (rs_reqClarity.next()) {
					reqClaritycount = rs_reqClarity.getInt("reqClaritycount");
					sumreqClaritycount = sumreqClaritycount + reqClaritycount;
				}
				if (rs_canceled.next()) {
					canceledcount = rs_canceled.getInt("canceledcount");
					sumcanceledcount = sumcanceledcount + canceledcount;
				}
				
				total =opencount+wipcount+reqClaritycount;
				sumtotal = sumtotal + total;
				if (rs_Todayclosedcount.next()) {
					todaycount = rs_Todayclosedcount.getInt("Todayclosedcount");
					sumclosedcount = sumclosedcount + todaycount;
				}
				     buf.append("<tr><td>").append(categoryName).append("</td><td>").append(subcategoryName)
						.append("</td><td>").append(opencount).append("</td><td>").append(todaycount)
						.append("</td><td>").append(wipcount).append("</td><td>").append(reqClaritycount)
						.append("</td><td>").append(canceledcount).append("</td><td>").append(total)
						.append("</td><td>").append(communicateTo)			
						.append("</td></tr>");

			}
			buf.append("<tr><td>").append("GrandTotal").append("</td><td>").append("--").append("</td><td>")
					.append(sumopencount).append("</td><td>").append(sumclosedcount).append("</td><td>")
					.append(sumwipcount).append("</td><td>").append(sumreqClaritycount).append("</td><td>")
					.append(sumcanceledcount).append("</td><td>").append(sumtotal).append("</td></tr>");

			buf.append("</table>" + "</body>" + "</html>");
			html = buf.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	
}
