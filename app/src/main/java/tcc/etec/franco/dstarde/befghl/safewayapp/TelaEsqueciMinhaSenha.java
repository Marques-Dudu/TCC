package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TelaEsqueciMinhaSenha extends AppCompatActivity {

    //Objetos XML
    EditText edEmailLinkRecuperacao;
    Button btEnviarLinkRecuperacao;
    FirebaseAuth auth;

    // Timer responsável por controlar o cooldown
    CountDownTimer contador;
    // Constante que define o tempo de cooldown em milissegundos (60s)
    final int TEMPO_COOLDOWN_MS = 60 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_esqueci_minha_senha);


        edEmailLinkRecuperacao = findViewById(R.id.edtEmailLinkRecuperacao);
        btEnviarLinkRecuperacao = findViewById(R.id.btnEnviarLinkRecuperação);
        auth = FirebaseAuth.getInstance();

        btEnviarLinkRecuperacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmailLinkRecuperacao.getText().toString().trim();

                // Verificando se o e-mail é válido
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Snackbar snackbar = Snackbar.make(v, "Insira um e-mail válido", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    return;  // Para a execução caso o e-mail não seja válido
                }
                // Verifica se o e-mail foi preenchido
                if (email.isEmpty()) {
                    Toast.makeText(TelaEsqueciMinhaSenha.this, "Por favor, digite um e-mail válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Desativa o botão e inicia a contagem de cooldown
                iniciarCooldown();

                // Envia o link de recuperação de senha para o e-mail
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sucesso: mostra mensagem de confirmação
                                Toast.makeText(TelaEsqueciMinhaSenha.this, "Link enviado para " + email, Toast.LENGTH_LONG).show();
                            } else {
                                // Falha: mostra erro e reativa o botão
                                Toast.makeText(TelaEsqueciMinhaSenha.this, "Erro ao enviar link. Verifique o e-mail.", Toast.LENGTH_LONG).show();
                                cancelarCooldown(); // volta botão ao normal
                            }
                        });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Inicia a contagem de 60 segundos e atualiza o texto do botão
    private void iniciarCooldown() {
        // Desativa o botão
        btEnviarLinkRecuperacao.setEnabled(false);

        // Cria o temporizador que atualiza a cada 1 segundo
        contador = new CountDownTimer(TEMPO_COOLDOWN_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Atualiza o texto do botão com os segundos restantes
                long segundosRestantes = millisUntilFinished / 1000;
                btEnviarLinkRecuperacao.setText("Aguarde (" + segundosRestantes + "s)");
            }

            @Override
            public void onFinish() {
                // Quando terminar, reativa o botão e restaura o texto
                btEnviarLinkRecuperacao.setText("Enviar Email de Recuperação");
            }
        }.start(); // Inicia o timer
    }

    // Caso aconteça um erro, essa função cancela o cooldown
    private void cancelarCooldown() {
        if (contador != null) {
            contador.cancel(); // Para a contagem
        }
        // Reativa o botão e restaura o texto original
        btEnviarLinkRecuperacao.setEnabled(true);
        btEnviarLinkRecuperacao.setText("Enviar Email de Recuperação");
    }

    // Se o usuário sair da tela, garante que o timer pare corretamente
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( contador != null) {
            contador.cancel(); // Evita vazamentos de memória
        }
    }
}