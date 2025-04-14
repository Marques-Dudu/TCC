package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class TelaLogin extends AppCompatActivity {

    //Objetos XML
    TextView btIrCadastrar;
    EditText editEmail, editSenha;
    Button btLogin;
    ImageButton btMostarSenha;
    ProgressBar progressBar;
    String[] mensagens = {"Preencha todos os campos.","Login realizado com êxito!"};

    //variavel auxiliar da Progress bar
    int i = 0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);

        //Iniciando Componentes
        IniciarComponentes();

        //Verificando conexão com Internet
        if (!isConnectedToInternet()) {
            // Cria e exibe o AlertDialog
            showNoInternetDialog();
        }

        //Botao para tornar a senha visivel ou nao no input
        btMostarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSenha.getInputType() == 129){ //129 é o código para EditText de Password (senha), ou seja, o texto fica escondido
                    btMostarSenha.setImageResource(R.drawable.olho);
                    editSenha.setInputType(1); // 1 é o código para "textVisiblePassword", faz o texto ficar visivel
                }
                else {
                    btMostarSenha.setImageResource(R.drawable.olho_fechado);
                    editSenha.setInputType(129);
                }
            }
        });


        //Preparando click do botão Login
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.parseColor("#EF5454"));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    AutenticarUsuário(v);
                }
            }
        });

        //Preparando o click do link para tela de cadastro
        btIrCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ir para outra tela
                Intent irParaTelaCadastro = new Intent(TelaLogin.this , TelaCadastro.class);
                startActivity(irParaTelaCadastro);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void AutenticarUsuário(View view){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email , senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Snackbar snackbar = Snackbar.make(view, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.parseColor("#429549"));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    //fazendo a progress bar aparecer e funcionar
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (i < 100){
                                i+=1;
                                try {
                                    Thread.sleep(20);
                                }
                                catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                handler.post(new Runnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        progressBar.setProgress(i);

                                        if (i == progressBar.getMax())
                                        {

                                            TelaPrincipal();

                                        }

                                    }

                                });
                            }
                        }
                    }).start();
                }
                else {
                    String erro;
                    try {
                        throw task.getException();
                    }
                    catch (Exception e){
                        erro = "Erro ao realizar o login.";
                        Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                }
            }
        });
    }

    //criando ciclo de vida
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            TelaPrincipal();
        }
    }

    private void TelaPrincipal(){

        Intent intent = new Intent(TelaLogin.this , TelaIndexActivity.class);
        startActivity(intent);
        finish();

    }
    private void IniciarComponentes(){

        //Referenciando XML ao Java
        btIrCadastrar = findViewById(R.id.btnIrProCadastro);
        editEmail = findViewById(R.id.edtEmailLogin);
        editSenha = findViewById(R.id.edtSenhaLogin);
        btLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBarLogin);
        btMostarSenha = findViewById(R.id.btnMostarSenha);

    }

    /**
     * Método para verificar se o dispositivo está conectado à internet.
     *
     * @return true se estiver conectado à internet, false caso contrário.
     */
    //tradução variavel = estaConectadoNaInternet
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Verifica a conexão de rede
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // Se a conexão não for nula e estiver conectada à internet
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Exibe o AlertDialog para informar ao usuário que ele está sem internet.
     */
    // tradução variavel = mostrarDialogoSemInternet
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sem Conexão")
                .setMessage("Você não está conectado à internet. O aplicativo será fechado.")
                .setCancelable(false)  // Não permite que o usuário cancele
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Fecha o aplicativo após o usuário confirmar
                        finishAffinity(); // Fecha o app de forma definitiva
                    }
                });

        // Exibe o dialog
        builder.create().show();
    }

//bloqueando botão de voltar
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //vazio para bloquear o botão de voltar
    }
}