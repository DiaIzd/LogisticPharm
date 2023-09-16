package com.example.logisticpharm.userGUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logisticpharm.R;
import com.example.logisticpharm.article.ArticleAdapter;
import com.example.logisticpharm.article.ArticleAdd;
import com.example.logisticpharm.article.ArticleShow;
import com.example.logisticpharm.authentication.LoginActivity;

import com.example.logisticpharm.model.Article;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ArticleUser extends AppCompatActivity {


    private BottomNavigationView bottomNavigationViewUser;
    RecyclerView recviewArticleUser;
    ArticleUserAdapter  adapterArticleUser;
    SearchView searchViewArticle;
    FloatingActionButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_user);
        setTitle("");
        searchViewArticle = findViewById(R.id.searchViewArticleUser);
        bottomNavigationViewUser = findViewById(R.id.bottomNavigationViewUser);
        bottomNavigationViewUser.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.action_profile){
                    startActivity(new Intent(ArticleUser.this, UserProfile.class));
                    return true;
                } else if (itemId == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ArticleUser.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }else if(itemId == R.id.action_articleUser){
                    startActivity(new Intent(ArticleUser.this, ArticleUser.class));
                    return true;
                }else if(itemId == R.id.action_medicine) {
                    startActivity(new Intent(ArticleUser.this, RecyclerViewUser.class));
                    return true;
                }else if(itemId == R.id.action_favorite) {
                    startActivity(new Intent(ArticleUser.this, UserFavorite.class));
                    return true;
                }else
                    return false;

                //case R.id.action_article:
                //    startActivity(new Intent(RecylcerShow.this, SearchActivity.class));
                //   return true;


            }
        });
        searchViewArticle.clearFocus();
        searchViewArticle.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s){
                filterList(s);
                return true;
            }

            private void filterList(String s) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Article");

                String query = s.toUpperCase(); // Konwertujemy zapytanie na małe litery

                Query filteredQuery = reference.orderByChild("medicineName")
                        .startAt(query)
                        .endAt(query + "\uf8ff");

                FirebaseRecyclerOptions<Article> options =
                        new FirebaseRecyclerOptions.Builder<Article>()
                                .setQuery(filteredQuery, Article.class)
                                .build();
                adapterArticleUser=new ArticleUserAdapter(options);
                adapterArticleUser.startListening();
                recviewArticleUser.setAdapter(adapterArticleUser);
            }
        });

        recviewArticleUser=(RecyclerView)findViewById(R.id.recviewArticleUser);
        recviewArticleUser.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Article> options =
                new FirebaseRecyclerOptions.Builder<Article>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Article"), Article.class)
                        .build();

        adapterArticleUser=new ArticleUserAdapter(options);
        recviewArticleUser.setAdapter(adapterArticleUser);


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterArticleUser.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterArticleUser.stopListening();
    }





}
