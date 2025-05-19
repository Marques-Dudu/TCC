package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TelaCadastro extends AppCompatActivity {

    //Objetos XML
    TextView btnVoltar, btnPorqDados;
    ProgressBar progressBar;
    private EditText editNome, editDataNascimento, editTelefone, editEmail, editSenha, editConfirmarSenha;

    private Button btCadastrar;

    private ImageButton btnCalendario, btMostrarSenha;

    //variaveis auxiliares
    String usuarioId;

    String[] mensagens = {"Preencha todos os campos.", "Cadastrado(a) com êxito."};

    int i = 0;

    private boolean isUpdating = false; // Flag para evitar loops infinitos

    private String email, senha, senhaConfirmar, telefone, dataNascimento, nome;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(tcc.etec.franco.dstarde.befghl.safewayapp.R.layout.activity_tela_cadastro);

        //Verificando conexão com Internet
        if (!isConnectedToInternet()) {
            // Cria e exibe o AlertDialog
            showNoInternetDialog();
        }

        //Iniciando Componentes XML a OBJETO JAVA
        IniciarComponentes();

        //Mascarando EditText no padrão de telefone brasileiro
        editTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            }

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

        //Exibir calendário para o usuário escolher o ano de nascimento
        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH); // 0 = Janeiro
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TelaCadastro.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Formatando a data como dd/mm/aaaa
                                String dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                                editDataNascimento.setText(dataFormatada);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

//Mostrando regras da senha ao usuario clicar no EditText
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_dica, null);
// Cria o PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
// Remove sombra no fundo (opcional)
        popupWindow.setElevation(8);

        //Preprarando botão
        editSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Mostra o popup acima do EditText
                    popupWindow.showAsDropDown(editSenha, 420, -editSenha.getHeight() + 100);
                } else {
                    popupWindow.dismiss(); // Fecha o popup ao perder o foco
                }
            }
        });

        //Botao para tornar a senha visivel ou nao no input
        btMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSenha.getInputType() == 129) { //129 é o código para EditText de Password (senha), ou seja, o texto fica escondido
                    btMostrarSenha.setImageResource(R.drawable.olho);
                    editSenha.setInputType(1); // 1 é o código para "textVisiblePassword", faz o texto ficar visivel
                } else {
                    btMostrarSenha.setImageResource(R.drawable.olho_fechado);
                    editSenha.setInputType(129);
                }
            }
        });

        //Botão Sobre os Dados
        btnPorqDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irTelaPorqDados = new Intent(TelaCadastro.this, TelaUsoDeDados.class);
                startActivity(irTelaPorqDados);
            }
        });

        //botão cadastrar
        btCadastrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                nome = editNome.getText().toString();
                telefone = editTelefone.getText().toString();
                dataNascimento = editDataNascimento.getText().toString();
                email = editEmail.getText().toString();
                senha = editSenha.getText().toString();
                senhaConfirmar = editConfirmarSenha.getText().toString();

                //TRATAMENTO DE CAMPOS E VALIDAÇÃO DE DADOS PARA CADASTRO
                // tratamento de campos vazios
                if (nome.isEmpty() || telefone.isEmpty() || dataNascimento.isEmpty() || email.isEmpty() || senha.isEmpty() || senhaConfirmar.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
                // Validação de nome
                else if (nome.length() < 3) {
                    editNome.setError("Insira um nome válido");
                }
                //Validação de idade mínima e idade máxima
                else if (!validarIdade(dataNascimento, v)) {
                    //vazio, porque os avisos ja estão na função
                }
                //Tratamento de validação de Telefone
                else if (!ValidarTelefone(telefone)) {
                    editTelefone.setError("Número de celular inválido");
                    editTelefone.requestFocus();
                }
                //Tratamento para comparar senhas
                else if (!senha.equals(senhaConfirmar)) {
                    editConfirmarSenha.setError("Senhas não correspendentes");
                    editConfirmarSenha.requestFocus();
                }
                //Chama a função de cadastro
                else {

                    MostrarCaixaDeDialogo(v);

                }
            }
        });

        //Fazendo o clique do botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TelaCadastro.this, "Voltando para área de login.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TelaCadastro.this, TelaLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Função para cadastrar usuario
    private void CadastrarUsuario(View v) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && !currentUser.isAnonymous()) {
            // Usuário já logado (ex: Google), vincula senha
            String senha = editSenha.getText().toString();
            String email = currentUser.getEmail();

            AuthCredential credential = EmailAuthProvider.getCredential(email, senha);

            currentUser.linkWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Senha vinculada com sucesso, salva os dados no Firestore
                    SalvarDadosUsuario();

                    // Feedback para o usuário
                    Snackbar snackbar = Snackbar.make(v, "Senha vinculada com sucesso!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.parseColor("#429549"));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    // Vai para tela principal
                    TelaPrincipal();
                } else {
                    // Erro ao vincular senha
                    String erro = "Erro ao vincular senha: " + task.getException().getMessage();
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        } else {
            // Usuário não logado, cria conta normalmente
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.parseColor("#429549"));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    TelaPrincipal();
                } else {
                    String erro = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editSenha.setError("A senha criada não segue os critérios de segurança");
                        editSenha.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editEmail.setError("O Email fornecido já está cadastrado");
                        editEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email digitado é inválido.";
                        editEmail.setError("O Email digitado é inválido.");
                        editEmail.requestFocus();
                    } catch (Exception e) {
                        erro = "Ocorreu algum erro ao cadastrar.\nConfira se os dados foram digitados corretamente.";
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, 5000);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        }
    }


    private void TelaPrincipal() {

        Intent intent = new Intent(TelaCadastro.this, TelaIndexActivity.class);
        startActivity(intent);
        finish();
    }

    // Salvando os dados no Firestore
    @SuppressLint("SuspiciousIndentation")
    private void SalvarDadosUsuario() {

        FirebaseFirestore bd = FirebaseFirestore.getInstance();

        //Iniciando Mapa de Banco
        HashMap<String, Object> usuario = new HashMap<>();
        usuario.put("Nome", nome);
        usuario.put("Telefone", telefone);
        usuario.put("DataNasc", dataNascimento);
        usuario.put("Email", email);
        usuario.put("Senha", senha);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DocumentReference documentReference = bd.collection("usuarios").document(usuarioId);
        documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("bd", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("bd_error", "Erro ao salvar os dados" + e);
            }
        });


    }


    //Referenciação
    public void IniciarComponentes() {
        btCadastrar = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.btnCadastrar);
        btnVoltar = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.btnVoltarParaLogin);
        btnCalendario = findViewById(R.id.btnCalendario);
        btMostrarSenha = findViewById(R.id.btnMostrarSenhaCad);
        btnPorqDados = findViewById(R.id.btnPorqDados);
        editNome = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtNomeCadastro);
        editDataNascimento = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtDataNascimento);
        editTelefone = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtTelefone);
        editEmail = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtEmailCadastro);
        editSenha = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtSenhaCadastro);
        editConfirmarSenha = findViewById(tcc.etec.franco.dstarde.befghl.safewayapp.R.id.edtRepetirSenhaCadastro);
        progressBar = findViewById(R.id.progressBarCadastro);
    }

    // Função para validar a idade com base na data
    private boolean validarIdade(String dataNascimento, View v) {
        boolean bool;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataNasc = sdf.parse(dataNascimento);
            Calendar calendar = Calendar.getInstance();
            int idade = calcularIdade(dataNasc);

            if (idade < 10) {
                bool = false;
                Snackbar snackbar = Snackbar.make(v, "A idade mínina permitida é 10 anos.", 5000);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                snackbar.show();
            } else if (idade > 100) {
                bool = false;
                Snackbar snackbar = Snackbar.make(v, "Data de nascimento inválida.", 5000);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                snackbar.show();
            } else {
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

    //confirmação botão de voltar
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Desloga o usuário para evitar que a checagem na TelaLogin redirecione para cadastro novamente
        FirebaseAuth.getInstance().signOut();

        // Volta para TelaLogin, limpando a pilha para evitar voltar pra tela de cadastro
        Intent intent = new Intent(TelaCadastro.this, TelaLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
