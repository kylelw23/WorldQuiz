package com.example.worldquiz;

public class LiteratureClass {
    private int id;
    private String question;
    private String answer;
    public  LiteratureClass(String question, String answer){
        this.question=question;
        this.answer=answer;
    }
    public int getId(){
        return this.id;
    }
    public String getQuestion(){
        return this.question;
    }
    public String getAnswer(){
        return this.answer;
    }
}
