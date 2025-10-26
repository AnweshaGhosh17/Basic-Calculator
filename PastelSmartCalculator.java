import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class PastelSmartCalculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final StringBuilder expression;

    public PastelSmartCalculator() {
        setTitle("Pastel Smart Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(248, 248, 255)); // soft pastel bg

        expression = new StringBuilder();

        // Display field
        display = new JTextField();
        display.setFont(new Font("Consolas", Font.BOLD, 28));
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBackground(new Color(255, 255, 255));
        display.setForeground(new Color(50, 50, 60));
        display.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(display, BorderLayout.NORTH);

        // Buttons layout
        String[] buttons = {
                "C", "⌫", "(", ")",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(new Color(248, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String text : buttons) {
            JButton button = createRoundedButton(text);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    // 🎨 Pastel Rounded Button
    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = new Color(230, 230, 250);
                if (getModel().isPressed()) bgColor = new Color(210, 210, 240);
                else if (getModel().isRollover()) bgColor = new Color(235, 235, 255);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(180, 180, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(new Color(60, 60, 80));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "C":
                expression.setLength(0);
                display.setText("");
                break;

            case "⌫":
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    display.setText(expression.toString());
                }
                break;

            case "=":
                try {
                    double result = evaluateExpression(expression.toString());
                    display.setText(expression + " = " + result);
                    expression.setLength(0);
                    expression.append(result);
                } catch (Exception ex) {
                    display.setText("Error");
                    expression.setLength(0);
                }
                break;

            default:
                expression.append(cmd);
                display.setText(expression.toString());
                break;
        }
    }

    // 🧠 Expression Evaluator with Brackets
    private double evaluateExpression(String expr) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (ch == ' ') continue;

            // Number or decimal
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() &&
                        (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(num.toString()));
            }

            // Opening bracket
            else if (ch == '(') {
                ops.push(ch);
            }

            // Closing bracket
            else if (ch == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                if (!ops.isEmpty()) ops.pop(); // remove '('
            }

            // Operator
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!ops.isEmpty() && hasPrecedence(ch, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(ch);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Divide by 0");
                return a / b;
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PastelSmartCalculator calc = new PastelSmartCalculator();
            calc.setVisible(true);
        });
    }
}
