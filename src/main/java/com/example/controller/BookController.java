package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Book;
import com.example.service.BookService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api/books")

public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping

    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        Book createdBook = bookService.create(book);

        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);

    }

    @GetMapping

    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")

    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PutMapping("/{id}")

    public ResponseEntity<Book> update(
            @PathVariable Long id,
            @Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.update(id, book));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
