package com.example.testfromb2group;




public class HandbookOfConsequencesOfViolations {
    private int id;
    private String name;
    private Integer divisionType;
    private Object del;

    public HandbookOfConsequencesOfViolations(){}

    public HandbookOfConsequencesOfViolations(int id, String name, int divisionType, Object del) {
        this.id = id;
        this.name = name;
        this.divisionType = divisionType;
        this.del = del;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(int divisionType) {
        this.divisionType = divisionType;
    }

    public Object getDel() {
        return del;
    }

    public void setDel(Object del) {
        this.del = del;
    }

    @Override
    public String toString(){
        return "iD: " + id + "\n" + "Name: " + name + "\n" +
        "DivisionType: " + divisionType + "\n" +
        "Del: " + del;
    }
}


