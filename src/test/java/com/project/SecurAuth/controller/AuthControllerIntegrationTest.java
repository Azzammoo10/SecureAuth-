package com.project.SecurAuth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.SecurAuth.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'API d'enregistrement
 * 
 * Note: Ces tests utilisent la configuration par défaut.
 * Pour utiliser une base de données de test (H2), ajouter application-test.properties
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test d'enregistrement utilisateur avec données valides")
    void testRegister_WithValidData() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean.dupont@example.com")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@example.com"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Jean"));
    }

    @Test
    @DisplayName("Test d'enregistrement avec email invalide")
    void testRegister_WithInvalidEmail() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Marie")
                .lastName("Martin")
                .email("email-invalide")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement avec mot de passe faible")
    void testRegister_WithWeakPassword() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Pierre")
                .lastName("Bernard")
                .email("pierre.bernard@example.com")
                .password("12345")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement avec champs manquants")
    void testRegister_WithMissingFields() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("")
                .lastName("Test")
                .email("test@example.com")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement avec email manquant")
    void testRegister_WithMissingEmail() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement avec prénom et nom manquants")
    void testRegister_WithMissingNameFields() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName(null)
                .lastName(null)
                .email("user@example.com")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement avec requête JSON invalide")
    void testRegister_WithInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test d'enregistrement sans Content-Type")
    void testRegister_WithoutContentType() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("SecureP@ssw0rd123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}
