package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.entity.Book;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.BookRepository;

@ExtendWith(MockitoExtension.class)

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void create_shouldSavedAndReturnBook() {
        Book book = new Book(null, "Clean Code", "Robert Martin", new BigDecimal("45.99"));
        Book savedBook = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));

        when(bookRepository.save(book)).thenReturn(savedBook);

        Book result = bookService.create(book);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getAll_shouldReturnAllBooks() {

        Book book1 = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));
        Book book2 = new Book(2L, "Effective Java", "Joshua Bloch", new BigDecimal("55.00"));

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = bookService.getAll();

        assertEquals(2, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
        assertEquals("Effective Java", result.get(1).getTitle());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnBookWhenBookExists() {

        Book book = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenBookNotFound() {

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getById(99L));

        assertEquals("Book with id 99 not found", exception.getMessage());

        verify(bookRepository, times(1)).findById(99L);
    }

    @Test
    void update_shouldUpdateBookAndReturnUpdatedBook() {

        Book existingBook = new Book(1L, "Old Title", "Old Author", new BigDecimal("10.00"));
        Book bookDetails = new Book(null, "New Title", "New Author", new BigDecimal("20.00"));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.update(1L, bookDetails);

        assertEquals(1L, result.getId());
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(new BigDecimal("20.00"), result.getPrice());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void delete_shouldDeleteBookWhenBookExists() {

        Book book = new Book(1L, "Clean Code", "Robert Martin", new BigDecimal("45.99"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void delete_shouldThrowExceptionWhenBookNotFound() {

        when(bookRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(5L));

        verify(bookRepository, times(1)).findById(5L);
        verify(bookRepository, never()).delete(any(Book.class));
    }

}
