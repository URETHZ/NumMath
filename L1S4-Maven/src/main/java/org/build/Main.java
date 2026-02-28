package org.build;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/*
Вопросы для подготовки к защите лабораторной работы №1.
1. Этапы, особенности решения нелинейных уравнений численными методами.
2. Метод половинного деления.
3. Метод касательных.
4. Метод секущих.
5. Метод хорд.
6. Метод простых итераций.
7. Геометрическая интерпретация (для любого из перечисленных методов).
8. Сходимость метода (для любого из перечисленных). Устойчивость методов.
9. Определение достаточного количества итераций при заданной точности (для любого из перечисленных алгоритмов).
10. Вывод формулы для корня (метод хорд, метод секущих, метод касательных).
11. Ограничения и рекомендации по применению каждого из методов.
 */
public class Main {
    private double eps = 0.01, HalfDivisionMethodResult = -1, q = 1;
    private double MetodOfSimpleIterationResult = -1, TangentMethodResult = -1;
    private double SecantMetodResult = -1, ChordMetodResult = -1;
    private int Count = 50;
    private List<Double> approximations = new ArrayList<>(); // список всех приближений

    public int getCount() { return Count; }

    public void setCount(int count) { this.Count = count; }

    public void setEps(double e) { eps = e; }

    public double getEps() { return eps; }

    public void setHDMR(double X) { HalfDivisionMethodResult = X; }

    public double getHDMR() { return HalfDivisionMethodResult; }

    public void setMOSI(double X) { MetodOfSimpleIterationResult = X; }

    public double getMOSI() { return MetodOfSimpleIterationResult; }

    public double getTM() { return TangentMethodResult; }

    public void setTM(double X) { this.TangentMethodResult = X; }

    public double getQ() { return q; }

    public void setQ(double X) { this.q = X; }

    public void setSM(double X) { this.SecantMetodResult = X; }

    public double getSM() { return SecantMetodResult; }

    public void setCM(double X) { this.ChordMetodResult = X; }

    public double getCM() { return ChordMetodResult; }

    public List<Double> getApprox() { return approximations; }

    public void setAprox(double X){

        this.approximations.add(X);
        // Уведомляем всех слушателей об изменении
        support.firePropertyChange("approximations", "0", "1");
    }

    public void clearApproximations() { approximations.clear(); }

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public static void main(String[] args) {
        new MainFrame(new Main());
    }

    public void HalfDivisionMethod(double x1, double x2, Double X0prior) {
        clearApproximations(); // очищаем список перед началом нового метода
        double x0, fx0;
        int n = 0;
        try {
            if (x1 > x2) {
                double temp = x2;
                x2 = x1;
                x1 = temp;
            }
            if (f(x1) * f(x2) > 0) throw new Exception("Границы области одного знака.");

            x0 = (x1 + x2) / 2;

            if(!Double.isNaN(X0prior)){
                x0 = X0prior;
            }
            while (Math.abs(x2 - x1) > 2 * eps || n < Math.log(Math.abs(x2 - x1)) / Math.log(2) / eps - 1){// предел сходимости
                n++;
                setAprox(x0); // записываем текущее приближение
                fx0 = (double) Math.round(f(x0) * 1000) / 1000;
                if (fx0 == 0.000) {
                    x0 = (double) Math.round(x0 * 1000) / 1000;
                    setHDMR(x0);
                    return;
                } else if (fx0 * f(x1) > 0) {
                    x1 = x0;
                } else {
                    x2 = x0;
                }
                x0 = (x1+x2)/2;

                // Проверка на превышение максимального количества итераций
                if (n > getCount()) {
                    throw new RuntimeException("Превышено максимальное количество итераций (" + getCount() + ") в методе половинного деления");
                }
            }

            x0 = (double) Math.round(x0 * 1000) / 1000;
            setHDMR(x0);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка в методе половинного деления: " + ex.getMessage());
        }
    }

    public void TangentMethod(double x1, double x2, Double X0prior) {
        clearApproximations(); // очищаем список перед началом нового метода
        try {
            double xn, xk = x1;
            if (x1 > x2) {
                double temp = x2;
                x2 = x1;
                x1 = temp;
            }
            if (f(x1) * f(x2) > 0) throw new Exception("Границы области одного знака.");
            Double[] ffx = new Double[11], fffx = new Double[11];
            double a;
            for (int i = 0; i <= 10; i++) {
                a = x1 + i * (x2 - x1) / 10;
                ffx[i] = ff(a);
                fffx[i] = fff(a);
                if (fffx[i] * f(a) > 0) {
                    xk = a;
                }
            }
            double Max = fffx[0], Min = ffx[0];
            boolean allPositive = true, allNegativ = true;
            for (Double y : ffx) {
                if (Math.abs(y) < Math.abs(Min)) {
                    Min = y;
                }
                if (y < 0) {
                    allPositive = false;
                }
                if (y >= 0) {
                    allNegativ = false;
                }
            }
            if (!allPositive && !allNegativ) throw new Exception("Производная f(x) на участке меняет знак, ошибка.");
            for (Double y : ffx) {
                if (Math.abs(y) > Math.abs(Max)) {
                    Max = y;
                }
            }
            if((allPositive || allNegativ) && Double.isNaN(X0prior))
            {
                for(double x = x1; x<=x2; x = x + (x2-x1)/10){
                    if (fff(x) * f(x) >= 0) {
                        xk=x;
                    }
                }
            }
            if(!Double.isNaN(X0prior)){
                xk = X0prior;
            }

            setAprox(xk); // записываем начальное приближение

            xn = xk - f(xk) / ff(xk);
            setAprox(xn); // записываем первое приближение

            int iteration = 1;
            while (Math.abs(xn - xk) > Math.sqrt(2 * Min * eps / Max)) {
                xk = xn;
                xn = xk - f(xk) / ff(xk);
                setAprox(xn); // записываем текущее приближение
                iteration++;

                // Проверка на превышение максимального количества итераций
                if (iteration > getCount()) {
                    throw new RuntimeException("Превышено максимальное количество итераций (" + getCount() + ") в методе Ньютона");
                }
            }
            xn = (double) Math.round(xn * 1000) / 1000;
            setTM(xn);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка в методе Ньютона: " + ex.getMessage());
        }
    }

    void SecantMethod(double x1, double x2, Double X0prior) {
        clearApproximations(); // очищаем список перед началом нового метода
        try {
            double xn, xkk = Double.isNaN(X0prior)?x1:X0prior, xk;
            if (xkk>=x1 && xkk+eps<=x2) {  xk = xkk + eps; }
            else {  xk = xkk - eps;}
            if (x1 > x2) {
                double temp = x2;
                x2 = x1;
                x1 = temp;
            }
            if (f(x1) * f(x2) > 0) throw new Exception("Границы области одного знака.");

            setAprox(xkk); // записываем x_(n-2)
            setAprox(xk);   // записываем x_(n-1)

            // xn = xn; xk = x_(n-1) xkk = x_(n-2);
            xn = xk - (xk - xkk) * f(xk) / (f(xk) - f(xkk));
            setAprox(xn); // записываем первое приближение
            if (Double.isNaN(xn) || xn== xk) { throw new RuntimeException("Возможно попдание в ассимптоту. (xn=x(n-1))");}
            int iteration = 1;
            while (Math.abs(xn - xk) > eps || Math.abs(f(xn)) > eps) {
                xkk = xk;
                xk = xn;
                xn = xk - (xk - xkk) * f(xk) / (f(xk) - f(xkk));
                if (Double.isNaN(xn) || xn== xk) { throw new RuntimeException("Возможно попдание в ассимптоту. (xn=x(n-1))");}
                setAprox(xn); // записываем текущее приближение
                iteration++;

                // Проверка на превышение максимального количества итераций
                if (iteration > getCount()) {
                    throw new RuntimeException("Превышено максимальное количество итераций (" + getCount() + ") в методе секущих");
                }
            }
            xn = (double) Math.round(xn * 1000) / 1000;
            setSM(xn);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка в методе секущих: " + ex.getMessage());
        }
    }

    void ChordMethod(double x1, double x2, Double X0prior) {
        clearApproximations();
        try {
            double xn, xk, b;
            if (x1 > x2) {
                double temp = x2;
                x2 = x1;
                x1 = temp;
            }
            if (f(x1) * f(x2) > 0) throw new Exception("Границы области одного знака.");
            if (fff(x1) * f(x1) >= 0) {
                xk = x2;
                b = x1;
            } else if (fff(x2) * f(x2) >= 0) {
                xk = x1;
                b = x2;
            } else throw new RuntimeException("X0 не установлено");
            if(!Double.isNaN(X0prior)){ xk = X0prior;}
            if(xk==b) {throw new RuntimeException("x0 не должно быть равно b. Оно должно быть максимально далеко от него!");}
            setAprox(xk); // записываем начальное приближение
            xn = xk - (b - xk) * f(xk) / (f(b) - f(xk));
            if (Double.isNaN(xn) || xn== xk) { throw new RuntimeException("Возможно попдание в ассимптоту. (xn=x(n-1))");}
            setAprox(xn); // записываем первое приближение
            int iteration = 1;
            while (Math.abs(xn - xk) > eps) {
                xk = xn;
                xn = xk - (b - xk) * f(xk) / (f(b) - f(xk));
                if (Double.isNaN(xn)|| xn== xk) { throw new RuntimeException("Попдание в ассимптоту.");}
                setAprox(xn); // записываем текущее приближение
                iteration++;
                // Проверка на превышение максимального количества итераций
                if (iteration > getCount()) {
                    throw new RuntimeException("Превышено максимальное количество итераций (" + getCount() + ") в методе хорд");
                }
            }
            xn = (double) Math.round(xn * 1000) / 1000;
            setCM(xn);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка в методе хорд: " + ex.getMessage());
        }
    }

    void MethodOfSimpleIterations(double x1, double x2, Double X0prior) {
        clearApproximations(); // очищаем список перед началом нового метода
        try {
            double lambda;
            if (f(x1) * f(x2) > 0) throw new Exception("Границы области одного знака.");
            if (x1 > x2) {
                double temp = x2;
                x2 = x1;
                x1 = temp;
            }
            //знак лямбда
            Double[] fx = new Double[11];
            for (int i = 0; i <= 10; i++) {
                fx[i] = ff(x1 + i * (x2 - x1) / 10);
            }
            double Max = fx[0], Min = fx[0];
            boolean allPositive = true, allNegativ = true;
            for (Double y : fx) {
                if (Math.abs(y) > Math.abs(Max)) {
                    Max = y;
                }
                if (Math.abs(y) < Math.abs(Min)) {
                    Min = y;
                }
                if (y < 0) {
                    allPositive = false;
                }
                if (y >= 0) {
                    allNegativ = false;
                }
            }
            if (!allPositive && !allNegativ)
                throw new Exception("Производная f(x) на участке меняет знак, ошибка.");
            if (allPositive) {
                lambda = -1 / Math.abs(Max);
            } else {
                lambda = 1 / Math.abs(Max);
            }
            double q = 0.0;
            for (int i = 0; i <= 10; i++) {
                double x = x1 + i * (x2 - x1) / 10;
                double fprime = ff(x); // производная f(x) в точке x
                double phiprime = 1.0 + lambda * fprime;
                double abs_phiprime = Math.abs(phiprime);
                if (abs_phiprime > q) {
                    q = abs_phiprime;
                }
            }
            setQ(q);
            if (q >= 1) {
                // Можно выбросить предупреждение или попробовать подобрать другую lambda
                throw new RuntimeException("Внимание: q = " + q + " >= 1. Сходимость не гарантирована.");
            }
            double x_prev = x1;  // предыдущее приближение
            if (!Double.isNaN(X0prior)){x_prev=X0prior;}
            double x_curr = phi(x_prev, lambda);  // текущее приближение

            setAprox(x_prev); // записываем начальное приближение
            setAprox(x_curr); // записываем первое приближение

            int iteration = 1;
            while (Math.abs(x_curr - x_prev) > (1 - q) / q * eps) {
                x_prev = x_curr;
                x_curr = phi(x_prev, lambda);
                setAprox(x_curr); // записываем текущее приближение
                iteration++;

                // Проверка на превышение максимального количества итераций
                if (iteration > getCount()) {
                    throw new RuntimeException("Превышено максимальное количество итераций (" + getCount() + ") в методе простых итераций");
                }
            }
            x_curr = (double) Math.round(x_curr * 1000) / 1000;
            setMOSI(x_curr);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка в методе итераций: " + ex.getMessage());
        }
    }

    //Функция, ее первая, вторая производная и phi(x) для метода простых итераций.
    static double f(double x) {
        return -Math.pow(x, 4) + 4 * Math.pow(x, 3) - 5 * Math.log(x) - 10;
    }

    static double ff(double x) {
        return -4 * Math.pow(x, 3) + 12 * Math.pow(x, 2) - 5 / x;
    }

    static double fff(double x) {
        return -12 * Math.pow(x, 2) + 24 * x + 5 / Math.pow(x, 2);
    }

    static double phi(double x, double lambda) {
        return x + lambda * f(x);
    }
}