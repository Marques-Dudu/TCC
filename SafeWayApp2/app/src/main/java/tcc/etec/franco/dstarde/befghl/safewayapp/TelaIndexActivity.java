package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import tcc.etec.franco.dstarde.befghl.safewayapp.databinding.ActivityTelaIndexBinding;

public class TelaIndexActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTelaIndexBinding binding;

    // declarando o drawerLayout globalmente para todos os metodos terem acesso
    private DrawerLayout drawer;

    /*Declarando o navcontroler globalmente para todos os metodos terem acesso,
    esta variavel controla a navegacao dos fragments no Navigation Drawer */
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelaIndexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

                //Verificando conexão com Internet
        if (!isConnectedToInternet()) {
            // Cria e exibe o AlertDialog
            showNoInternetDialog();
        }
        
        setSupportActionBar(binding.appBarTelaIndex.toolbar);

        //linkando o a classe DrawerLayout do Java ao objeto drawerLayout do XML
          drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.Menu_Map)
                .setOpenableLayout(drawer)
                .build();

         navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tela_index);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        // Alterando o clique do item "chat" do menu para a activity_tela_chat
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if(id == R.id.Menu_Map){

                navController.navigate(R.id.Menu_Map);

            } else if (id == R.id.Menu_Chat) {

                Intent TelaChat = new Intent(this, TelaChat.class);
                startActivity(TelaChat);

                // Fecha o Navigation Drawer apos mudar de tela
                drawer.closeDrawer(GravityCompat.START);

            }

            return true;

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tela_index, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tela_index);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        // Verifica se a barra lateral esta aberta, o parametro passado serve para dizer que a barra esta no lado esquerdo da tela
        if (drawer.isDrawerOpen(GravityCompat.START)){

            // Se a barra lateral (Drawer) estiver aberta, sera fechada
            drawer.closeDrawer(GravityCompat.START);

        } else {
                // Criando um alert de confirmacao para sair
                new AlertDialog.Builder(TelaIndexActivity.this).setIcon(R.drawable.close_icon).setTitle("Deseja realmente sair?")
                        .setPositiveButton("Sim", ((dialog, which) -> finishAffinity()))
                        .setNegativeButton("Não",(dialog, which) -> dialog.dismiss()).show();

                }
    }
}

////Declarando uma variavel auxiliar, para saber se o Drawer esta aberto ou nao
//        int PaginaAtual;
//        // Recebendo o id do fragment atual, e verificando se é o de mapa (fragmento principal do app)
//        PaginaAtual = navController.getCurrentDestination().getId();
