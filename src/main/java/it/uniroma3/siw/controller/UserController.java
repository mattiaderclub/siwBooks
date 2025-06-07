package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.dto.UpdateUserDTO;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/utente/{id}")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@GetMapping("/profilo")
	public String mostraProfilo(@PathVariable("id") Long id, Model model) {
	    Credentials cred = getCredentialsLoggate();

	    if (cred == null || cred.getUser() == null || !cred.getUser().getId().equals(id)) {
	        return "redirect:/login";
	    }

	    model.addAttribute("utente", cred.getUser());
	    model.addAttribute("credentials", cred);

	    return "utente/profilo";
	}
	
	@GetMapping("/profilo/modifica")
	public String formUpdateProfilo(@PathVariable("id") Long id, Model model) {
	    Credentials cred = getCredentialsLoggate();

	    if (cred == null || cred.getUser() == null || !cred.getUser().getId().equals(id)) {
	        return "redirect:/login";
	    }

	    User user = cred.getUser();
	    UpdateUserDTO dto = new UpdateUserDTO();
	    dto.setName(user.getName());
	    dto.setSurname(user.getSurname());
	    dto.setCell(user.getCell());

	    model.addAttribute("updateUserDTO", dto);
	    model.addAttribute("credentials", cred);
	    return "utente/formModificaProfilo";
	}
	
	@PostMapping("/profilo/modifica")
	public String modificaProfilo(@PathVariable("id") Long id,
	                              @Valid @ModelAttribute("updateUserDTO") UpdateUserDTO updateUserDTO,
	                              BindingResult bindingResult,
	                              @RequestParam("newPassword") String newPassword,
	                              @RequestParam("confirmNewPassword") String confirmNewPassword,
	                              Model model) {

	    Credentials cred = getCredentialsLoggate();

	    if (cred == null || cred.getUser() == null || !cred.getUser().getId().equals(id)) {
	        return "redirect:/login";
	    }

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("credentials", cred);
	        return "utente/formModificaProfilo";
	    }

	    User user = cred.getUser();
	    user.setName(updateUserDTO.getName());
	    user.setSurname(updateUserDTO.getSurname());
	    user.setCell(updateUserDTO.getCell());

	    // Gestione password
	    if (newPassword != null && !newPassword.isEmpty()) {
	        if (!newPassword.equals(confirmNewPassword)) {
	            model.addAttribute("passwordError", "Le password non coincidono");
	            model.addAttribute("credentials", cred);
	            return "utente/formModificaProfilo";
	        }
	        cred.setPassword(newPassword);
	        credentialsService.saveCredentials(cred);
	    } else {
	        credentialsService.saveWithoutEncoding(cred);
	    }

	    userService.saveUser(user);

	    return "redirect:/utente/" + id + "/profilo";
	}

    private Credentials getCredentialsLoggate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return credentialsService.getCredentials(userDetails.getUsername());
    }
}
