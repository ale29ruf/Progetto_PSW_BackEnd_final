package com.example.progetto_psw.rest.freeController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import support.authentication.Utils;

@RestController //unione di @Controller e @ResponseBody
@RequestMapping("/check")
public class CheckController {


    @GetMapping("/simple")
    public ResponseEntity checkSimple() {
        return new ResponseEntity("Check status ok!", HttpStatus.OK);
    }

    @GetMapping("/prova2")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity check4(){
        return ResponseEntity.ok("SIA ADMIN CHE UTENTI "+
                "\n"+"Username: "+ Utils.getUsername()+"\nEmail: "+Utils.getEmail()+"\nRuoli: "+Utils.getRole());
    }

    @GetMapping("/provaUser")
    @PreAuthorize("hasAuthority('user')")
    public String home(@RequestParam(value = "someValue") int value) {
        return "Welcome, " + Utils.getEmail() + " " + value + " !";
    }

}
