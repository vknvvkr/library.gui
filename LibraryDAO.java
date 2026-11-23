package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAO {

    public List<Author> getAllAuthors() throws SQLException {

        List<Author> authors = new ArrayList<>();

        String sql = "SELECT id, full_name FROM authors ORDER BY full_name";

        try (
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {
                authors.add(new Author(
                        result.getInt("id"),
                        result.getString("full_name"),
                        ""));
            }
        }

        return authors;
    }


    public void addAuthor(String name, String country) throws SQLException {

        String sql = "INSERT INTO authors (full_name) VALUES (?)";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }


    public void addReader(String name, String phone) throws SQLException {

        String sql = "INSERT INTO readers (full_name, phone) VALUES (?, ?)";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.setString(2, phone);

            statement.executeUpdate();
        }
    }


    public List<String> getAllLoans() throws SQLException {

        List<String> loans = new ArrayList<>();

        String sql = """
                SELECT borrowings.id, books.title, readers.full_name,
                       borrowings.borrow_date, borrowings.return_date
                FROM borrowings
                JOIN books ON borrowings.book_id = books.id
                JOIN readers ON borrowings.reader_id = readers.id
                ORDER BY borrowings.id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {
                Date returnDate = result.getDate("return_date");
                String status = (returnDate == null) ? "На руках" : "Возвращена (" + returnDate + ")";
                loans.add("ID: " + result.getInt("id") +
                        ", Книга: " + result.getString("title") +
                        ", Читатель: " + result.getString("full_name") +
                        ", Дата: " + result.getDate("borrow_date") +
                        ", Статус: " + status);
            }
        }

        return loans;
    }


    /** Количество книг на руках (выдачи без даты возврата). */
    public int countActiveLoans() throws SQLException {

        String sql = "SELECT COUNT(*) FROM borrowings WHERE return_date IS NULL";

        try (
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql)
        ) {
            return result.next() ? result.getInt(1) : 0;
        }
    }


    public void addLoan(int bookId, int readerId, Date date) throws SQLException {

        String sql = """
                INSERT INTO borrowings (book_id, reader_id, borrow_date)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, bookId);
            statement.setInt(2, readerId);
            statement.setDate(3, date);

            statement.executeUpdate();
        }
    }


    public void returnBook(int id, Date date) throws SQLException {

        String sql = """
                UPDATE borrowings
                SET return_date = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDate(1, date);
            statement.setInt(2, id);

            statement.executeUpdate();
        }
    }
}