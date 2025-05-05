package com.example.teste7;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView nameTextView, descTextView;
    private ImageView iconImageView;
    private Button callButton, btLocalizacao;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3871c1")));
            getSupportActionBar().setDisplayShowTitleEnabled(true); // mostra o título
            getSupportActionBar().setTitle("Voltar");
        }


        nameTextView = findViewById(R.id.nameTextView);
        descTextView = findViewById(R.id.descriptionTextView);
        iconImageView = findViewById(R.id.iconImageView);
        callButton = findViewById(R.id.callButton);
        btLocalizacao = findViewById(R.id.btnLocalizacao);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        int icon = intent.getIntExtra("icon", 0);
        phone = intent.getStringExtra("phone");
        String description = intent.getStringExtra("description");

        nameTextView.setText(name);
        descTextView.setText(description);
        iconImageView.setImageResource(icon);

        // Quando o botão de discagem for clicado
        callButton.setOnClickListener(v -> {
            // Aplica a animação de clique
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);
            v.startAnimation(animation);
            // Abre o discador com o número da autoridade
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));  // Número de telefone
            startActivity(callIntent);  // Inicia a atividade do discador
        });




        btLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipoAutoridade = name;
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(tipoAutoridade));

                // Não força o Maps, deixa o Android decidir o melhor app
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(DetailActivity.this, "Nenhum app de mapas encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Fecha a DetailActivity e volta para a MainActivity
        return true;
    }
}

