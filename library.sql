DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE authors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    year_published INT,
    author_id INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE readers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE borrowings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    reader_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (reader_id) REFERENCES readers(id)
);

INSERT INTO authors (full_name) VALUES
('Абай Кунанбаев'),
('Мухтар Ауэзов');

INSERT INTO books (title, year_published, author_id) VALUES
('Слова назидания', 1890, 1),
('Путь Абая', 1942, 2);

INSERT INTO readers (full_name, phone) VALUES
('Иван Петров', '+77010000001'),
('Анна Сидорова', '+77020000002');