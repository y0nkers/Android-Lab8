package com.example.lab8;

public class Game {
    private static final int LOWEST_NUMBER = 10;
    private static final int HIGHEST_NUMBER = 99;
    private static final Character[] OPERATIONS = {'+', '-', '*', '/'};

    private String problem;
    private int answer;

    Game() {
        newProblem();
    }

    static public int randomInt(int min, int max) { return (int) ((Math.random() * (max - min)) + min); }

    public void newProblem() {
        int first_number = randomInt(LOWEST_NUMBER, HIGHEST_NUMBER),
            second_number = randomInt(LOWEST_NUMBER, HIGHEST_NUMBER);
        Character operation = OPERATIONS[randomInt(0, OPERATIONS.length)];

        switch (operation) {
            case '+':
                problem = first_number + " + " + second_number + " = ?";
                answer = first_number + second_number;
                break;
            case '-':
                problem = first_number + " - " + second_number + " = ?";
                answer = first_number - second_number;
                break;
            case '*':
                problem = first_number + " * " + second_number + " = ?";
                answer = first_number * second_number;
                break;
            case '/':
                // special case for division, treat as multiplication in reverse
                problem = (first_number * second_number) + " / " + second_number + " = ?";
                answer = first_number;
                break;
        }
    }

    public String getProblem() { return problem; }

    public int getAnswer() { return answer; }
}
