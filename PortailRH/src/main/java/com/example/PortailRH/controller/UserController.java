package com.example.PortailRH.controller;

import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.LoginDTO;
import com.example.PortailRH.entity.dto.PasswordDTO;
import com.example.PortailRH.entity.dto.UserDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import com.example.PortailRH.exception.EmailNotFoundException;
import com.example.PortailRH.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Auth")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    @PostMapping("/addchef")
    public ResponseEntity<?> addChef(@RequestBody UserDTO userDTO) {
        try {
            User savedUser = userService.addChef(userDTO);
            return ResponseEntity.ok(Map.of("success", true, "message", "Chef added successfully!"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            userService.loginUser(loginDTO);

            return ResponseEntity.ok("Login successful! Token: ");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("all")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/users-by-roles")
    public List<User> getUsersByRoles() {
        return userService.getUsersByRoles();
    }


    @GetMapping("/export")
    public ResponseEntity<Resource> exportUsersToExcel() throws IOException {
        String filePath = "utilisateurs.xlsx";
        userService.exportUsersToExcel(filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping("/role-counts")
    public ResponseEntity<Map<RoleName, Long>> getRoleCounts() {
        Map<RoleName, Long> roleCounts = userService.getRoleCounts();
        return ResponseEntity.ok(roleCounts);
    }

    @PutMapping("/coll-che/{id}")
    public ResponseEntity<User> updateCollCHE(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateCollCHE(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/coll-che/{id}")
    public ResponseEntity<Void> deleteCollCHE(@PathVariable Long id) {
        userService.deleteCollCHE(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportUsersToPdf() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            userService.exportUsersToPdf(outputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/users/jasper")
    public ResponseEntity<byte[]> exportUserReport(HttpServletResponse response) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            userService.exportUsersJasper(baos);

            byte[] pdfBytes = baos.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);

            // Set filename for download
            String filename = "users_report_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (JRException e) {
            // log.error("Error generating PDF report", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/preview")
    public void previewUserReport(HttpServletResponse response) {
        try {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader("Content-Disposition", "inline; filename=users_report.pdf");

            userService.exportUsersJasper(response.getOutputStream());

        } catch (Exception e) {
            //   log.error("Error generating PDF preview", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok("Instructions de réinitialisation envoyées à " + email);
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Email non trouvé");
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(
            @PathVariable String token,
            @RequestBody PasswordDTO resetDTO) {
        try {
            userService.resetPassword(token, resetDTO);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleName>> getUserRolesById(@PathVariable Integer userId) {
        List<RoleName> roles = userService.getUserRolesById(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{userId}/note-globale")
    public Double getNoteGlobale(@PathVariable Long userId) {
        return userService.getNoteGlobaleByUserId(userId);
    }
    @GetMapping("/id/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Integer userId, @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = userService.updateProfile(userId, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/collaboratorsusernames")
    public ResponseEntity<List<Map<String, Object>>> getCollaboratorsUsernames() {
        List<Map<String, Object>> collaboratorsUsernames = userService.getAllCollaboratorsUsernames();
        return ResponseEntity.ok(collaboratorsUsernames);
    }
    @GetMapping("chatuser/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/withspecificroles")
    public ResponseEntity<List<User>> getAllUsersWithSpecificRoles() {
        List<User> users = userService.getAllUsersWithSpecificRoles();
        return ResponseEntity.ok(users);
    }


    @GetMapping("usersjasperlogi/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getById(id);
        return userOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}