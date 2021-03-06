package pl.codecity.main.controller.admin.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codecity.main.exception.ServiceException;
import pl.codecity.main.model.Comment;
import pl.codecity.main.service.CommentService;
import pl.codecity.main.utility.AuthorizedUser;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;

@Controller
@RequestMapping(value="/{language}/comments/bulk-unapprove", method=RequestMethod.POST)
public class CommentBulkUnapproveController {

	@Inject
	private CommentService commentService;

	private static Logger logger = LoggerFactory.getLogger(CommentBulkUnapproveController.class);

	@ModelAttribute("query")
	public String query(@RequestParam(required = false) String query) {
		return query;
	}

	@RequestMapping
	public String unapprove(
			@Valid @ModelAttribute("form") CommentBulkUnapproveForm form,
			BindingResult errors,
			String query,
			AuthorizedUser authorizedUser,
			RedirectAttributes redirectAttributes,
			Model model) {
		redirectAttributes.addAttribute("query", query);

		if (errors.hasErrors()) {
			logger.debug("Errors: {}", errors);
			return "redirect:/_admin/{language}/comments/index";
		}

		Collection<Comment> unapprovedComments;
		try {
			unapprovedComments = commentService.bulkUnapproveComment(form.toCommentBulkUnapproveRequest(), authorizedUser);
		} catch (ServiceException e) {
			return "redirect:/_admin/{language}/comments/index";
		}

		redirectAttributes.addFlashAttribute("unapprovedComments", unapprovedComments);
		return "redirect:/_admin/{language}/comments/index";
	}
}
