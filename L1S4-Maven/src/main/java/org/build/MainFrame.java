package org.build;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

//JOptionPane.showMessageDialog(this, "Введите число!");
public class MainFrame extends JFrame
{
    private Main model;
    private JTextArea ta1 ,ta2;
    private final Font font = new Font("Arial",Font.BOLD, 25);
    public MainFrame(Main p) {
        this.model=p;
        setSize(500, 400);
        setLocationRelativeTo(null);
        setTitle("Вычисление нулей функции");
        ImageIcon img = new ImageIcon("L1S4-Maven/Icon.png");
        setIconImage(img.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setVisible(true);
    }
    private void initComponents()
    {
        JPanel mainpanel = new JPanel(new BorderLayout());
        JMenuBar menubar = new JMenuBar();
        JMenu graf = new JMenu("График");
        menubar.add(graf);
        JMenuItem Show = new JMenuItem("Построить график.");
        Show.addActionListener(e-> new Grafic(this.getLocation().x, this.getLocation().y));
        graf.add(Show);
        JMenu eps = new JMenu("Epsilon");
        menubar.add(eps);
        JMenuItem editEps = new JMenuItem("Задать постоянную");
        eps.add(editEps);
        editEps.addActionListener(e->
        {
            ChangeEps();
        });


        setJMenuBar(menubar);

        JPanel gridpanel = new JPanel(new GridLayout(0,2));
        ArrayList<JButton> btns = new ArrayList<>(Arrays.asList(
                createStyledButton("<html><center>Половинный<br>интервал</center></html>"),
                createStyledButton("<html><center>Метод<br>секущих</center></html>"),
                createStyledButton("<html><center>Метод<br>касательных</center></html>"),
                createStyledButton("<html><center>Метод<br>хорд</center></html>"),
                createStyledButton("<html><center>Метод<br>Простых итер-ций</center></html>")
        ));
        ArrayList<JLabel> lbs = new ArrayList<>(Arrays.asList(
                createStyledLabel("Вывод 1"),
                createStyledLabel("Вывод 2"),
                createStyledLabel("Вывод 3"),
                createStyledLabel("Вывод 4"),
                createStyledLabel("Вывод 5")
        ));
        for(int x=0,y=0;x!=5;x++,y++){
            gridpanel.add(btns.get(x));
            gridpanel.add(lbs.get(y));
        }
        addMethodListener(btns.get(0),"half" ,lbs.get(0),() -> model.HalfDivisionMethod(getX1(),getX2()));
        addMethodListener(btns.get(1),"secant" ,lbs.get(1),() -> model.SecantMethod(getX1(),getX2()));
        addMethodListener(btns.get(2),"newton" ,lbs.get(2),() -> model.TangentMethod(getX1(),getX2()));
        addMethodListener(btns.get(3),"chord" ,lbs.get(3),() -> model.ChordMethod(getX1(),getX2()));
        addMethodListener(btns.get(4),"iteration" ,lbs.get(4),() -> model.MethodOfSimpleIterations(getX1(),getX2()));

        JPanel labelpanel = new JPanel(new BorderLayout());

        JPanel textbar = new JPanel(new FlowLayout());
        textbar.setBackground(Color.darkGray);
        textbar.setFocusable(true);
        JLabel lb6 = createStyledLabel("x1:");
        JLabel lb7 = createStyledLabel("x2:");

        ta1 = new JTextArea("3");
        ta2 = new JTextArea("4");
        ta1.setFont(font);
        ta2.setFont(font);
        ta1.setBackground(Color.darkGray);
        ta2.setBackground(Color.darkGray);
        ta1.setForeground(Color.cyan);
        ta2.setForeground(Color.cyan);
        textbar.add(lb6);textbar.add(ta1);
        textbar.add(lb7);textbar.add(ta2);

        labelpanel.add(textbar);

        mainpanel.add(labelpanel, BorderLayout.SOUTH);
        mainpanel.add(gridpanel, BorderLayout.CENTER);

        add(mainpanel);
    }
    private void addMethodListener(JButton button, String methodName,
                                   JLabel resultLabel, Runnable methodCall) {
        button.addActionListener(e -> {
            try {
                methodCall.run();
                resultLabel.setText("Res: " + getMethodResult(methodName));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
    private double getMethodResult(String methodName) {
        switch (methodName) {
            case "half": return model.getHDMR();
            case "secant": return model.getHDMR();
            case "newton": return model.getTM();
            case "chord": return model.getHDMR();
            case "iteration": return model.getMOSI();
            default: return 0;
        }
    }
    private double getX1()
    {
        if(!TryParseDouble(ta1.getText()))
        {
            throw new NumberFormatException("X1 - введите правильное число");
        }
        return Double.parseDouble(ta1.getText());
    }
    private double getX2()
    {
        if(!TryParseDouble(ta2.getText()))
        {
            throw new NumberFormatException("X2 - введите правильное число");
        }
        return Double.parseDouble(ta2.getText());
    }
    private boolean TryParseDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException ex){
            return false;
        }
    }
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.darkGray);
        btn.setFont(new Font("Arial",Font.BOLD,20));
        btn.setForeground(Color.CYAN);
        btn.setFocusPainted(false);
        return btn;
    }
    private JLabel createStyledLabel(String text) {
        JLabel lb1 = new JLabel();
        lb1.setOpaque(true);
        lb1.setVerticalTextPosition(JLabel.CENTER);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        lb1.setText(text);
        lb1.setFont(font);
        lb1.setBackground(Color.DARK_GRAY);
        lb1.setForeground(Color.CYAN);
        return lb1;
    }
    private void ChangeEps() {
        String input = JOptionPane.showInputDialog(
                this,
                "Значение точности (eps):",
                model.getEps()
        );

        if (input != null) {
            try {
                double eps = Double.parseDouble(input);
                if (eps > 0) {
                    model.setEps(eps);
                    JOptionPane.showMessageDialog(this,
                            "Точность установлена: " + eps);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Значение должно быть положительным!",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Некорректный формат числа!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}