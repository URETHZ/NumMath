package org.build;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.function.DoubleUnaryOperator;

public class Grafic extends JFrame {
    private GraphPanel graphPanel;
    private JComboBox<String> functionSelector;
    private JTextField x1Field, x2Field;

    // Константы для выбора функции
    private static final int FUNC_F = 0;
    private static final int FUNC_FF = 1;
    private static final int FUNC_FFF = 2;
    private static final int FUNC_PHI = 3;

    public Grafic(int x, int y){
        setTitle("График функции");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(x + 510, y); // небольшая корректировка позиции

        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Панель управления сверху
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(60, 60, 60));

        // Выбор функции
        JLabel funcLabel = new JLabel("Функция:");
        funcLabel.setForeground(Color.WHITE);
        controlPanel.add(funcLabel);

        String[] functions = {"f(x) - исходная", "f'(x) - первая производная",
                "f''(x) - вторая производная", "φ(x) - для итераций"};
        functionSelector = new JComboBox<>(functions);
        functionSelector.setBackground(Color.DARK_GRAY);
        functionSelector.setForeground(Color.CYAN);
        functionSelector.addActionListener(e -> updateGraph());
        controlPanel.add(functionSelector);

        // Поля для ввода границ
        controlPanel.add(new JLabel("  от:"));
        x1Field = new JTextField("3", 5);
        x1Field.setBackground(Color.DARK_GRAY);
        x1Field.setForeground(Color.CYAN);
        controlPanel.add(x1Field);

        controlPanel.add(new JLabel("до:"));
        x2Field = new JTextField("4", 5);
        x2Field.setBackground(Color.DARK_GRAY);
        x2Field.setForeground(Color.CYAN);
        controlPanel.add(x2Field);

        // Кнопка обновления
        JButton updateBtn = new JButton("Обновить");
        updateBtn.setBackground(Color.DARK_GRAY);
        updateBtn.setForeground(Color.CYAN);
        updateBtn.addActionListener(e -> updateGraph());
        controlPanel.add(updateBtn);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Панель для графика
        graphPanel = new GraphPanel();
        graphPanel.setBackground(Color.BLACK);
        mainPanel.add(graphPanel, BorderLayout.CENTER);

        // Нижняя панель с подсказкой
        JLabel hintLabel = new JLabel("  Левая кнопка мыши - перемещение | Колесико - масштаб",
                SwingConstants.CENTER);
        hintLabel.setBackground(new Color(40, 40, 40));
        hintLabel.setForeground(Color.LIGHT_GRAY);
        hintLabel.setOpaque(true);
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // Первоначальная отрисовка
        updateGraph();
        setVisible(true);
    }

    private void updateGraph() {
        try {
            double x1 = Double.parseDouble(x1Field.getText());
            double x2 = Double.parseDouble(x2Field.getText());

            if (x1 >= x2) {
                JOptionPane.showMessageDialog(this,
                        "Левая граница должна быть меньше правой!",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedFunc = functionSelector.getSelectedIndex();
            DoubleUnaryOperator func;
            String funcName;

            switch (selectedFunc) {
                case FUNC_F:
                    func = Main::f;
                    funcName = "f(x) = -x⁴ + 4x³ - 5ln(x) - 10";
                    break;
                case FUNC_FF:
                    func = Main::ff;
                    funcName = "f'(x) = -4x³ + 12x² - 5/x";
                    break;
                case FUNC_FFF:
                    func = Main::fff;
                    funcName = "f''(x) = -12x² + 24x + 5/x²";
                    break;
                case FUNC_PHI:
                    // Для phi(x) нужна lambda, используем стандартную lambda = -0.074
                    double lambda = -0.074;
                    func = x -> Main.phi(x, lambda);
                    funcName = "φ(x) = x + λ·f(x), λ = -0.074";
                    break;
                default:
                    func = Main::f;
                    funcName = "f(x)";
            }

            graphPanel.setFunction(func, funcName, x1, x2);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Введите корректные числа для границ!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Внутренний класс для отрисовки графика
    private class GraphPanel extends JPanel {
        private DoubleUnaryOperator function;
        private String functionName;
        private double xMin = 3, xMax = 4;
        private double yMin = -10, yMax = 10;

        // Параметры для масштабирования и перемещения
        private double offsetX = 0;
        private double offsetY = 0;
        private double scale = 1.0;
        private Point lastDragPoint;

        public void setFunction(DoubleUnaryOperator func, String name, double x1, double x2) {
            this.function = func;
            this.functionName = name;
            this.xMin = x1;
            this.xMax = x2;

            // Вычисляем автоматически yMin и yMax
            computeYRange();

            repaint();
        }

        private void computeYRange() {
            if (function == null) return;

            yMin = Double.MAX_VALUE;
            yMax = -Double.MAX_VALUE;

            double step = (xMax - xMin) / 500;
            for (double x = xMin; x <= xMax; x += step) {
                try {
                    double y = function.applyAsDouble(x);
                    if (Double.isFinite(y)) {
                        yMin = Math.min(yMin, y);
                        yMax = Math.max(yMax, y);
                    }
                } catch (Exception e) {
                    // Игнорируем точки, где функция не определена
                }
            }

            // Добавляем отступы
            double padding = (yMax - yMin) * 0.1;
            if (padding == 0) padding = 1.0;
            yMin -= padding;
            yMax += padding;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (function == null) {
                g2.setColor(Color.WHITE);
                g2.drawString("Функция не задана", getWidth()/2 - 70, getHeight()/2);
                return;
            }

            int width = getWidth();
            int height = getHeight();

            // Рисуем сетку и оси
            drawGrid(g2, width, height);

            // Рисуем график функции
            g2.setColor(new Color(0, 255, 100)); // ярко-зеленый

            double step = (xMax - xMin) / 500;
            Path2D.Double path = new Path2D.Double();
            boolean firstPoint = true;

            for (double x = xMin; x <= xMax; x += step) {
                try {
                    double y = function.applyAsDouble(x);
                    if (Double.isFinite(y)) {
                        int screenX = (int) (((x - xMin) / (xMax - xMin)) * width);
                        int screenY = (int) (height - ((y - yMin) / (yMax - yMin)) * height);

                        if (firstPoint) {
                            path.moveTo(screenX, screenY);
                            firstPoint = false;
                        } else {
                            path.lineTo(screenX, screenY);
                        }
                    } else {
                        firstPoint = true; // Разрыв в графике
                    }
                } catch (Exception e) {
                    firstPoint = true;
                }
            }

            g2.draw(path);

            // Подпись функции
            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString(functionName, 10, 20);

            // Подписи значений
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(String.format("x: [%.2f, %.2f]", xMin, xMax), 10, 35);
            g2.drawString(String.format("y: [%.2f, %.2f]", yMin, yMax), 10, 50);
        }

        private void drawGrid(Graphics2D g2, int width, int height) {
            g2.setColor(new Color(50, 50, 50));

            // Вертикальные линии сетки
            for (int i = 0; i <= 10; i++) {
                int x = i * width / 10;
                g2.drawLine(x, 0, x, height);
            }

            // Горизонтальные линии сетки
            for (int i = 0; i <= 10; i++) {
                int y = i * height / 10;
                g2.drawLine(0, y, width, y);
            }

            // Оси координат (приблизительно)
            g2.setColor(new Color(100, 100, 200));

            // Ось X (приблизительно)
            if (yMin <= 0 && yMax >= 0) {
                int yZero = (int) (height - ((0 - yMin) / (yMax - yMin)) * height);
                g2.drawLine(0, yZero, width, yZero);
            }

            // Ось Y (приблизительно)
            if (xMin <= 0 && xMax >= 0) {
                int xZero = (int) (((0 - xMin) / (xMax - xMin)) * width);
                g2.drawLine(xZero, 0, xZero, height);
            }
        }
    }
}