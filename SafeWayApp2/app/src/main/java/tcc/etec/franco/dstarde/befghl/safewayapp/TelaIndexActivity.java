package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import android.os.Bundle;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import tcc.etec.franco.dstarde.befghl.safewayapp.databinding.ActivityTelaIndexBinding;
import tcc.etec.franco.dstarde.befghl.safewayapp.ui.ViewModel.LocalizacaoViewModel;

public class TelaIndexActivity extends AppCompatActivity {

    // Variaveis de configuracao do Navigation Drawer
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTelaIndexBinding binding;

    // declarando o drawerLayout globalmente para todos os metodos terem acesso
    private DrawerLayout drawer;

    /*Declarando o navcontroler globalmente para todos os metodos terem acesso,
    esta variavel controla a navegacao dos fragments no Navigation Drawer */
    NavController navController;

    // Variaveis para configuracao da localizacao
    private FusedLocationProviderClient HostLocation;
    private com.google.android.gms.location.LocationRequest RequisicaoLocation;
    private LocationCallback RespostaLocation;

    private double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelaIndexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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

        CheckPermissaoLocalizacao();
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


    // Solicitando localizacao para o usuario
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Variavel auxiliar de permissao da localizacao

     public void CheckPermissaoLocalizacao(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Criando o aviso de solicitacao da localizacao exata e aproximada
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);

        } else {

            // Metodo para captar a localizacao
            getLocation();

        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        /* Verifica se o requestCode e igual ao codigo que passei na funcao requestPermission(), isto e, se o usuario permitir o acesso
         a locazicao o requestCode sera igual ao que eu defini na variavel auxiliar, caso nao permita o codigo sera diferente */
       if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){

           if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

              getLocation();

           } else {

               View TelaSnackBar = findViewById(R.id.drawer_layout);
               Snackbar BarNotification = Snackbar.make(TelaSnackBar, "O aplicativo precisa de acesso a sua localização para funcionar!", Snackbar.LENGTH_INDEFINITE);
               BarNotification.setBackgroundTint(Color.parseColor("#EF5454"));
               BarNotification.setTextColor(Color.WHITE);
               BarNotification.setAction("Ok", v -> {

                   // Funcao para checar se o usuario recusou a solicitacao mais de uma vez (marcou como "nunca permitir")
                   if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) == false || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) == false){

                       new AlertDialog.Builder(this).setIcon(R.drawable.alert_icon).setTitle("Permissão necessária")
                               .setMessage("O aplicativo precisa de acesso a sua localização para funcionar! Vá até as configurações " +
                                "para ativá-la manualmente.").setPositiveButton("OK", (dialog, which) -> {

                                Intent ConfigApp = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                ConfigApp.setData(android.net.Uri.parse("package:" + getPackageName()));
                                startActivity(ConfigApp);

                       }).setNegativeButton("Sair",(dialog, which) -> {  finishAffinity(); }).show();

                   } else {
                       // Fecha a snackBar
                       BarNotification.dismiss();
                       // Mostrando novamente a solicitacao
                       CheckPermissaoLocalizacao();
                   }

               });
               BarNotification.show();

           }

       }

    }

    // funcao para configurar o host, criar a requisicao, e configurar a resposta (callBack)
    public void getLocation(){

        HostLocation = LocationServices.getFusedLocationProviderClient(this);

        // O metodo utilizado ja foi descontinuado, porem como o minSDK do app e 25, esta e a maneira compativel
        RequisicaoLocation = LocationRequest.create()
                .setWaitForAccurateLocation(false) // Define que o app nao precisa esperar a localizacao mais exata possivel, para atualizar
                .setInterval(1000) // Define o intervalo em que o app ira pegar a localizacao do usuario (milisegundos)
                .setFastestInterval(500) // Define o intervalo minimo que o app pode pegar a localizacao do usuario
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY) // Define que a localizacao sera pega via GPS
                .setMaxWaitTime(1000); // Define que o app deve esperar a localizacao mais aproximada para retornar


                // Configurando a chegada da localizacao (CallBack)
                RespostaLocation = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {

                        if(locationResult != null){

                            // ForEach, para cada coordenadas que chegar, sera armazenada em um ojeto da classe Location
                            for (Location localizacao: locationResult.getLocations()) {

                            double lat = localizacao.getLatitude();
                            double lon = localizacao.getLongitude();

                                Log.i(null, "Localizacao -> Latitude: "+localizacao.getLatitude()+" Longitude: "+localizacao.getLongitude()+".");

                                /* Istancia(cria) um objeto da nossa classe LocalizacaoViewModel, referencia a
                                 tela que provera os dados (this) e referencia para qual classe java ira os dados */
                                 LocalizacaoViewModel transporter = new ViewModelProvider(TelaIndexActivity.this).get(LocalizacaoViewModel.class);

                                 // Utiliza o setter para enviar a localizacao (objeto com latitude e longitude)
                                transporter.setActualLocation(localizacao);

                            }

                        }

                    }

                };

                // Verificando novamente se a solicitacao foi aceita, se sim, roda a funcao para captar a localizacao
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            HostLocation.requestLocationUpdates(RequisicaoLocation,RespostaLocation,Looper.getMainLooper());

        }


    }

    // funcao para deixar um aviso snack bar "para sempre" na index, caso o usuario nao permita a localizacao



    // Configurando o botao voltar do celular
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        // Verifica se a barra lateral esta aberta, o parametro passado serve para dizer que a barra esta no lado esquerdo da tela
        if (drawer.isDrawerOpen(GravityCompat.START)){

            // Se a barra lateral (Drawer) estiver aberta, sera fechada
            drawer.closeDrawer(GravityCompat.START);

        } else {
                // Criando um alert de confirmacao para sair
                new AlertDialog.Builder(TelaIndexActivity.this).setIcon(R.drawable.exit_icon).setTitle("Deseja realmente sair?")
                        .setPositiveButton("Sim", ((dialog, which) -> finishAffinity()))
                        .setNegativeButton("Não",(dialog, which) -> dialog.dismiss()).show();

                }


    }
}

