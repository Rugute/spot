package ampath.or.ke.spot.controllers;

import ampath.or.ke.spot.models.KHISMapping;
import ampath.or.ke.spot.models.KashaClients;
import ampath.or.ke.spot.models.KashaDeliveries;
import ampath.or.ke.spot.models.PendulumRiskScores;
import ampath.or.ke.spot.services.KashaClientsServices;
import ampath.or.ke.spot.services.KashaDeliveriesService;
import ampath.or.ke.spot.services.PendulumRiskScoreService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/project_beyond")
public class ProjectBeyondController {

    @Autowired
    KashaDeliveriesService kashaDeliveriesService;
    @Autowired
    KashaClientsServices kashaClientsServices;
    @Autowired
    PendulumRiskScoreService pendulumRiskScoreService;
    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public ModelAndView mappings(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<KashaClients> mappingList = kashaClientsServices.getAllEligible(1);
            modelAndView.addObject("mappings", mappingList);

            modelAndView.setViewName("kasha_clients");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }

    @RequestMapping(value = "/deliveries", method = RequestMethod.GET)
    public ModelAndView deliveries(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<KashaDeliveries> mappingList = kashaDeliveriesService.getAllDataset();
            modelAndView.addObject("mappings", mappingList);

            modelAndView.setViewName("kasha_deliveries");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }
    @RequestMapping(value = "/risks", method = RequestMethod.GET)
    public ModelAndView risks(HttpSession session) throws IOException, JSONException {
        if (session.getAttribute("user") != null) {
            ModelAndView modelAndView = new ModelAndView();
            List<PendulumRiskScores> mappingList = pendulumRiskScoreService.GetALLData();
            modelAndView.addObject("mappings", mappingList);

            modelAndView.setViewName("kasha_risks");
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/auth/login");
        }

    }

}
