package com.newamerica.webserver;

import com.newamerica.webserver.model.User;
import com.newamerica.webserver.service.MailService;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class EmailController extends BaseResource {

    @Autowired
    private MailService notificationService;

    @Autowired
    private User user;

    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(FundsController.class);

    public EmailController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @PostMapping(value = "/send-email", produces = "application/json", params = "email")
    private Response sendEmail (@QueryParam("email") String email) {
        try {
            String resourcePath = String.format("/send-email?email=%s", email);
            user.setEmailAddress(email);
            notificationService.sendEmail(user);
            return Response.ok("Email sent successfully").build();
        }catch (MailException mailException) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, mailException.getMessage());
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
