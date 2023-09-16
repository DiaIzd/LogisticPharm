package com.example.logisticpharm.article;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logisticpharm.R;
import com.example.logisticpharm.model.Article;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ArticleAdd extends AppCompatActivity {


    // creating variables for
    // EditText and buttons.
    private EditText articleNameEdt, articleTextEdt, articleAuthorEdt;
    private Button sendDataArticlebtn;
    private Button btnBackArticleeAdd;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // creating a variable for
    // our object class
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_add);

        // initializing our edittext and button
        articleNameEdt = findViewById(R.id.idEdtArticleName);
        articleTextEdt = findViewById(R.id.idEdtArticleText);
        articleAuthorEdt = findViewById(R.id.idEdtArticleAuthor);
        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Article");

        // initializing our object
        // class variable.
        article = new Article();

        sendDataArticlebtn = findViewById(R.id.idBtnSendDataArticle);
        btnBackArticleeAdd = findViewById(R.id.idBtnBackAddArticle);
        btnBackArticleeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ArticleAdd.this, ArticleShow.class));
            }
        });
        // adding on click listener for our button.

        sendDataArticlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // getting text from our edittext fields.
                String name = articleNameEdt.getText().toString();

                String text = articleTextEdt.getText().toString();
                String author = articleAuthorEdt.getText().toString();

                if (!TextUtils.isEmpty(name) && !Character.isUpperCase(name.charAt(0))) {
                    Toast.makeText(ArticleAdd.this, "Nazwa leku musi zaczynać się dużą literą.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) || TextUtils.isEmpty(text) ) {
                    Toast.makeText(ArticleAdd.this, "Proszę uzupełnić wszystkie pola.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //  else call the method to add data to our database.

                    addArticletoFirebase(name, text,author);
                }
            }
        });
    }

    private void addArticletoFirebase(String name, String text, String author) {
        // below 3 lines of code is used to set
        // data in our object class.
        HashMap<String, Object> articleHashmap = new HashMap<>();
        //  String medicineId = databaseReference.push().getKey();
        article.setArticleName(name);

        article.setArticleText(text);
        article.setArticleAuthor(author);

        String keyArticle = databaseReference.push().getKey();

        databaseReference.child(keyArticle).setValue(article).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ArticleAdd.this, "Artykuł dodany", Toast.LENGTH_SHORT).show();
                articleNameEdt.getText().clear();
                    articleTextEdt.getText().clear();
                articleAuthorEdt.getText().clear();

            }
        });
    }

    }

