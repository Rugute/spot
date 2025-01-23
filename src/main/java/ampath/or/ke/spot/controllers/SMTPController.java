package ampath.or.ke.spot.controllers;

import ampath.or.ke.spot.models.Programs;
import ampath.or.ke.spot.models.SMTPServer;
import ampath.or.ke.spot.models.User;
import ampath.or.ke.spot.services.RoleService;
import ampath.or.ke.spot.services.SMTPServerService;
import ampath.or.ke.spot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Controller
@RequestMapping("/mailserver")
public class SMTPController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SMTPServerService smtpServerService;


    @RequestMapping(value="/smtpserver", method = RequestMethod.GET)
    public ModelAndView EmployeesDetails(HttpSession session){
        if (session.getAttribute("company") != null && session.getAttribute("user") != null) {
            Programs programdetails = (Programs) session.getAttribute("companydetails");
            User userdetails = (User) session.getAttribute("user");
            Date nowDate = new Date();
            ModelAndView modelAndView = new ModelAndView();
           // modelAndView.addObject("allsections", sectionService.getAllSection());
            modelAndView.addObject("smtpserver",new SMTPServer());
            modelAndView.addObject("allSMTPServer", smtpServerService.getAllSMTPServer());
            modelAndView.setViewName("admin/smtp");
            return modelAndView;
        }
        else{
            return new ModelAndView("redirect:/setup");
        }
    }
    @RequestMapping(value="/addsmtpserver", method = RequestMethod.POST)
    public ModelAndView AddDetails(HttpSession session, @Valid SMTPServer smtpServer , BindingResult bindingResult){
        if (session.getAttribute("company") != null && session.getAttribute("user") != null) {
            Programs programdetails  = (Programs) session.getAttribute("companydetails");
            User userdetails = (User) session.getAttribute("user");
            Date nowDate = new Date();
            ModelAndView modelAndView = new ModelAndView();
            smtpServerService.saveSMTPServer(smtpServer);
            modelAndView.addObject("smtpserver",new SMTPServer());
            modelAndView.addObject("allSMTPServer", smtpServerService.getAllSMTPServer());
            modelAndView.setViewName("admin/smtp");
            return modelAndView;
        }
        else{
            return new ModelAndView("redirect:/setup");
        }

    }
    @RequestMapping(value="/testsmtpserver", method = RequestMethod.POST)
    public ModelAndView TestServer(HttpSession session, @RequestParam(required=false, value="lhost") String host,
                                   @RequestParam(required=false, value="emaill") String to,
                                   @RequestParam(required=false, value="econtent") String content ) throws MessagingException, UnsupportedEncodingException {
        if (session.getAttribute("company") != null && session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            SMTPServer smtpServers = smtpServerService.getByHost(host);
            System.out.print(" Host ndo hii "+host);
            if(smtpServers !=null) {
                String subject = "IOMS SMTP SERVER TEST ";
                String message = "Dear User" +
                        ""+ "\r\n";
                message += "\r\n";
                message +="Thank you for applying to the [Job_title] position at AMPATH."+ "\r\n";
                message += "\r\n";
                message += "I’d like to inform you that we received your application and resume. Our recruitment team is currently reviewing all applications and planning to schedule interviews for qualified candidates, we will keep you posted on the status of your application.";
                message += "\r\n";
                message += "\r\n";
                message += "Regards"+ "\r\n";
                message += "Recruitments Team"+ "\r\n";
                ApplicationMailer.sendMail(to, subject, message, smtpServers);
            }

            modelAndView.addObject("smtpserver",new SMTPServer());
            modelAndView.addObject("allSMTPServer", smtpServerService.getAllSMTPServer());
            modelAndView.setViewName("admin/smtp");
            return modelAndView;
        }
        else{
            return new ModelAndView("redirect:/setup");
        }

    }
}
