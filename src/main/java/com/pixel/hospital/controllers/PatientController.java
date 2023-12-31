package com.pixel.hospital.controllers;

import com.pixel.hospital.entities.Patient;
import com.pixel.hospital.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PatientController {

    // injection de dependance par le constructeur avec parametres(utilisation des annotations Lombok)
    private PatientRepository patientRepository;

    @GetMapping("/user/index")
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

    @GetMapping("/admin/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public   String delete(@RequestParam(name="id") Long id,
                           @RequestParam(name = "keyword", defaultValue = "") String motCle,
                           @RequestParam(name = "page", defaultValue = "0") int page){

        patientRepository.deleteById(id);

        return "redirect:/user/index?page="+page+"&keyword="+motCle;
    }

    @GetMapping("/admin/formPatients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public   String formPatient(Model model){
        model.addAttribute("patient", new Patient());
        return "formPatients";
    }

 // Enregistrement d'un  nouveau Patient dans la Base des données .......
    @PostMapping("/admin/savePatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public   String savePatient(@Valid  Patient patient, BindingResult bindingResult){// en cas d'erreur on stock le message d'erreur dans BingingResult
        // avant d'enregistrement dans la base des données , je teste si l'objet bindingResult ne contient pas de message d'erreur
        if (bindingResult.hasErrors()){
            return "formPatients";

        }
        patientRepository.save(patient);
        return "redirect:/user/index?keyword="+patient.getNomPatient(); // redirection vers index tout en recuperant le nom du patient ajouté comme mot clé
    }


     // ----------------------------- Mise à jour des informations d'un patient ------------------
     @GetMapping("/admin/editPatient")
     @PreAuthorize("hasRole('ROLE_ADMIN')")
     public   String editPatient(Model model, @RequestParam(name = "id") Long id){
          Patient patientRechercherParId = patientRepository.findById(id).get();
         model.addAttribute("patientRechercherParId", patientRechercherParId);
         return "editPatient";
     }

    @GetMapping("/")
    public   String home(){

        return "redirect:/user/index";
    }

}
