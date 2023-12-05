package com.pixel.hospital.controllers;

import com.pixel.hospital.entities.Patient;
import com.pixel.hospital.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PatientController {

    // injection de dependance par le constructeur avec parametres(utilisation des annotations Lombok)
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name ="page", defaultValue = "0") int p,
                        @RequestParam(name ="size", defaultValue = "5") int s,
                        @RequestParam(name = "keyword", defaultValue = "") String motCle){

        // j'utilise findAll  de JPA repository qui permet la pagination
        Page<Patient> pagepatients = patientRepository.findPatientByNomPatientContains(motCle, PageRequest.of(p,s));
        model.addAttribute("listeDesPatients", pagepatients.getContent());

        // je stock dans le model le nombre des pages pour me permettre de faire la pagination
        model.addAttribute("pages", new int[pagepatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("motCle", motCle);

        return "patients";
    }


    @GetMapping("/delete")
    public   String delete(@RequestParam(name="id") Long id,
                           @RequestParam(name = "keyword", defaultValue = "") String motCle,
                           @RequestParam(name = "page", defaultValue = "0") int page){

        patientRepository.deleteById(id);

        return "redirect:/index?page="+page+"&keyword="+motCle;
    }

    @GetMapping("/")
    public   String delete(){

        return "redirect:/index";
    }


}