package com.audiobook.nbogdand.playbook.data;

public class Category {
    private String name;
    private Integer resourceId;

    public Category(){}

    public Category(String name,int resourceId){
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
