package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private static Book mapBook(ResultSet result) throws SQLException {
        return new Book(
                result.getInt("id"),
                result.getString("title"),
                result.getInt("author_id"),
                result.getString("author"),
                result.getInt("year_published"),
                1
        );
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = """
                SELECT books.id, books.title, books.author_id, authors.full_name AS author,
                       books.year_published
                FROM books
                JOIN authors ON books.author_id = authors.id
                ORDER BY books.id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {
                books.add(mapBook(result));
            }
        }

        return books;
    }

    public void addBook(String title, int authorId, int year, int quantity) throws SQLException {
        String sql = """
                INSERT INTO books (title, author_id, year_published)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, title);
            statement.setInt(2, authorId);
            statement.setInt(3, year);

            statement.executeUpdate();
        }
    }

    /** @return найденная книга или null, если такой нет. */
    public Book findBook(int id) throws SQLException {
        String sql = """
                SELECT books.id, books.title, books.author_id, authors.full_name AS author,
                       books.year_published
                FROM books
                JOIN authors ON books.author_id = authors.id
                WHERE books.id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapBook(result) : null;
            }
        }
    }

    /** @return true, если строка была изменена. */
    public boolean updateBook(int id, String title, int authorId, int year, int quantity) throws SQLException {
        String sql = """
                UPDATE books
                SET title = ?, author_id = ?, year_published = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, title);
            statement.setInt(2, authorId);
            statement.setInt(3, year);
            statement.setInt(4, id);

            return statement.executeUpdate() > 0;
        }
    }

    /** @return true, если строка была удалена. */
    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }
}