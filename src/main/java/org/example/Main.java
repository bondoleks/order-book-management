package org.example;

public class Main {

    public static void main(String[] args) {
        OrderBookManagement orderBook = new OrderBookManagement();
        orderBook.processInputFile("input.txt", "output.txt");
    }
}