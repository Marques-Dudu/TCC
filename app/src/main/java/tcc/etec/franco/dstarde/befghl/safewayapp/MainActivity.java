package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.content.Intent;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // Inicializa Firebase e AppCheck como você já faz
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        // Verifica usuário atual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Usuário logado, verifica se cadastrado
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Usuário cadastrado → TelaPrincipal
                            startActivity(new Intent(MainActivity.this, TelaIndexActivity.class));
                        } else {
                            // Usuário não cadastrado → TelaCadastro
                            startActivity(new Intent(MainActivity.this, TelaCadastro.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Erro ao acessar Firestore → TelaLogin
                        startActivity(new Intent(MainActivity.this, TelaLogin.class));
                        finish();
                    });
        } else {
            // Usuário não logado → TelaLogin
            new Handler().postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, TelaLogin.class));
                finish();
            }, 2000); // mantém o delay para mostrar a splash
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
