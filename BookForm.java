package org.example;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.List;


public class BookForm extends JDialog {

    public record Data(String title, int authorId, int year, int quantity) {}

    private static final int MIN_YEAR = 1000;
    private static final int MAX_TITLE = 150;

    private final JTextField titleField = new JTextField(24);
    private final JComboBox<Author> authorBox;
    private final JTextField yearField = new JTextField(8);
    private final JTextField quantityField = new JTextField(8);

    private Data result;

    /**
     * @param owner   главное окно
     * @param authors список авторов для выпадающего списка
     * @param book    книга для редактирования или null для новой книги
     */
    public BookForm(Frame owner, List<Author> authors, Book book) {
        super(owner, book == null ? "Добавить книгу" : "Изменить книгу #" + book.getId(), true);

        authorBox = new JComboBox<>(authors.toArray(new Author[0]));

        if (book != null) {
            titleField.setText(book.getTitle());
            yearField.setText(String.valueOf(book.getYear()));
            quantityField.setText(String.valueOf(book.getQuantity()));
            for (Author a : authors) {
                if (a.getId() == book.getAuthorId()) {
                    authorBox.setSelectedItem(a);
                }
            }
        } else {
            quantityField.setText("1");
        }

        JPanel content = new JPanel(new BorderLayout(0, Theme.GAP));
        content.setBorder(Theme.padding(20));
        content.setBackground(Theme.SURFACE);

        JLabel heading = new JLabel(getTitle());
        heading.setFont(Theme.font(Font.BOLD, 17f));
        heading.setForeground(Theme.TEXT);
        content.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addRow(fields, 0, "Название *", titleField);
        addRow(fields, 1, "Автор *", authorBox);
        addRow(fields, 2, "Год издания *", yearField);
        addRow(fields, 3, "Количество *", quantityField);
        content.add(fields, BorderLayout.CENTER);

        JButton save = Theme.button("Сохранить", Theme.PRIMARY);
        JButton cancel = Theme.button("Отмена", Theme.MUTED);
        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(save);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        getRootPane().setDefaultButton(save);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(6, 0, 6, 12);
        c.anchor = GridBagConstraints.WEST;

        JLabel l = new JLabel(label);
        l.setFont(Theme.font(Font.PLAIN, 13f));
        l.setForeground(Theme.MUTED);
        c.gridx = 0;
        panel.add(l, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        panel.add(field, c);
    }

    /** Проверка всех полей. При ошибке — сообщение и фокус на неверное поле. */
    private void onSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            error("Введите название книги.", titleField);
            return;
        }
        if (title.length() > MAX_TITLE) {
            error("Название слишком длинное (максимум " + MAX_TITLE + " символов).", titleField);
            return;
        }

        Author author = (Author) authorBox.getSelectedItem();
        if (author == null) {
            error("Выберите автора. Если списка нет — сначала добавьте автора.", authorBox);
            return;
        }

        int currentYear = Year.now().getValue();
        Integer year = parseInt(yearField.getText());
        if (year == null) {
            error("Год должен быть целым числом, например 1942.", yearField);
            return;
        }
        if (year < MIN_YEAR || year > currentYear) {
            error("Год должен быть от " + MIN_YEAR + " до " + currentYear + ".", yearField);
            return;
        }

        Integer quantity = parseInt(quantityField.getText());
        if (quantity == null) {
            error("Количество должно быть целым числом.", quantityField);
            return;
        }
        if (quantity < 0) {
            error("Количество не может быть отрицательным.", quantityField);
            return;
        }

        result = new Data(title, author.getId(), year, quantity);
        dispose();
    }

    private static Integer parseInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void error(String message, JComponent field) {
        JOptionPane.showMessageDialog(this, message, "Проверьте данные", JOptionPane.WARNING_MESSAGE);
        field.requestFocusInWindow();
    }

    /** Показать форму. @return данные или null, если пользователь нажал «Отмена». */
    public Data showDialog() {
        setVisible(true);
        return result;
    }
}
