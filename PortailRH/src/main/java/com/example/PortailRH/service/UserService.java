package com.example.PortailRH.service;

import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.LoginDTO;
import com.example.PortailRH.entity.dto.PasswordDTO;
import com.example.PortailRH.entity.dto.UserDTO;
import com.example.PortailRH.entity.enummerations.RoleName;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User findUserByUsername (String username);
    public User loginUser(LoginDTO loginDTO);
    List<User> findAllUsers();

    public void deleteUser(Integer userId);
    public List<User> getUsersByRoles();
    void exportUsersToExcel(String filePath) throws IOException;
    Map<RoleName, Long> getRoleCounts();
    public User updateCollCHE(Long userId, UserDTO userDTO);
     public void deleteCollCHE(Long userId);
    public void exportUsersToPdf(OutputStream outputStream) throws IOException;
    public void exportUsersJasper(OutputStream outputStream) throws JRException;

    void forgotPassword(String email);
    public void resetPassword(String token, PasswordDTO passwordResetDTO);
    public List<RoleName> getUserRolesById(Integer userId);
    public Double getNoteGlobaleByUserId(Long userId);
    public User findByUsername(String username);
    public User updateProfile(Integer userId, UserDTO userDTO);
    public User addChef(UserDTO userDTO);
    public List<Map<String, Object>> getAllCollaboratorsUsernames();
    public Optional<User> getUserById(Integer userId);
    public List<User> getAllUsersWithSpecificRoles();
    public Optional<User> getById(Long id);
}
