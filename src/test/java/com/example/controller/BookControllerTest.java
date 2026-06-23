package com.example.controller;

import com.example.config.SecurityConfig;
import com.example.entity.Book;
import com.example.exception.GlobalExceptionHandler;
import com.example.exception.ResourceNotFoundException;
import com.example.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class BookControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private BookService bookService;

        @MockBean
        private JwtDecoder jwtDecoder;

        @Test
        void getAll_shouldReturnBooks_forUserRole() throws Exception {
                Book book1 = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));
                Book book2 = new Book(2L, "Effective Java", "Joshua Bloch", new BigDecimal("55.00"));

                when(bookService.getAll()).thenReturn(List.of(book1, book2));

                mockMvc.perform(get("/api/books")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                                .andExpect(jsonPath("$[1].title").value("Effective Java"));
        }

        @Test
        void getAll_shouldReturn401_withoutToken() throws Exception {
                mockMvc.perform(get("/api/books"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void getById_shouldReturnBook_forUserRole() throws Exception {
                Book book = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));

                when(bookService.getById(1L)).thenReturn(book);

                mockMvc.perform(get("/api/books/1")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Clean Code"))
                                .andExpect(jsonPath("$.author").value("Robert Martin"))
                                .andExpect(jsonPath("$.price").value(45.99));
        }

        @Test
        void getById_shouldReturn404_whenBookNotFound() throws Exception {
                when(bookService.getById(99L))
                                .thenThrow(new ResourceNotFoundException("Book with id 99 not found"));

                mockMvc.perform(get("/api/books/99")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Book with id 99 not found"));
        }

        @Test
        void create_shouldReturnCreatedBook_forAdminRole() throws Exception {
                Book inputBook = new Book(null, "Spring in Action", "Craig Walls", new BigDecimal("39.99"));
                Book savedBook = new Book(1L, "Spring in Action", "Craig Walls", new BigDecimal("39.99"));

                when(bookService.create(org.mockito.ArgumentMatchers.any(Book.class))).thenReturn(savedBook);

                mockMvc.perform(post("/api/books")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .with(csrf())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(inputBook)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Spring in Action"))
                                .andExpect(jsonPath("$.author").value("Craig Walls"))
                                .andExpect(jsonPath("$.price").value(39.99));
        }

        @Test
        void create_shouldReturn403_forUserRole() throws Exception {
                Book inputBook = new Book(null, "Spring in Action", "Craig Walls", new BigDecimal("39.99"));

                mockMvc.perform(post("/api/books")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                                .with(csrf())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(inputBook)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void create_shouldReturn400_whenInvalidBody() throws Exception {
                String invalidJson = """
                                {
                                  "title": "",
                                  "author": "",
                                  "price": -10
                                }
                                """;

                mockMvc.perform(post("/api/books")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .with(csrf())
                                .contentType("application/json")
                                .content(invalidJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.title").exists())
                                .andExpect(jsonPath("$.author").exists())
                                .andExpect(jsonPath("$.price").exists());
        }

        @Test
        void delete_shouldReturnNoContent_forAdminRole() throws Exception {
                mockMvc.perform(delete("/api/books/1")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        void delete_shouldReturn403_forUserRole() throws Exception {
                mockMvc.perform(delete("/api/books/1")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                                .with(csrf()))
                                .andExpect(status().isForbidden());
        }
}