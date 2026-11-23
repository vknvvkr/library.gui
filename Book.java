package org.example;

public class Book {

    private int id;
    private String title;
    private int authorId;
    private String authorName;
    private int year;
    private int quantity;

    public Book(int id, String title, int authorId, String authorName, int year, int quantity) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.year = year;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public int getYear() { return year; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Название: " + title +
                ", Автор: " + authorName +
                ", Год: " + year +
                ", Количество: " + quantity;
    }
}
