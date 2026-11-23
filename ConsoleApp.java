package org.example;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;
public class ConsoleApp {

    public static void run() {

        Scanner scanner = new Scanner(System.in);

        BookDAO bookDAO = new BookDAO();
        LibraryDAO libraryDAO = new LibraryDAO();

        while (true) {

            System.out.println("\nБИБЛИОТЕКА");
            System.out.println("1. Показать все книги");
            System.out.println("2. Добавить книгу");
            System.out.println("3. Найти книгу");
            System.out.println("4. Изменить книгу");
            System.out.println("5. Удалить книгу");
            System.out.println("6. Добавить автора");
            System.out.println("7. Добавить читателя");
            System.out.println("8. Выдать книгу");
            System.out.println("9. Показать выдачи");
            System.out.println("10. Вернуть книгу");
            System.out.println("0. Выход");

            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> bookDAO.getAllBooks().forEach(System.out::println);

                    case 2 -> {
                        System.out.print("Название: ");
                        String title = scanner.nextLine();
                        System.out.print("ID автора: ");
                        int authorId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Год: ");
                        int year = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Количество: ");
                        int quantity = Integer.parseInt(scanner.nextLine().trim());
                        bookDAO.addBook(title, authorId, year, quantity);
                        System.out.println("Книга добавлена!");
                    }

                    case 3 -> {
                        System.out.print("ID книги: ");
                        Book book = bookDAO.findBook(Integer.parseInt(scanner.nextLine().trim()));
                        System.out.println(book != null ? book : "Книга не найдена!");
                    }

                    case 4 -> {
                        System.out.print("ID книги: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        Book old = bookDAO.findBook(id);
                        if (old == null) {
                            System.out.println("Книга не найдена!");
                            break;
                        }
                        System.out.print("Новое название: ");
                        String newTitle = scanner.nextLine();
                        System.out.print("Новый год: ");
                        int newYear = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Количество: ");
                        int newQuantity = Integer.parseInt(scanner.nextLine().trim());
                        bookDAO.updateBook(id, newTitle, old.getAuthorId(), newYear, newQuantity);
                        System.out.println("Книга изменена!");
                    }

                    case 5 -> {
                        System.out.print("ID книги: ");
                        boolean deleted = bookDAO.deleteBook(Integer.parseInt(scanner.nextLine().trim()));
                        System.out.println(deleted ? "Книга удалена!" : "Книга не найдена!");
                    }

                    case 6 -> {
                        System.out.print("Имя автора: ");
                        String author = scanner.nextLine();
                        System.out.print("Страна: ");
                        String country = scanner.nextLine();
                        libraryDAO.addAuthor(author, country);
                        System.out.println("Автор добавлен!");
                    }

                    case 7 -> {
                        System.out.print("Имя читателя: ");
                        String reader = scanner.nextLine();
                        System.out.print("Телефон: ");
                        String phone = scanner.nextLine();
                        libraryDAO.addReader(reader, phone);
                        System.out.println("Читатель добавлен!");
                    }

                    case 8 -> {
                        System.out.print("ID книги: ");
                        int bookId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("ID читателя: ");
                        int readerId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Дата выдачи (ГГГГ-ММ-ДД): ");
                        Date date = Date.valueOf(scanner.nextLine().trim());
                        libraryDAO.addLoan(bookId, readerId, date);
                        System.out.println("Книга выдана!");
                    }

                    case 9 -> libraryDAO.getAllLoans().forEach(System.out::println);

                    case 10 -> {
                        System.out.print("ID выдачи: ");
                        int loanId = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Дата возврата (ГГГГ-ММ-ДД): ");
                        Date returnDate = Date.valueOf(scanner.nextLine().trim());
                        libraryDAO.returnBook(loanId, returnDate);
                        System.out.println("Книга возвращена!");
                    }

                    case 0 -> {
                        System.out.println("Программа завершена!");
                        return;
                    }

                    default -> System.out.println("Неверный выбор!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный формат даты! Нужно ГГГГ-ММ-ДД.");
            } catch (SQLException e) {
                System.out.println("Ошибка базы данных: " + e.getMessage());
            }
        }
    }
}
