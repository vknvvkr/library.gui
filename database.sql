DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE authors (
    id      INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(100) NOT NULL,
    country VARCHAR(100)
);

CREATE TABLE books (
    id        INT PRIMARY KEY AUTO_INCREMENT,
    title     VARCHAR(150) NOT NULL,
    author_id INT NOT NULL,
    year      INT NOT NULL,
    quantity  INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id),
    CONSTRAINT chk_books_quantity CHECK (quantity >= 0)
);

CREATE TABLE readers (
    id    INT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE loans (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    book_id     INT NOT NULL,
    reader_id   INT NOT NULL,
    loan_date   DATE NOT NULL,
    return_date DATE,
    status      VARCHAR(20) NOT NULL DEFAULT 'Выдана',
    CONSTRAINT fk_loans_book   FOREIGN KEY (book_id)   REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_loans_reader FOREIGN KEY (reader_id) REFERENCES readers(id)
);

INSERT INTO authors (name, country) VALUES
('Абай Кунанбаев', 'Казахстан'),
('Мухтар Ауэзов',  'Казахстан'),
('Лев Толстой',    'Россия');

INSERT INTO books (title, author_id, year, quantity) VALUES
('Слова назидания', 1, 1890, 5),
('Путь Абая',       2, 1942, 3),
('Война и мир',     3, 1869, 4);

INSERT INTO readers (name, phone) VALUES
('Иван Петров',   '+77010000001'),
('Анна Сидорова', '+77020000002');

INSERT INTO loans (book_id, reader_id, loan_date) VALUES
(1, 1, '2026-11-01');
