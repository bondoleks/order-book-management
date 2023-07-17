package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class OrderBookManagement {
    private TreeMap<Integer, Integer> bids;
    private TreeMap<Integer, Integer> asks;
    private String[] parts;

    public OrderBookManagement() {
        bids = new TreeMap<>();
        asks = new TreeMap<>();
    }

    public void processInputFile(String inputFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(String line, BufferedWriter writer) throws IOException {
        parts = line.split(",");
        String command = parts[0];

        if (command.equals("u")) {
            int price = Integer.parseInt(parts[1]);
            int size = Integer.parseInt(parts[2]);
            String type = parts[3];
            updateOrderBook(price, size, type);
        } else if (command.equals("q")) {
            String queryType = parts[1];
            processQuery(queryType, writer);
        } else if (command.equals("o")) {
            String orderType = parts[1];
            int size = Integer.parseInt(parts[2]);
            processMarketOrder(orderType, size);
        }
    }

    private void updateOrderBook(int price, int size, String type) {
        if (type.equals("bid")) {
            bids.put(price, size);
        } else if (type.equals("ask")) {
            asks.put(price, size);
        }
    }

    private void processQuery(String queryType, BufferedWriter writer) throws IOException {
        if (queryType.equals("best_bid")) {
            int bestBidPrice = getBestBidPrice();
            int bestBidSize = bids.getOrDefault(bestBidPrice, 0);
            writer.write(bestBidPrice + "," + bestBidSize);
            writer.newLine();
        } else if (queryType.equals("best_ask")) {
            int bestAskPrice = getBestAskPrice();
            int bestAskSize = asks.getOrDefault(bestAskPrice, 0);
            writer.write(bestAskPrice + "," + bestAskSize);
            writer.newLine();
        } else if (queryType.equals("size")) {
            int price = Integer.parseInt(parts[2]);
            int size = getSizeAtPrice(price);
            writer.write(String.valueOf(size));
            writer.newLine();
        }
    }

    private int getBestBidPrice() {
        return bids.lastKey();
    }

    private int getBestAskPrice() {
        return asks.firstKey();
    }

    private int getSizeAtPrice(int price) {
        if (bids.containsKey(price)) {
            return bids.get(price);
        } else if (asks.containsKey(price)) {
            return asks.get(price);
        }
        return 0;
    }

    private void processMarketOrder(String orderType, int size) {
        if (orderType.equals("buy")) {
            processBuyMarketOrder(size);
        } else if (orderType.equals("sell")) {
            processSellMarketOrder(size);
        }
    }

    private void processBuyMarketOrder(int size) {
        while (size > 0 && !asks.isEmpty()) {
            Map.Entry<Integer, Integer> bestAsk = asks.firstEntry();
            int bestAskPrice = bestAsk.getKey();
            int bestAskSize = bestAsk.getValue();
            if (bestAskSize <= size) {
                size -= bestAskSize;
                asks.pollFirstEntry();
            } else {
                asks.put(bestAskPrice, bestAskSize - size);
                size = 0;
            }
        }
    }

    private void processSellMarketOrder(int size) {
        while (size > 0 && !bids.isEmpty()) {
            Map.Entry<Integer, Integer> bestBid = bids.lastEntry();
            int bestBidPrice = bestBid.getKey();
            int bestBidSize = bestBid.getValue();
            if (bestBidSize <= size) {
                size -= bestBidSize;
                bids.pollLastEntry();
            } else {
                bids.put(bestBidPrice, bestBidSize - size);
                size = 0;
            }
        }
    }
}
