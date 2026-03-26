package com.example.kamnywesoliqourstore.admin;

public class BranchProfit {
    private String branch;
    private double revenue;
    private double cost;
    private double profit;
    private String status;

    public BranchProfit(String branch, double revenue, double cost, double profit, String status) {
        this.branch = branch;
        this.revenue = revenue;
        this.cost = cost;
        this.profit = profit;
        this.status = status;
    }

    public String getBranch() { return branch; }
    public double getRevenue() { return revenue; }
    public double getCost() { return cost; }
    public double getProfit() { return profit; }
    public String getStatus() { return status; }
}
