
package controllers.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ManagerService;
import services.RendeService;
import services.UserService;
import controllers.AbstractController;
import domain.Manager;
import domain.User;

@Controller
@RequestMapping("/user/manager")
public class UserManagerController extends AbstractController {

	//Autowired
	@Autowired
	ManagerService		managerService;
	
	@Autowired
	UserService		userService;

	@Autowired
	RendeService	rendeService;


	//Constructor
	public UserManagerController() {
		super();
	}

	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int userId) {
		final ModelAndView result;
		User user;
		final Manager principal = this.managerService.findByPrincipal();
		final String uri = "/user";
		final Boolean viewAttendants = false;

		user = this.userService.findOne(userId);

		result = new ModelAndView("user/display");
		result.addObject("user", user);
		result.addObject("principal", principal);
		result.addObject("viewAttendants", viewAttendants);
		result.addObject("uri", uri);
		return result;

	}

}