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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TelaCadastro extends AppCompatActivity {

    //Objetos XML
    TextView btnVoltar;
    ProgressBar progressBar;
    private EditText editNome, editDataNascimento, editTelefone, editEmail, editConfirmarEmail, editSenha, editConfirmarSenha;
    private RadioButton rbMasculino, rbFeminino, rbOutro;
    private Button btCadastrar;

    //variaveis auxiliares
    String usuarioId;

    String[] mensagens = {"Preencha todos os campos.", "Cadastrado(a) com êxito."};

    int i = 0;

    private boolean isUpdating = false; // Flag para evitar loops infinitos

    private String urlTelefone, telefone;


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

        //Iniciando Componentes XML a OBJETO JAVA
        IniciarComponentes();

        //Mascarando EditText de Data no padrãp dd/mm/aaaa
        editDataNascimento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Não precisamos fazer nada aqui.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Aplica a máscara dd/mm/aaaa
                if (charSequence.length() == 2 || charSequence.length() == 5) {
                    if (start != 2 && start != 5) {
                        editDataNascimento.setText(charSequence.toString() + "/");
                        editDataNascimento.setSelection(charSequence.length() + 1);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Não precisamos fazer nada aqui.
            }
        });

        //Mascarando EditText no padrão de telefone brasileiro
        editTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (isUpdating) {
                    return; // Previne o loop infinito
                }
                String text = editable.toString();
                if (text.length() > 0) {
                    // Remove todos os caracteres não numéricos
                    text = text.replaceAll("[^\\d]", "");
                    // Começa a atualização do texto
                    isUpdating = true;
                    // Formata a máscara de celular
                    if (text.length() <= 2) {
                        text = "(" + text;
                    } else if (text.length() <= 6) {
                        text = "(" + text.substring(0, 2) + ") " + text.substring(2);
                    } else if (text.length() <= 10) {
                        text = "(" + text.substring(0, 2) + ") " + text.substring(2, 7) + "-" + text.substring(7);
                    } else {
                        text = "(" + text.substring(0, 2) + ") " + text.substring(2, 7) + "-" + text.substring(7, 11);
                    }
                    // Atualiza o texto com a máscara
                    editable.replace(0, editable.length(), text);
                    // Garantir que o cursor esteja no final do texto
                    editTelefone.setSelection(text.length());
                    // Finaliza a atualização
                    isUpdating = false;
                }
            }
        });

        //botão cadastrar
        btCadastrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String nome = editNome.getText().toString();
                telefone = editTelefone.getText().toString();
                String dataNascimento = editDataNascimento.getText().toString();
                String email = editEmail.getText().toString();
                String emailConfirmar = editConfirmarEmail.getText().toString();
                String senha = editSenha.getText().toString();
                String senhaConfirmar = editConfirmarSenha.getText().toString();

                //TRATAMENTO DE CAMPOS E VALIDAÇÃO DE DADOS PARA CADASTRO
                // tratamento de campos vazios
                if (nome.isEmpty() || telefone.isEmpty() || dataNascimento.isEmpty() || email.isEmpty() || emailConfirmar.isEmpty() || senha.isEmpty() || senhaConfirmar.isEmpty())
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
                //Validação de idade mínima e idade máxima
                else if (!validarIdade(dataNascimento)) {

                }
                //Tratamento de validação de Telefone
                else if (!ValidarTelefone(telefone)) {
                        editTelefone.setError("Número de celular inválido");
                        editTelefone.requestFocus();
                    }

                //Tratamento força da senha
                else if (!validarSenha(senha)){
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
                // Exibir a caixa de dialógo e na própia caixa realiza o cadastro ou não
                else {
                    MostrarCaixaDeDialogo(v);
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
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(i);
                                            if (i == progressBar.getMax()){
                                                TelaPrincipal();
                                            }
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    //tratando excessões
                    else{
                        String erro;
                        try {
                            throw task.getException();
                        }
                        catch (FirebaseAuthUserCollisionException e){
                            erro = "O Email tentado ja está cadastrado.";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erro = "Email digitado é inválido.";
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

    private void TelaPrincipal(){

        Intent intent = new Intent(TelaCadastro.this , TelaIndexActivity.class);
        startActivity(intent);
        finish();
    }
        // Salvando os dados no FireStone
    private void SalvarDadosUsuario(){
        String nome = editNome.getText().toString();


        String telefone = editTelefone.getText().toString();
        String generoEscolhido;

        //Tratamento para checar qual gênero o usuario escolheu
        if (rbFeminino.isChecked()){
            generoEscolhido = "Feminino";
        }
        else if (rbMasculino.isChecked()) {
            generoEscolhido = "Masculino";
        }
        else {
            generoEscolhido = "Outro";
        }


                //deste arquivo

        //Iniciando o banco de dados firestone
        FirebaseFirestore bd = FirebaseFirestore.getInstance();

        //Iniciando Mapa de Banco
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);
        usuarios.put("telefone",telefone);
        usuarios.put("genero",generoEscolhido);


                // deste arquivo

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = bd.collection("Usuarios").document(usuarioId);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("bd", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("bd_error", "Erro ao salvar os dados" + e.toString());
            }
        });

    }


    //Referenciação
    public void IniciarComponentes(){
        btCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltar = findViewById(R.id.btnVoltarParaLogin);
        editNome = findViewById(R.id.edtNomeCadastro);
        editDataNascimento = findViewById(R.id.edtDataNascimento);
        editTelefone = findViewById(R.id.edtTelefone);
        editEmail = findViewById(R.id.edtEmailCadastro);
        editConfirmarEmail = findViewById(R.id.edtRepetirEmailCadastro);
        editSenha = findViewById(R.id.edtSenhaCadastro);
        editConfirmarSenha = findViewById(R.id.edtRepetirSenhaCadastro);
        rbMasculino = findViewById(R.id.rdbMasculino);
        rbFeminino = findViewById(R.id.rdbFeminino);
        rbOutro = findViewById(R.id.rdbOutro);
        progressBar = findViewById(R.id.progressBarCadastro);
    }

    // Função para validar a idade com base na data
    public boolean validarIdade(String dataNascimento) {
        boolean bool;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataNasc = sdf.parse(dataNascimento);
            Calendar calendar = Calendar.getInstance();
            int idade = calcularIdade(dataNasc);

            if (idade < 10 ){
                bool = false;
                editDataNascimento.setError("A idade mínina permitida é 10 anos.");
                editDataNascimento.requestFocus();
            } else if (idade > 100) {
                bool = false;
                editDataNascimento.setError("Data de nascimento inválida.");
                editDataNascimento.requestFocus();
            }
            else{
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }
    // Função para calcular a idade com base na data de nascimento
    private int calcularIdade(Date dataNascimento) {
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dataNascimento);

        Calendar hoje = Calendar.getInstance();

        int idade = hoje.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (hoje.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (hoje.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && hoje.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            idade--;
        }
        return idade;
    }

    // Função para verificar a validade do número de telefone
    private boolean ValidarTelefone(String phoneNumber) {
        // Expressão regular para validar o celular no formato (XX) 9XXXX-XXXX
        String regex = "^\\(\\d{2}\\) 9\\d{4}-\\d{4}$";

        // Cria um objeto Pattern com a regex
        Pattern pattern = Pattern.compile(regex);
        // Cria um Matcher que verifica se a string corresponde à regex
        Matcher matcher = pattern.matcher(phoneNumber);

        // Retorna true se o número corresponder à regex
        return matcher.matches();
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
        if (!senha.matches(".*[!@#$%_^&*(),.?/'^~´`\":{}|<>].*")) {
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

    //tradução função = estaConectadoNaInternet
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

//Criando funções para Caixa de Dialogo
private void MostrarCaixaDeDialogo(View v) {
    // Criando a mensagem formal com links
    String message = "Ao se cadastrar, você concorda com os nossos " +
            "Termos de Uso e com nossa " +
            "Política de Privacidade." +
            "\nPor favor, leia atentamente ambos os documentos antes de continuar. O seu cadastro estará sujeito aos " +
            "termos descritos, e você autoriza o uso das suas informações conforme a nossa política de privacidade.";

    // Criando o SpannableString
    SpannableString spannableMessage = new SpannableString(message);

    // Tornando "Termos de Uso" clicável
    int termsStart = message.indexOf("Termos de Uso");
    int termsEnd = termsStart + "Termos de Uso".length();
    spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#0000ff")), termsStart, termsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor do link
    spannableMessage.setSpan(new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            // Redirecionar para a tela de Termos de Uso
            Toast.makeText(TelaCadastro.this, "Abrindo Termos de Uso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TelaCadastro.this, TermosDeUso.class));
        }
    }, termsStart, termsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    // Tornando "Política de Privacidade" clicável
    int privacyStart = message.indexOf("Política de Privacidade");
    int privacyEnd = privacyStart + "Política de Privacidade".length();
    spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#0000ff")), privacyStart, privacyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor do link
    spannableMessage.setSpan(new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            // Redirecionar para a tela de Política de Privacidade
            Toast.makeText(TelaCadastro.this, "Abrindo Política de Privacidade", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TelaCadastro.this, PoliticaDePrivacidade.class));
        }
    }, privacyStart, privacyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    // Criando o AlertDialog com o texto clicável dentro da própria caixa de diálogo
    AlertDialog.Builder builder = new AlertDialog.Builder(TelaCadastro.this);
            builder.setTitle("Politica de Privacida e Termos de Uso.")
            .setMessage("Por favor, leia atentamente os termos abaixo:")
            .setCancelable(false)
            .setView(createTextView(spannableMessage))
            .setPositiveButton("Aceitar", (dialog, id) -> {
                // Ação quando o usuário confirma
                    CadastrarUsuario(v);
            })
            .setNegativeButton("Recusar", (dialog, id) -> {
                // Se o usuário clicar em "Cancelar", mostrar um Toast
                Toast.makeText(TelaCadastro.this, "Cadastro cancelado, precisamos que concorde com os Termos.", Toast.LENGTH_SHORT).show();
            });
    // Exibindo o AlertDialog
    AlertDialog alertDialog = builder.create();

    // Modificando a cor dos botões de "Confirmar" e "Cancelar"
    alertDialog.setOnShowListener(dialog -> {
        // Alterando a cor do botão "Confirmar" (positivo)
        TextView positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#0000ff")); // Alterar a cor do botão

        // Alterando a cor do botão "Cancelar" (negativo)
        TextView negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#ff0000")); // Alterar a cor do botão
    });

    alertDialog.show();

}
    private TextView createTextView(SpannableString spannableMessage) {
        // Criando um TextView para exibir o texto com os links clicáveis
        TextView textView = new TextView(TelaCadastro.this);
        textView.setText(spannableMessage);
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // Permite que os links sejam clicáveis
        textView.setLinkTextColor(Color.parseColor("#0000ff")); // Cor dos links
        textView.setTextSize(14); // Ajuste o tamanho da fonte conforme necessário
        return textView;
    }

//bloquear botão de voltar
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // vazio para bloquear
    }
}