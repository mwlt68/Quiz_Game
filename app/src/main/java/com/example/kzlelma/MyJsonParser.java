package com.example.kzlelma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyJsonParser {
    public static List<Question> jsonParse(String jsonData) throws JSONException {

        List<Question> questions= new ArrayList<>();
        JSONArray readerArray = new JSONArray(jsonData);
        for (int i = 0; i < readerArray.length(); i++) {
            JSONObject object=readerArray.optJSONObject(i);
            int id=object.getInt("id");
            String category=object.getString("category");
            String questionStr=object.getString("question");
            String imageName=object.getString("image_name");
            String trueAnswer=object.getString("true_answer");
            JSONArray falseAnswerObj=object.getJSONArray("false_answer");
            String[] falseAnswers= new String[falseAnswerObj.length()];
            for (int j = 0; j < falseAnswerObj.length(); j++) {
                falseAnswers[j]=falseAnswerObj.getString(j);
            }
            Question question= new Question(id,questionStr,category,trueAnswer,falseAnswers,imageName);
            questions.add(question);
        }
        return questions;
    }
}
