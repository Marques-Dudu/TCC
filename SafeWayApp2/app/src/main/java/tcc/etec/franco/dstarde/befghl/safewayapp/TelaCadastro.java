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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.github.rto.view.widget.MaskEditText;


public class TelaCadastro extends AppCompatActivity {

    //Objetos XML
    TextView btnVoltar;

    private EditText editNome, editDataNascimento, editCpf, editEmail, editConfirmarEmail, editSenha, editConfirmarSenha;
    private RadioButton rbMasculino, rbFeminino, rbOutro;
    private Button btCadastrar;

    private String urlCPF, cpf;

    String[] mensagens = {"Preencha todos os campos.", "Cadastrado(a) com êxito."};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);

        //Verificando conexão com Internet
        if (!isConnectedToInternet()) {
            // Cria e exibe o AlertDialog
            showNoInternetDialog();
        }

        MaskEditTe cpfEditText = new MaskEditText(this);
        cpfEditText.setMask("###.###.###-##"); // Máscara para CPF
        cpfEditText.setHint("Digite o CPF");

        // Colocar o MaskedEditText no layout (se necessário)
        LinearLayout layout = findViewById(R.id.layout);
        layout.addView(cpfEditText);

        //Iniciando Componentes XML a OBJETO JAVA
        IniciarComponentes();

        //botão cadastrar
        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString();
                cpf = editCpf.getText().toString();
                String dataNascimento = editDataNascimento.getText().toString();
                String email = editEmail.getText().toString();
                String emailConfirmar = editConfirmarEmail.getText().toString();
                String senha = editSenha.getText().toString();
                String senhaConfirmar = editConfirmarSenha.getText().toString();

                // tratamento de campos vazios
                if (nome.isEmpty() || cpf.isEmpty() || dataNascimento.isEmpty() || email.isEmpty() || emailConfirmar.isEmpty() || senha.isEmpty() || senhaConfirmar.isEmpty() )
                    {
                        Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                // Validação de nome
                else if (nome.length() < 3){
                    editNome.setError("Insira um nome válido");
                }

                //Tratamento de validação de CPF
                else if (cpf.length() != 11) {
                    editCpf.setError("Informe um CPF válido.");
                    editCpf.requestFocus();
                } else {
                    cpf = editCpf.getText().toString();
                    urlCPF = "https://api.invertexto.com/v1/validator?token=18718%7C6mnMnO0o3pqZRuYNPBkPL4Ooe8o87IDe&value="+cpf+"&type=cpf";
                    ValidarCPF(urlCPF);
                }

                //Tratamento força da senha
                if (!validarSenha(senha)){
                    editSenha.setError("Certifque que sua senha contenha:\n" +
                            "8 ou mais caracteres; \n" +
                            "Uma letra minúscula; \n" +
                            "Uma letra maiúscula \n" +
                            "Um caracter especial (!, @, #, etc)");
                    editSenha.requestFocus();
                }

                // tratamento para conferir se o email e senha foram digitados corretamente
                else if (!email.equals(emailConfirmar)) {
                    editConfirmarEmail.setError("Emails não correspondentes.");
                    editConfirmarEmail.requestFocus();
                }
                else if (!senha.equals(senhaConfirmar)) {
                    editConfirmarSenha.setError("Senhas não correspendentes");
                    editConfirmarSenha.requestFocus();
                }
                // Executar o cadastro do usuário
                else {
                    CadastrarUsuario(v);
                }


            }
        });

        //Fazendo o clique do botão Voltar
            btnVoltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Função para cadastrar usuario
        private void CadastrarUsuario(View v){
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            //Cadastro via Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //salvar dados do usuario no banco
                    SalvarDadosUsuario();


                    //mensagem de cadastro realizado com sucesso
                    if (task.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                    //tratando excessões
                    else{
                        String erro;
                        try {
                            throw task.getException();
                        }
                        catch (FirebaseAuthUserCollisionException e){
                            erro = "O email ja está cadastrado.";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erro = "Email inválido.";
                        }
                        catch (Exception e) {
                            erro = "Erro ao cadastrar.";
                        }
                        Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                }
            });
        }

        // Salvando os dados no FireStone
    private void SalvarDadosUsuario(){
        String nome = editNome.getText().toString();

        //Iniciando o banco de dados firestone
        FirebaseFirestore bd = FirebaseFirestore.getInstance();

        //Iniciando Mapa de Banco
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);

    }


    //Referenciação
    public void IniciarComponentes(){
        btCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltar = findViewById(R.id.btnVoltarParaLogin);
        editNome = findViewById(R.id.edtNomeCadastro);
        editDataNascimento = findViewById(R.id.edtDataNascimento);
        editCpf = findViewById(R.id.edtCPF);
        editEmail = findViewById(R.id.edtEmailCadastro);
        editConfirmarEmail = findViewById(R.id.edtRepetirEmailCadastro);
        editSenha = findViewById(R.id.edtSenhaCadastro);
        editConfirmarSenha = findViewById(R.id.edtRepetirSenhaCadastro);
        rbMasculino = findViewById(R.id.rdbMasculino);
        rbFeminino = findViewById(R.id.rdbFeminino);
        rbOutro = findViewById(R.id.rdbOutro);
    }

    //Verificação da Força da senha
    private boolean validarSenha(String senha) {
        // Verifica se a senha tem pelo menos 8 caracteres
        if (senha.length() < 8) {
            return false;
        }

        // Verifica se a senha contém pelo menos uma letra minúscula
        if (!senha.matches(".*[a-z].*")) {
            return false;
        }

        // Verifica se a senha contém pelo menos uma letra maiúscula
        if (!senha.matches(".*[A-Z].*")) {
            return false;
        }

        // Verifica se a senha contém pelo menos um número
        if (!senha.matches(".*\\d.*")) {
            return false;
        }

        // Verifica se a senha contém pelo menos um caractere especial
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }

        // Se passar todas as verificações, a senha é válida
        return true;
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

    //Função para usar API de validação de CPF
    private void ValidarCPF(String url){
        RequestQueue requisicao = Volley.newRequestQueue(this);
        StringRequest resposta = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject cpf = new JSONObject(response);
                    Log.e("teste",String.valueOf(cpf));
                        //criacao variavel auxiliar
                    String varAuxiliar = cpf.getString("valid");
                    if(!varAuxiliar.equals("true")){
                        editCpf.setError("CPF inválido.");
                        editCpf.requestFocus();
                    }


                } catch (JSONException e) {
                    Toast.makeText(TelaCadastro.this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TelaCadastro.this, "Erro ao buscar CPF", Toast.LENGTH_SHORT).show();
            }
        });
        requisicao.add(resposta);
    };



//bloquear botão de voltar
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // vazio para bloquear
    }
}