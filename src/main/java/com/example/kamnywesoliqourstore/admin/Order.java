package com.example.kamnywesoliqourstore.admin;

public class Order {
    private String id;
    private String branch;
    private int items;
    private String status;
    private String time;

    public Order(String id, String branch, int items, String status, String time) {
        this.id = id;
        this.branch = branch;
        this.items = items;
        this.status = status;
        this.time = time;
    }

    public String getId() { return id; }
    public String getBranch() { return branch; }
    public int getItems() { return items; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
}
