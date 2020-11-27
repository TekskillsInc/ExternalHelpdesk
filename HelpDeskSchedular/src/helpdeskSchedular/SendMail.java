package helpdeskSchedular;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	public void dayWiseSendMail(String from_mail, String tomail, String subject,String statusTable) throws IOException {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			prop.load(input);
			 String mailhost = prop.getProperty("mailhost");
			 String port = prop.getProperty("port");
			 
			java.util.Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", mailhost);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "false");
			 try
		     {
		
			Session session1 = Session.getDefaultInstance(properties, null);

			MimeMessage message = new MimeMessage(session1);
			message.setFrom(new InternetAddress(from_mail));
			if (tomail != null) {
				message.setRecipients(Message.RecipientType.TO, tomail);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("Hi,");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Status Report");
			sb.append("<br>");
			sb.append("<br>");
			sb.append(statusTable);
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Thanks & Regards");
			sb.append("<br>");
			sb.append("HPCL");
			
			message.setSubject(subject);
			message.setContent(sb.toString(),"text/html"); 
			Transport.send(message);
		     }
		     catch (AddressException e) {
		         System.out.println("AddressException raised while sending ----"+e);

		     } catch (SendFailedException e) {
		    	 System.out.println("SendFailedException raised while sending ----"+e);

		     } catch (Exception e) {
		    	 System.out.println("Logic Exception raised while sending ----"+e);
		     }  
	}
	
	
	public void CategorySendMail(String from_mail, String tomail, String subject, String categoryTable) throws IOException {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			prop.load(input);
			 String mailhost = prop.getProperty("mailhost");
			
			 String port = prop.getProperty("port");
			 
			java.util.Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", mailhost);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "false");
			 try
		     {
			Session session1 = Session.getDefaultInstance(properties,null);

			MimeMessage message = new MimeMessage(session1);
			message.setFrom(new InternetAddress(from_mail));
			if (tomail != null) {
				message.setRecipients(Message.RecipientType.TO, tomail);
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Hi,");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Category Report");
			sb.append("<br>");
			sb.append("<br>");
			sb.append(categoryTable);
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Thanks & Regards");
			sb.append("<br>");
			sb.append("HPCL");
			
			message.setSubject(subject);
			message.setContent(sb.toString(),"text/html"); 
			Transport.send(message);
		     }
		     catch (AddressException e) {
		         System.out.println("AddressException raised while sending ----"+e);

		     } catch (SendFailedException e) {
		    	 System.out.println("SendFailedException raised while sending ----"+e);

		     } catch (Exception e) {
		    	 System.out.println("Logic Exception raised while sending ----"+e);
		     }  
	}
	
	public void triggerMail(String toMail1, String fromMail, String toName, String subject, String desc,
			List<String> ccMailids, String detailedDesc) throws MessagingException, IOException {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			//input = DbConnection.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			 String mailhost = prop.getProperty("mailhost");
			 String port = prop.getProperty("port");
			 
			java.util.Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", mailhost);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "false");
			 try
		     {
				 Session session1 = Session.getDefaultInstance(properties,null);

			MimeMessage message = new MimeMessage(session1);
			message.setFrom(new InternetAddress(fromMail));

			InternetAddress[] toCcList = null;
			if (toMail1 != null) {
					message.setRecipients(Message.RecipientType.TO, toMail1);
				}
			InternetAddress[] myCcList = null;
			if (ccMailids != null) {
				for (String ccmail : ccMailids) {
					if (ccmail != null) {
						myCcList = InternetAddress.parse(ccmail);
					}
					message.addRecipients(Message.RecipientType.CC, myCcList);
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Hi" + " " + toName + ",<br>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append(detailedDesc);
			sb.append("<br>");
			sb.append("<br>");
			sb.append(desc);
			sb.append("<br>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Thanks & Regards");
			sb.append("<br>");
			sb.append("HELPDESK");
			message.setSubject(subject);
			message.setContent(sb.toString(),"text/html"); 
			Transport.send(message);
		     }
		     catch (AddressException e) {
		         System.out.println("AddressException raised while sending ----"+e);

		     } catch (SendFailedException e) {
		    	 System.out.println("SendFailedException raised while sending ----"+e);

		     } catch (Exception e) {
		    	 System.out.println("Logic Exception raised while sending ----"+e);
		     }  
	}


	public void escalationMail(List<String> toMailList, String fromMail, String toName, String subject, String desc,
			List<String> ccMailids, String detailedDesc) throws IOException {
		 try
	     {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("C:\\HPCLConfig\\config.properties");
			//input = DbConnection.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			 String mailhost = prop.getProperty("mailhost");
			 String port = prop.getProperty("port");
			 
			java.util.Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", mailhost);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "false");
			
			Session session1 = Session.getDefaultInstance(properties,null);

			MimeMessage message = new MimeMessage(session1);
			message.setFrom(new InternetAddress(fromMail));

			InternetAddress[] ToList = null;
			if (toMailList != null) {
				for (String tomail : toMailList) {
					if (tomail != null) {
						ToList = InternetAddress.parse(tomail);
					}
					message.addRecipients(Message.RecipientType.TO, ToList);
				}
			}
			
			InternetAddress[] myCcList = null;
			if (ccMailids != null) {
				for (String ccmail : ccMailids) {
					if (ccmail != null) {
						myCcList = InternetAddress.parse(ccmail);
					}
					message.addRecipients(Message.RecipientType.CC, myCcList);
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Hi" + " " + toName + ",<br>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append(detailedDesc);
			sb.append("<br>");
			sb.append("<br>");
			sb.append(desc);
			sb.append("<br>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("Thanks & Regards");
			sb.append("<br>");
			sb.append("HELPDESK");
			message.setSubject(subject);
			message.setContent(sb.toString(),"text/html"); 
			Transport.send(message);
		     }
		     catch (AddressException e) {
		         System.out.println("AddressException raised while sending ----"+e);

		     } catch (SendFailedException e) {
		    	 System.out.println("SendFailedException raised while sending ----"+e);

		     } catch (Exception e) {
		    	 System.out.println("Logic Exception raised while sending ----"+e);
		     }  
		
		
	}


	public void wipNotificationMail(String from_mail, String tomail, String subject, int count, String name) throws IOException {
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream("C:\\HPCLConfig\\config.properties");
		prop.load(input);
		 String mailhost = prop.getProperty("mailhost");
		 String port = prop.getProperty("port");
		 
		java.util.Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", mailhost);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "false");
		 try
	     {
	
			 Session session1 = Session.getDefaultInstance(properties,null);

		MimeMessage message = new MimeMessage(session1);
		message.setFrom(new InternetAddress(from_mail));
		if (tomail != null) {
			message.setRecipients(Message.RecipientType.TO, tomail);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hi "+name+",");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("Good Morning,");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<b>Count of pending tickets in your bucket is:</b> "+count+"");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("Thanks & Regards");
		sb.append("<br>");
		sb.append("HPCL");
		
		message.setSubject(subject);
		message.setContent(sb.toString(),"text/html"); 
		Transport.send(message);
	     }
	     catch (AddressException e) {
	         System.out.println("AddressException raised while sending ----"+e);

	     } catch (SendFailedException e) {
	    	 System.out.println("SendFailedException raised while sending ----"+e);

	     } catch (Exception e) {
	    	 System.out.println("Logic Exception raised while sending ----"+e);
	     }  
		
	}
	
}
