package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class TelaSettings extends AppCompatActivity {

    // TextView onde será exibida a saudação
    private TextView txUserSettings;

    // Instâncias do Firebase
    private FirebaseFirestore banco;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa componentes Firebase
        banco = FirebaseFirestore.getInstance();
        autenticacao = FirebaseAuth.getInstance();

        // Associa o TextView com o ID do XML
        txUserSettings = findViewById(R.id.txtUserSettings);

        // Chama a função para montar e exibir a saudação
        exibirSaudacao();

        // Tela direcionada a configuração do usuario



    }

    // Função para buscar o nome do usuário e montar a saudação
    private void exibirSaudacao() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();

        // Verifica se o usuário está logado
        if (usuarioAtual == null) {
            txUserSettings.setText("Olá, visitante!");
            return;
        }

        String uid = usuarioAtual.getUid();

        // Busca o documento do usuário no Firestore
        banco.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documento -> {
                    if (documento.exists()) {
                        // Pega o campo "nome" ou usa "Usuário" como padrão
                        String nomeUsuario = documento.getString("Nome");
                        if (nomeUsuario == null || nomeUsuario.isEmpty()) {
                            nomeUsuario = "Usuário";
                        }

                        // Determina a saudação com base na hora do sistema
                        String saudacao = obterSaudacaoAtual();

                        // Exibe o resultado final
                        txUserSettings.setText(saudacao + ", " + nomeUsuario + "!");
                    } else {
                        txUserSettings.setText("Usuário não encontrado.");
                    }
                })
                .addOnFailureListener(e -> txUserSettings.setText("Erro ao carregar usuário."));
    }

    // Retorna a saudação adequada com base na hora atual
    private String obterSaudacaoAtual() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);

        if (hora >= 5 && hora <= 11) {
            return "Bom dia";
        } else if (hora >= 12 && hora <= 17) {
            return "Boa tarde";
        } else if (hora >= 18 && hora <= 23) {
            return "Boa noite";
        } else {
            return "Boa madrugada";
        }
    }

}