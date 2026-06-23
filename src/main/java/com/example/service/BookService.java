package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entity.Book;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.BookRepository;

@Service

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }

    public Book update(Long id, Book bookDetails) {
        Book existingBook = getById(id);

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPrice(bookDetails.getPrice());

        return bookRepository.save(existingBook);

    }

    public void delete(Long id) {
        Book existingBook = getById(id);

        bookRepository.delete(existingBook);
    }
}
