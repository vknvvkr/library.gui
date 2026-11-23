package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JFrame {

    private static final String[] COLUMNS = {"ID", "Название", "Автор", "Год", "Количество"};

    private final BookDAO bookDAO = new BookDAO();
    private final LibraryDAO libraryDAO = new LibraryDAO();

    private List<Book> books = new ArrayList<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // редактирование только через форму
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return (column == 0 || column >= 3) ? Integer.class : String.class;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JTextField searchField = new JTextField(22);

    private final JLabel totalBooksValue = new JLabel("—");
    private final JLabel totalCopiesValue = new JLabel("—");
    private final JLabel authorsValue = new JLabel("—");
    private final JLabel loansValue = new JLabel("—");
    private final JLabel statusLabel = new JLabel(" ");

    public Dashboard() {
        super("Библиотека — управление книгами");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 700);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null); // по центру экрана

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BACKGROUND);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, Theme.GAP));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        main.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, Theme.GAP));
        center.setOpaque(false);
        center.add(buildStats(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        main.add(buildStatusBar(), BorderLayout.SOUTH);

        root.add(main, BorderLayout.CENTER);
        setContentPane(root);

        // загрузка после показа окна, чтобы сообщение об ошибке появилось поверх него
        SwingUtilities.invokeLater(this::refresh);
    }

    // ---------- Построение интерфейса ----------

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Theme.SIDEBAR);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createEmptyBorder(24, 18, 24, 18));

        JLabel logo = new JLabel("Library");
        logo.setFont(Theme.font(Font.BOLD, 20f));
        logo.setForeground(Color.WHITE);
        side.add(logo);
        side.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Book DAO · MySQL");
        sub.setFont(Theme.font(Font.PLAIN, 12f));
        sub.setForeground(Theme.SIDEBAR_TEXT);
        side.add(sub);
        side.add(Box.createVerticalStrut(28));

        JLabel nav = new JLabel("▸  Книги");
        nav.setFont(Theme.font(Font.BOLD, 14f));
        nav.setForeground(Color.WHITE);
        side.add(nav);
        side.add(Box.createVerticalGlue());

        JButton about = Theme.button("О приложении", new Color(0x334155));
        about.setAlignmentX(Component.LEFT_ALIGNMENT);
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                """
                Библиотека — CRUD-приложение на Java Swing + MySQL.

                • Add — добавить книгу
                • Edit — изменить выбранную книгу (или двойной клик)
                • Delete — удалить выбранную книгу
                • Refresh — заново загрузить данные из БД
                • Поиск — по названию и автору, сортировка — клик по столбцу
                """,
                "О приложении", JOptionPane.INFORMATION_MESSAGE));
        side.add(about);
        return side;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Каталог книг");
        title.setFont(Theme.font(Font.BOLD, 24f));
        title.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("Добавляйте, изменяйте и удаляйте книги библиотеки");
        subtitle.setFont(Theme.font(Font.PLAIN, 13f));
        subtitle.setForeground(Theme.MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(subtitle);
        return header;
    }

    private JPanel buildStats() {
        JPanel stats = new JPanel(new GridLayout(1, 4, Theme.GAP, 0));
        stats.setOpaque(false);
        stats.add(statCard("Всего книг", totalBooksValue, Theme.PRIMARY));
        stats.add(statCard("Экземпляров", totalCopiesValue, Theme.SUCCESS));
        stats.add(statCard("Авторов", authorsValue, Theme.WARNING));
        stats.add(statCard("На руках", loansValue, Theme.DANGER));
        return stats;
    }

    private JPanel statCard(String caption, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                Theme.card()));

        JLabel label = new JLabel(caption);
        label.setFont(Theme.font(Font.PLAIN, 12f));
        label.setForeground(Theme.MUTED);

        value.setFont(Theme.font(Font.BOLD, 22f));
        value.setForeground(Theme.TEXT);

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.GAP));
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(Theme.card());

        // Панель инструментов: поиск слева, кнопки справа
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        searchField.setPreferredSize(new Dimension(240, 32));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Поиск:");
        searchLabel.setForeground(Theme.MUTED);
        searchField.putClientProperty("JTextField.placeholderText", "название или автор"); // FlatLaf
        searchField.setToolTipText("Фильтр по названию и автору");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JButton add = Theme.button("Add", Theme.SUCCESS);
        JButton edit = Theme.button("Edit", Theme.PRIMARY);
        JButton delete = Theme.button("Delete", Theme.DANGER);
        JButton refresh = Theme.button("Refresh", Theme.MUTED);
        JButton addAuthor = Theme.button("+ Автор", Theme.WARNING);

        add.setToolTipText("Добавить новую книгу");
        edit.setToolTipText("Изменить выбранную книгу");
        delete.setToolTipText("Удалить выбранную книгу");
        refresh.setToolTipText("Загрузить данные из базы заново");
        addAuthor.setToolTipText("Добавить нового автора");

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        delete.addActionListener(e -> onDelete());
        refresh.addActionListener(e -> refresh());
        addAuthor.addActionListener(e -> onAddAuthor());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(addAuthor);
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(refresh);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(buttons, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Таблица
        table.setRowSorter(sorter);
        table.setRowHeight(30);
        table.setFont(Theme.font(Font.PLAIN, 13f));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(0xDBEAFE));
        table.setSelectionForeground(Theme.TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.font(Font.BOLD, 13f));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : new int[]{0, 3, 4}) {
            table.getColumnModel().getColumn(col).setCellRenderer(center);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(320);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Двойной клик по строке = Edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    onEdit();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setOpaque(false);
        statusLabel.setFont(Theme.font(Font.PLAIN, 12f));
        statusLabel.setForeground(Theme.MUTED);
        bar.add(statusLabel);
        return bar;
    }

    // ---------- Действия ----------

    /** Refresh: заново читает данные из MySQL и обновляет таблицу и статистику. */
    private void refresh() {
        try {
            books = bookDAO.getAllBooks();

            tableModel.setRowCount(0);
            int copies = 0;
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                        b.getId(), b.getTitle(), b.getAuthorName(), b.getYear(), b.getQuantity()});
                copies += b.getQuantity();
            }

            totalBooksValue.setText(String.valueOf(books.size()));
            totalCopiesValue.setText(String.valueOf(copies));
            authorsValue.setText(String.valueOf(libraryDAO.getAllAuthors().size()));
            loansValue.setText(String.valueOf(libraryDAO.countActiveLoans()));

            setStatus("Загружено записей: " + books.size(), Theme.MUTED);
        } catch (SQLException e) {
            tableModel.setRowCount(0);
            books = new ArrayList<>();
            setStatus("Нет подключения к базе данных", Theme.DANGER);
            showDbError("Не удалось загрузить данные", e);
        }
    }

    private void onAdd() {
        List<Author> authors = loadAuthors();
        if (authors == null) return;
        if (authors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "В базе нет ни одного автора. Сначала нажмите «+ Автор».",
                    "Нет авторов", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BookForm.Data data = new BookForm(this, authors, null).showDialog();
        if (data == null) return; // нажали «Отмена»

        try {
            bookDAO.addBook(data.title(), data.authorId(), data.year(), data.quantity());
            refresh();
            setStatus("Книга «" + data.title() + "» добавлена", Theme.SUCCESS);
        } catch (SQLException e) {
            showDbError("Не удалось добавить книгу", e);
        }
    }

    private void onEdit() {
        Book book = selectedBook();
        if (book == null) return;

        List<Author> authors = loadAuthors();
        if (authors == null) return;

        BookForm.Data data = new BookForm(this, authors, book).showDialog();
        if (data == null) return;

        try {
            boolean updated = bookDAO.updateBook(book.getId(), data.title(), data.authorId(),
                    data.year(), data.quantity());
            refresh();
            if (updated) {
                setStatus("Книга #" + book.getId() + " изменена", Theme.SUCCESS);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Книга не найдена — возможно, её уже удалили. Таблица обновлена.",
                        "Изменение", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            showDbError("Не удалось изменить книгу", e);
        }
    }

    private void onDelete() {
        Book book = selectedBook();
        if (book == null) return;

        int answer = JOptionPane.showConfirmDialog(this,
                "Удалить книгу «" + book.getTitle() + "» (ID " + book.getId() + ")?\n" +
                        "Это действие нельзя отменить.",
                "Подтверждение удаления", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        try {
            bookDAO.deleteBook(book.getId());
            refresh();
            setStatus("Книга «" + book.getTitle() + "» удалена", Theme.DANGER);
        } catch (SQLException e) {
            showDbError("Не удалось удалить книгу", e);
        }
    }

    private void onAddAuthor() {
        JTextField name = new JTextField(20);
        JTextField country = new JTextField(20);
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Имя автора *"));
        form.add(name);
        form.add(new JLabel("Страна"));
        form.add(country);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, form, "Новый автор",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String n = name.getText().trim();
            if (n.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя автора.",
                        "Проверьте данные", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (n.length() > 100) {
                JOptionPane.showMessageDialog(this, "Имя слишком длинное (максимум 100 символов).",
                        "Проверьте данные", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            try {
                libraryDAO.addAuthor(n, country.getText().trim());
                refresh();
                setStatus("Автор «" + n + "» добавлен", Theme.SUCCESS);
            } catch (SQLException e) {
                showDbError("Не удалось добавить автора", e);
            }
            return;
        }
    }

    // ---------- Вспомогательные методы ----------

    private void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // (?iu) — без учёта регистра, в том числе для кириллицы
            sorter.setRowFilter(RowFilter.regexFilter("(?iu)" + java.util.regex.Pattern.quote(text), 1, 2));
        }
        setStatus("Показано: " + table.getRowCount() + " из " + books.size(), Theme.MUTED);
    }

    /** Возвращает выбранную книгу или показывает подсказку, если ничего не выбрано. */
    private Book selectedBook() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Сначала выберите книгу в таблице.",
                    "Нет выбора", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        // с учётом сортировки/фильтра индекс в таблице ≠ индекс в модели
        return books.get(table.convertRowIndexToModel(viewRow));
    }

    private List<Author> loadAuthors() {
        try {
            return libraryDAO.getAllAuthors();
        } catch (SQLException e) {
            showDbError("Не удалось загрузить авторов", e);
            return null;
        }
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    /** Понятное сообщение об ошибке БД вместо стектрейса. */
    private void showDbError(String action, SQLException e) {
        String hint;
        int code = e.getErrorCode();
        String state = e.getSQLState() == null ? "" : e.getSQLState();

        String message = e.getMessage() == null ? "" : e.getMessage();

        if (message.contains("No suitable driver")) {
            hint = "Не найден драйвер MySQL.\nЗапустите проект через Maven, чтобы подключился mysql-connector-j.";
        } else if (code == 1045) {
            hint = "Неверный логин или пароль MySQL.\nПроверьте USER и PASSWORD в DBConnection.java "
                    + "(или переменные DB_USER / DB_PASSWORD).";
        } else if (code == 1049) {
            hint = "База library_db не найдена.\nВыполните скрипт database.sql в MySQL.";
        } else if (code == 1146 || code == 1054) {
            hint = "Структура таблиц не совпадает с программой.\nПересоздайте базу скриптом database.sql.";
        } else if (code == 1451) {
            hint = "Запись используется в других таблицах (например, в выдачах).";
        } else if (code == 1452) {
            hint = "Указан несуществующий автор.";
        } else if (state.startsWith("08") || message.contains("Communications link failure")) {
            hint = "Нет связи с сервером MySQL.\nПроверьте, что MySQL запущен на localhost:3306.";
        } else {
            hint = "Ошибка базы данных.";
        }

        JOptionPane.showMessageDialog(this,
                hint + "\n\nПодробности: " + e.getMessage(),
                action, JOptionPane.ERROR_MESSAGE);
    }
}
