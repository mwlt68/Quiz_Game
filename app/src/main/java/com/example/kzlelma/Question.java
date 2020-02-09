package com.example.kzlelma;

public class Question {
    public int id;
    public String question;
    public MainActivity.GameType category;
    public String trueAnswer;
    public String[] falseAnswer;
    public String imageName;

    public Question(int id,String question,String category,String trueAnswer,String[] falseAnswer,String imageName){
        this.id=id;
        this.question=question;
        if (category.equals("Cocuk"))
            this.category= MainActivity.GameType.young;
        else    // Yetiskin
            this.category= MainActivity.GameType.adult;
        this.trueAnswer=trueAnswer;
        this.falseAnswer=falseAnswer;
        this.imageName=imageName;
    }
}
