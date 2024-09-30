package com.smart.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;

import java.security.*;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        model.addAttribute("user", user);
    }

    @GetMapping("/index")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String getMethodName(Model model) {
        model.addAttribute("title", "Add Contact");
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, 
        @RequestParam("profileImage") MultipartFile file, 
        Principal principal, HttpSession httpSession) {
        try {
            String userName = principal.getName();

            User user = this.userRepository.getUserByUserName(userName);

            if(file.isEmpty()){
                contact.setImage("sign-up.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);

            user.getContacts().add(contact);

            this.userRepository.save(user);

            httpSession.setAttribute("message",new Message("Your Contact is added. Add More..","success"));
            
        } catch (Exception e) {
            e.printStackTrace();
            httpSession.setAttribute("message",new Message("Something went wrong. Try Again!","danger"));
            
        }
        return "normal/add_contact_form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, 
                                Model model, Principal principal) {
        model.addAttribute("title", "Show Contacts");
        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }
    
    @GetMapping("/contact/{cId}")
    public String getMethodName(@PathVariable("cId") Integer cid, Model model,Principal principal) {
        model.addAttribute("title", "Contact Detail");
        Optional<Contact> oContact = this.contactRepository.findById(cid);
        Contact contact = oContact.get();

        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("contact", contact);
        }
        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,Model model, HttpSession session,Principal principal){
        System.out.println("Herer=====");
        Contact contact = this.contactRepository.findById(cId).get();

        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        user.getContacts().remove(contact);

        this.userRepository.save(user);
        
        if(user.getId()==contact.getUser().getId()){
            contact.setUser(null);
            this.contactRepository.delete(contact);
            session.setAttribute("message", new Message("Contact Deleted Successfully!","success"));
        } else {
            session.setAttribute("message", new Message("Problem. You are not authorized!","error"));
        }
        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cid}")
    public String postMethodName(@PathVariable("cid") Integer cid,Model model) {
        model.addAttribute("title", "Update Dashboard");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    @PostMapping("/procces-update")
    public String postMethodName(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal) {
        
        try {

            Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
            if(!file.isEmpty()) {

                //delete file
                File deleteFile = new ClassPathResource("static/image").getFile();
                File file1 = new File(deleteFile,oldContact.getImage());
                file1.delete();

                //update file
                File savFile = new ClassPathResource("static/image").getFile();
                Path path = Paths.get(savFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
                this.contactRepository.save(contact);
                session.setAttribute("message", new Message("Update Successfully","success"));
            } else {
                contact.setImage(oldContact.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "redirect:/user/"+contact.getcId()+"/contact";
    }
    
    @GetMapping("/profile")
    public String getYourProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/profile";
    }
    
    
}
