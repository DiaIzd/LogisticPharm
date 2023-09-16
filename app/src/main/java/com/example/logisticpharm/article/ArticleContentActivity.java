package com.example.logisticpharm.article;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.logisticpharm.R;

public class ArticleContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);

        TextView titleTextView = findViewById(R.id.articleTitleTextView);
        TextView authorTextView = findViewById(R.id.articleAuthorTextView);
        TextView contentTextView = findViewById(R.id.articleContentTextView);

        // Odczytaj przekazane dane z Intent
        String articleName = getIntent().getStringExtra("articleName");
        String articleAuthor = getIntent().getStringExtra("articleAuthor");
        String articleText = getIntent().getStringExtra("articleText");

        // Ustaw odczytane dane w odpowiednich elementach interfejsu
        titleTextView.setText(articleName);
        authorTextView.setText(articleAuthor);
        contentTextView.setText(articleText);
    }
}