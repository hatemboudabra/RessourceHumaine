package com.example.PortailRH.service;

import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.LoginDTO;
import com.example.PortailRH.entity.dto.PasswordDTO;
import com.example.PortailRH.entity.dto.UserDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import com.example.PortailRH.exception.AuthenticationException;
import com.example.PortailRH.exception.EmailNotFoundException;
import com.example.PortailRH.repository.UserRepo;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    public User registerUser(UserDTO userDTO) {

        if (!userRepo.existsByEmail(userDTO.getEmail())) {
            throw new EmailNotFoundException("Email not found in the database!");
        }
//        if (userRepo.existsByUsername(userDTO.getUsername())) {
//            throw new RuntimeException("Username already exists in the database!");
//        }
        if (userDTO.getRoles().contains(RoleName.ADMIN)) {
            throw new RuntimeException("Access denied: You cannot register with an ADMIN role!");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPost(userDTO.getPost());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList(RoleName.COLLABORATEUR));
        } else {
            user.setRoles(userDTO.getRoles());
        }      return userRepo.save(user);
    }





    @Transactional
    @Override
    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    @Override
    public User loginUser(LoginDTO loginDTO) {
        User user = userRepo.findByUsername(loginDTO.getUsername());

        if (user == null) {
            throw new AuthenticationException("User not found!");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password!");
        }

        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepo.deleteById(userId);
    }
    @Override
    public List<User> getUsersByRoles() {
        List<RoleName> roles = List.of(RoleName.COLLABORATEUR, RoleName.CHEF);
        return userRepo.findByRolesIn(roles);
    }
    @Override
    public void exportUsersToExcel(String filePath) throws IOException {
        List<User> users = getUsersByRoles();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Utilisateurs");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nom d'utilisateur");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("RoleName");
        headerRow.createCell(4).setCellValue("Post");
        int rowIndex = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getEmail());
            String roleNames = user.getRoles().stream()
                    .map(Enum::name)
                    .reduce((role1, role2) -> role1 + ", " + role2)
                    .orElse("");
            row.createCell(3).setCellValue(roleNames);
            row.createCell(4).setCellValue(user.getPost());
        }
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
    @Override
    public Map<RoleName, Long> getRoleCounts() {
        long collaborateurCount = userRepo.countByRolesContaining(RoleName.COLLABORATEUR);
        long chefCount = userRepo.countByRolesContaining(RoleName.CHEF);

        Map<RoleName, Long> roleCounts = new HashMap<>();
        roleCounts.put(RoleName.COLLABORATEUR, collaborateurCount);
        roleCounts.put(RoleName.CHEF, chefCount);

        return roleCounts;
    }



    @Override
    public User updateCollCHE(Long userId, UserDTO userDTO) {
        User existingUser = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (userDTO.getRoles().contains(RoleName.COLLABORATEUR) || userDTO.getRoles().contains(RoleName.CHEF)) {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setPassword(userDTO.getPassword());
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setPost(userDTO.getPost());
            existingUser.setRoles(userDTO.getRoles());
            return userRepo.save(existingUser);
        } else {
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle COLLABORATEUR ou CHEF.");
        }
    }

    @Override
    public void deleteCollCHE(Long userId) {
        User existingUser = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (existingUser.getRoles().contains(RoleName.COLLABORATEUR) || existingUser.getRoles().contains(RoleName.CHEF)) {
            userRepo.deleteById(Math.toIntExact(userId));
        } else {
            throw new IllegalArgumentException("Seuls les utilisateurs avec les rôles COLLABORATEUR ou CHEF peuvent être supprimés.");
        }
    }

    @Override
    public void exportUsersToPdf(OutputStream outputStream) throws IOException {
        List<User> users = getUsersByRoles();

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Liste des utilisateurs").setBold().setFontSize(16));

        float[] columnWidths = {50f, 150f, 200f, 150f, 100f};
        Table table = new Table(columnWidths);

        // En-têtes du tableau
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Nom d'utilisateur").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("RoleName").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Post").setBold()));

        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getUsername());
            table.addCell(user.getEmail());

            // Gestion des rôles sous forme de chaîne
            String roleNames = user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            table.addCell(roleNames);

            table.addCell(user.getPost() != null ? user.getPost() : "N/A");
        }

        document.add(table);
        document.close();
    }

    @Override
    public void exportUsersJasper(OutputStream outputStream) throws JRException {
        List<User> users = getUsersByRoles();

        List<Map<String, Object>> dataSource = users.stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("username", user.getUsername());
                    map.put("email", user.getEmail());
                    map.put("roleNames", user.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
                    return map;
                })
                .collect(Collectors.toList());

        InputStream templateStream = getClass().getResourceAsStream("/reports/users_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                new HashMap<>(),
                new JRBeanCollectionDataSource(dataSource)
        );
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email non trouvé: " + email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepo.save(user);

        String resetUrl = String.format("http://localhost:8082/swagger-ui/index.html#/reset-password/%s", resetToken);

        String subject = "Réinitialisation de votre mot de passe";
        String message = String.format(
                "Bonjour %s,\n\n" +
                        "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant :\n%s\n\n" +
                        "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet e-mail.\n\n" +
                        "Cordialement,\nL'équipe de support.",
                user.getUsername(), resetUrl
        );

        sendEmail(email, subject, message);
        System.out.println("Reset Token pour " + email + ": " + resetToken);
    }
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(String token, PasswordDTO passwordResetDTO) {
        if (!passwordResetDTO.getNewPassword().equals(passwordResetDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Jeton de réinitialisation invalide ou expiré."));

        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        user.setResetToken(null);
        userRepo.save(user);
    }

    @Override
    public List<RoleName> getUserRolesById(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + userId));
        return user.getRoles();
    }

    @Override
    public Double getNoteGlobaleByUserId(Long userId) {
        User user = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));
        return user.getNoteGlobale();
    }
@Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User updateProfile(Integer userId, UserDTO userDTO) {
        User userexistant = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + userId));
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            userexistant.setUsername(userDTO.getUsername());
        }
        if (userDTO.getLanguage() != null && !userDTO.getLanguage().isEmpty()) {
            userexistant.setLanguage(userDTO.getLanguage());

        }
        if (userDTO.getPostalCode() != null && !userDTO.getPostalCode().isEmpty()) {
            userexistant.setPostalCode(userDTO.getPostalCode());
        }
        if (userDTO.getPhone1() != null && !userDTO.getPhone1().isEmpty()) {
            userexistant.setPhone1(userDTO.getPhone1());
        }
        if (userDTO.getNationality() != null && !userDTO.getNationality().isEmpty()) {
            userexistant.setNationality(userDTO.getNationality());
        }
        if (userDTO.getDateOfBirth() != null) {
            userexistant.setDateOfBirth(userDTO.getDateOfBirth());
        }
        if (userDTO.getSexe() != null && !userDTO.getSexe().isEmpty()) {
userexistant.setSexe(userDTO.getSexe());        }
        if (userDTO.getAddress() != null && !userDTO.getAddress().isEmpty()) {
        userexistant.setAddress(userDTO.getAddress());
        }
        if (userDTO.getCivilStatus() != null && !userDTO.getCivilStatus().isEmpty()) {
            userexistant.setCivilStatus(userDTO.getCivilStatus());
        }
        if (userDTO.getCity() != null && !userDTO.getCity().isEmpty()) {
            userexistant.setCity(userDTO.getCity());
        }
        if (userDTO.getCountry() != null && !userDTO.getCountry().isEmpty()) {
            userexistant.setCountry(userDTO.getCountry());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            userexistant.setEmail(userDTO.getEmail());
        }  if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userexistant.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getPost() != null && !userDTO.getPost().isEmpty()) {
            userexistant.setPost(userDTO.getPost());

        }
        return userRepo.save(userexistant);


    }

    @Override
    public User addChef(UserDTO userDTO) {
        if (!userRepo.existsByEmail(userDTO.getEmail())) {
            throw new EmailNotFoundException("Email not found in the database!");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(Collections.singletonList(RoleName.CHEF));

        User savedUser = userRepo.save(user);

        // envoyer email a chef pour connecter a platform
        sendChefCredentials(userDTO.getEmail(), userDTO.getUsername(), userDTO.getPassword());

        return savedUser;
    }

    private void sendChefCredentials(String email, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Vos informations de connexion");
        message.setText("Bonjour,\n\n"
                + "Voici vos informations de connexion :\n"
                + "Username: " + username + "\n"
                + "Password: " + password + "\n"
                + "Email: " + email + "\n\n"
                + "Cordialement,\nVotre équipe");

        mailSender.send(message);
    }

    @Override
    public List<Map<String, Object>> getAllCollaboratorsUsernames() {
        return userRepo.findByRolesIn(Collections.singletonList(RoleName.COLLABORATEUR))
                .stream()
                .map(user -> {
                    Map<String, Object> collaborator = new HashMap<>();
                    collaborator.put("id", user.getId());
                    collaborator.put("username", user.getUsername());
                    return collaborator;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return userRepo.findById(userId);
    }

    @Override
    public List<User> getAllUsersWithSpecificRoles() {
        List<RoleName> targetRoles = Arrays.asList(
             //   RoleName.RESPONSABLE_RH,
                RoleName.COLLABORATEUR,
                RoleName.CHEF
        );
        return userRepo.findByRolesIn(targetRoles);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepo.findById(Math.toIntExact(id));
    }


}
