import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Calculator extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField display;
	private boolean newNumber = true;
    
    public static String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "⌫", "+",
            "C", "=", "(", ")"
    };

    public Calculator() {
        setTitle("Java Calculator - d7mSWE");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 30));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4));

        for (String text : buttons) {
            JButton b = new JButton(text);
            b.addActionListener(this);
            panel.add(b);
        }

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();

        if (cmd.charAt(0) >= '0' && cmd.charAt(0) <= '9') {
            if (newNumber) {
                display.setText(cmd);
                newNumber = false;
            } else {
                display.setText(display.getText() + cmd);
            }
        }
        
        else if (cmd.equals(".")) {
            String currentText = display.getText();
            if (newNumber) {
                display.setText("0.");
                newNumber = false;
            } else {
                display.setText(currentText + ".");
            }
        }
        
        else if (cmd.equals("(")) {
            String currentText = display.getText();
            if (!newNumber && currentText.length() > 0) {
                char lastChar = currentText.charAt(currentText.length() - 1);
                if ((lastChar >= '0' && lastChar <= '9') || lastChar == ')') {
                    display.setText(currentText + "*(");
                    newNumber = false;
                    return;
                }
            }
            
            if (newNumber) {
                display.setText("(");
                newNumber = false;
            } else {
                display.setText(currentText + "(");
            }
        }
        
        else if (cmd.equals(")")) {
            display.setText(display.getText() + ")");
            newNumber = false;
        }
        
        else if (cmd.equals("+") || cmd.equals("-") || cmd.equals("*") || cmd.equals("/")) {
            display.setText(display.getText() + cmd);
            newNumber = false;
        }
        
        else if (cmd.equals("⌫")) {
            String currentText = display.getText();
            if (currentText.length() > 1) {
                display.setText(currentText.substring(0, currentText.length() - 1));
            } else {
                display.setText("0");
                newNumber = true;
            }
        }

        else if (cmd.equals("C")) {
            display.setText("0");
            newNumber = true;
        }

        else if (cmd.equals("=")) {
            String expression = display.getText();
            expression = addMissingMultiplicationSigns(expression);
            double result = evaluateExpression(expression);
            
            if (Double.isNaN(result)) {
                display.setText("Error");
            } else {
                if (result == (long) result) {
                    display.setText("" + (long) result);
                } else {
                    display.setText("" + result);
                }
            }
            newNumber = true;
        }
    }
    
    private String addMissingMultiplicationSigns(String expr) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            result.append(c);
            
            if (i < expr.length() - 1) {
                char nextChar = expr.charAt(i + 1);
                if ((c >= '0' && c <= '9') || c == ')') {
                    if (nextChar == '(') {
                        result.append('*');
                    }
                }
                if (c == ')' && (nextChar >= '0' && nextChar <= '9')) {
                    result.append('*');
                }
            }
        }
        
        return result.toString();
    }
    
    private double evaluateExpression(String expr) {
        try {
            expr = expr.replaceAll("\\s", "");
            
            while (expr.contains("(")) {
                int lastOpen = expr.lastIndexOf("(");
                int firstClose = expr.indexOf(")", lastOpen);
                
                if (firstClose == -1) {
                    return Double.NaN;
                }
                
                String subExpr = expr.substring(lastOpen + 1, firstClose);
                double subResult = evaluateSimple(subExpr);
                
                expr = expr.substring(0, lastOpen) + subResult + expr.substring(firstClose + 1);
            }
            return evaluateSimple(expr);
            
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    private double evaluateSimple(String expr) {
        try {
            ArrayList<Double> numbers = new ArrayList<>();
            ArrayList<String> operators = new ArrayList<>();
          
            String num = "";
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (c == '+' || c == '-' || c == '*' || c == '/') {
                    numbers.add(Double.parseDouble(num));
                    operators.add(String.valueOf(c));
                    num = "";
                } else {
                    num += c;
                }
            }
            numbers.add(Double.parseDouble(num));
            
            for (int i = 0; i < operators.size(); i++) {
                String op = operators.get(i);
                if (op.equals("*") || op.equals("/")) {
                    double result;
                    if (op.equals("*")) {
                        result = numbers.get(i) * numbers.get(i + 1);
                    } else {
                        if (numbers.get(i + 1) == 0) {
                            return Double.NaN;
                        }
                        result = numbers.get(i) / numbers.get(i + 1);
                    }
                    numbers.set(i, result);
                    numbers.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }
            
            double result = numbers.get(0);
            for (int i = 0; i < operators.size(); i++) {
                String op = operators.get(i);
                if (op.equals("+")) {
                    result += numbers.get(i + 1);
                } else if (op.equals("-")) {
                    result -= numbers.get(i + 1);
                }
            }
            
            return result;
            
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}