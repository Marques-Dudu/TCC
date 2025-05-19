package tcc.etec.franco.dstarde.befghl.safewayapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tcc.etec.franco.dstarde.befghl.safewayapp.databinding.ActivityTelaIndexBinding;
import tcc.etec.franco.dstarde.befghl.safewayapp.ui.Autoridades.AutoridadesFragment;
import tcc.etec.franco.dstarde.befghl.safewayapp.ui.ViewModel.LocalizacaoViewModel;

import com.yalantis.ucrop.UCrop;

public class TelaIndexActivity extends AppCompatActivity {

    // Variaveis de configuracao do Navigation Drawer
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTelaIndexBinding binding;

    // declarando o drawerLayout globalmente para todos os metodos terem acesso
    private DrawerLayout drawer;

    /*Declarando o navcontroler globalmente para todos os metodos terem acesso,
    esta variavel controla a navegacao dos fragments no Navigation Drawer */
    NavController navController;

    // Declarando a variavel que controla todos os componentes XML do drawer lateral (menu lateral)
    NavigationView navigationView;


    // Variaveis para configuracao da localizacao
    private FusedLocationProviderClient HostLocation;
    private com.google.android.gms.location.LocationRequest RequisicaoLocation;
    private LocationCallback RespostaLocation;

    // Variável de controle do botão voltar do celular
    private long backPressedTime;
    private Toast toastVoltar;


    // Variavel para configuracao da permissao da camera
    int CAMERA_PERMISSION_REQUEST_CODE = 2;


    // Variaveis para controle da requisicao
    int REQUEST_CODE_GALERY = 3;
    int REQUEST_CODE_CAMERA = 4;


    // Objetos do firebase
    FirebaseUser CurrentUser;
    String UserID;
    FirebaseFirestore dataBase;

    // Objeto para controle do ViewHeader
     View ViewHeader;


     // Imagem de perfil do usuario

    private ImageView ProfileImage;
    private Drawable imagemPadrao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProfileImage = findViewById(R.id.imgPerfilUsuario);
        imagemPadrao = ContextCompat.getDrawable(this, R.drawable.profile_icon);

        binding = ActivityTelaIndexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarTelaIndex.toolbar);


        //linkando a classe DrawerLayout do Java ao objeto drawerLayout do XML
        drawer = binding.drawerLayout;

         navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.Menu_Map,R.id.Menu_Autoridades
        )
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tela_index);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        // Inicializando componentes do firebase
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserID = CurrentUser.getUid();
        dataBase = FirebaseFirestore.getInstance();

        // Permanencia da imagem de perfil
            ProfileImage(UserID);


        // Inicializando o ViewHeader
        ViewHeader = navigationView.getHeaderView(0);


        // Alterando o clique do item "chat" do menu, para a activity_tela_chat
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.Menu_Map) {

                if (navController.getCurrentDestination().getId() != R.id.Menu_Map) {

                    navController.navigate(R.id.Menu_Map);
                    // Fecha o Navigation Drawer apos mudar de tela
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else if (id == R.id.Menu_Chat) {

                Intent TelaChat = new Intent(this, TelaChat.class);
                startActivity(TelaChat);

                // Fecha o Navigation Drawer apos mudar de tela
                drawer.closeDrawer(GravityCompat.START);

            } else if (id == R.id.Menu_Autoridades) {



                if (navController.getCurrentDestination().getId() != R.id.Menu_Autoridades) {
                    navController.navigate(R.id.Menu_Autoridades);
                }

                drawer.closeDrawer(GravityCompat.START);
            }

            return true;

        });


        // Retornando nome de usuario e email
        UserNameEmail(ViewHeader);

        // Botao settings
        ImageButton BtnSettings = ViewHeader.findViewById(R.id.ImgBtnSettings);

        BtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent TelaSettings = new Intent(TelaIndexActivity.this, tcc.etec.franco.dstarde.befghl.safewayapp.TelaSettings.class);
                startActivity(TelaSettings);
            }
        });


        // Configurando insercao da imagem de perfil do usuario
        ImageButton BtnFoto = ViewHeader.findViewById(R.id.ImgBtnFoto);

        BtnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemFoto();

            }
        });


        // Chamando a função de localização
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

    public void UserNameEmail(View viewHeader){


        TextView txtUser = viewHeader.findViewById(R.id.txtUser);
        TextView txtEmail = viewHeader.findViewById(R.id.txtEmail);

        dataBase.collection("usuarios").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                txtUser.setText(documentSnapshot.getString("Nome").toUpperCase());
                txtEmail.setText(documentSnapshot.getString("Email"));

            }
        });


    }


    // Solicitando localizacao para o usuario
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Variavel auxiliar de permissao da localizacao

    public void CheckPermissaoLocalizacao() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Criando o aviso de solicitacao da localizacao exata e aproximada
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getLocation();

                } else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                }
            } else {

                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                View TelaSnackBar = findViewById(R.id.drawer_layout);
                Snackbar BarNotification = Snackbar.make(TelaSnackBar, "O aplicativo precisa de acesso a sua localização para funcionar!", Snackbar.LENGTH_INDEFINITE);
                BarNotification.setBackgroundTint(Color.parseColor("#EF5454"));
                BarNotification.setTextColor(Color.WHITE);
                BarNotification.setAction("Ok", v -> {

                    // Funcao para checar se o usuario recusou a solicitacao mais de uma vez (marcou como "nunca permitir")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) == false || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION) == false) {

                        new AlertDialog.Builder(this).setIcon(R.drawable.alert_icon).setTitle("Permissão necessária")
                                .setMessage("O aplicativo precisa de acesso a sua localização para funcionar! Vá até as configurações " +
                                        "para ativá-la manualmente.").setPositiveButton("OK", (dialog, which) -> {

                                    Intent ConfigApp = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    ConfigApp.setData(android.net.Uri.parse("package:" + getPackageName()));
                                    startActivity(ConfigApp);

                                }).setNegativeButton("Sair", (dialog, which) -> {
                                    finishAffinity();
                                }).show();

                    }  else {
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


        // Verificando se a permissao da camera foi concedida
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                OpenCamera();

            } else {

                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) == false) {

                    new AlertDialog.Builder(this).setIcon(R.drawable.alert_icon).setTitle("Permissão necessária")
                            .setMessage("O aplicativo necessita de permissão para acessar a câmera! Vá até as configurações para ativá-la manualmente.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent ConfigApp = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            ConfigApp.setData(android.net.Uri.parse("package:" + getPackageName()));
                                            startActivity(ConfigApp);

                                        }
                                    }).show();

                }

            }

        }


    }

    // funcao para configurar o host, criar a requisicao, e configurar a resposta (callBack)
    public void getLocation() {

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

                if (locationResult != null) {

                    // ForEach, para cada coordenadas que chegar, sera armazenada em um ojeto da classe Location
                    for (Location localizacao : locationResult.getLocations()) {


                        Log.i(null, "Localizacao -> Latitude: " + localizacao.getLatitude() + " Longitude: " + localizacao.getLongitude() + ".");

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            HostLocation.requestLocationUpdates(RequisicaoLocation, RespostaLocation, Looper.getMainLooper());

        }


    }


// Funcao para mostrar um AlertDialog para a escolha entre camera e galeria
public void ItemFoto() {
    String[] Opcoes = {"Galeria", "Camera", "Remover Foto"};

    new AlertDialog.Builder(this)
            .setTitle("Escolha o método para inserção da foto")
            .setItems(Opcoes, (dialog, which) -> {
                if (which == 0) {
                    OpenGalery();
                } else if (which == 1) {
                    CheckPermissionCamera();
                } else if (which == 2) {
                    removerFoto();
                }
            }).show();
}

    private void removerFoto() {
        // 1. Limpa a imagem da tela
        ProfileImage.setImageDrawable(imagemPadrao);

        // 2. Remove a URL da imagem do Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("FotoPerfil", null); // ou FieldValue.delete()

        db.collection("usuarios").document(userId).update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Foto removida com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao remover a foto!", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Erro ao remover FotoPerfil", e);
                });
    }



    public void CheckPermissionCamera(){

            // Verificando permissao
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            } else {

                OpenCamera();

            }



    }




    public void OpenGalery(){

        Intent OpenGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(OpenGaleria, REQUEST_CODE_GALERY);

    }
    private Uri cameraImageUri;
    public void OpenCamera() {
        File imageFile = new File(getExternalCacheDir(), "camera_full.jpg");
        cameraImageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                imageFile
        );

        Intent OpenCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        OpenCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        OpenCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(OpenCamera, REQUEST_CODE_CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se a imagem foi selecionada da galeria com sucesso
        if (requestCode == REQUEST_CODE_GALERY && resultCode == RESULT_OK && data != null) {
            // Obtém o URI da imagem selecionada na galeria
            Uri sourceUri = data.getData();

            // Cria um URI de destino temporário onde a imagem recortada será salva
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));

            // Aguarda o componente ProfileImage estar pronto para capturar seu tamanho real
            ProfileImage.post(() -> {
                int width = ProfileImage.getWidth();
                int height = ProfileImage.getHeight();

                // Inicia a tela de recorte da imagem com as dimensões e proporções do perfil
                UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(width, height)
                        .withMaxResultSize(width, height)
                        .start(TelaIndexActivity.this);
            });

        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            // Caso a imagem tenha vindo da câmera, já temos o cameraImageUri previamente definido

            // Cria um destino temporário para a imagem recortada da câmera
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_camera.jpg"));

            // Aguarda o componente ProfileImage estar pronto para capturar seu tamanho
            ProfileImage.post(() -> {
                int width = ProfileImage.getWidth();
                int height = ProfileImage.getHeight();

                // Inicia o recorte da imagem tirada pela câmera com o UCrop
                UCrop.of(cameraImageUri, destinationUri)
                        .withAspectRatio(width, height)
                        .withMaxResultSize(width, height)
                        .start(TelaIndexActivity.this);
            });

        }
        // Quando o UCrop finaliza com sucesso o recorte da imagem
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            // Obtém o URI da imagem recortada
            Uri croppedImageUri = UCrop.getOutput(data);

            // Se o URI da imagem recortada for válido, envia para o Firebase
            if (croppedImageUri != null) {
                uploadImageGaleryToFireBaseStorage(croppedImageUri);
            }
        }
        // Caso ocorra algum erro durante o recorte com UCrop
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            // Obtém o erro e exibe uma mensagem ao usuário
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Erro ao cortar imagem: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public void uploadImageGaleryToFireBaseStorage(Uri imageGalery){

        StorageReference ImageReference = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");
        ImageReference.putFile(imageGalery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String ImagemUrl = uri.toString();

                        saveImagesUrlToFIRESTORE(ImagemUrl, UserID);

                    }
                });


            }
        });

    }

    public void uploadPhotoCameraToFireBaseStorage(Bitmap imageCamera){

        /* Convertendo a foto que o usuario tirar com a camera em .JPG, e depois armazenando
        em um array do tipo byte[], para que possa ser enviado ao FireBase Storage */

        ByteArrayOutputStream ConversorImage = new ByteArrayOutputStream();
        imageCamera.compress(Bitmap.CompressFormat.PNG, 100, ConversorImage);
        byte[] foto = ConversorImage.toByteArray();

        // Criando um objeto da classe StorageReference e definindo um "id" para esta foto no FireBase Storage
        StorageReference PhotoReference = FirebaseStorage.getInstance().getReference("foto/" + UUID.randomUUID() + ".jpg");

        // Enviando a foto em formato de array byte para o firebase storage
        UploadTask UploadPhotoCamera = PhotoReference.putBytes(foto);

        // Capturando o id do usuario atual para fazer a insercao da foto
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String IdUser = user.getUid();

        // Se a imagem foi enviada corretamente, chama o metodo que a grava no banco de dados
        UploadPhotoCamera.addOnSuccessListener(taskSnapshot -> {

        PhotoReference.getDownloadUrl().addOnSuccessListener(uri -> { saveImagesUrlToFIRESTORE(uri.toString(), IdUser); });


        });


    }


    public void saveImagesUrlToFIRESTORE(String ImagemURL, String IdUsuario){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> ImagemPerfil = new HashMap<>();

        ImagemPerfil.put("FotoPerfil", ImagemURL);

        db.collection("usuarios").document(IdUsuario).update(ImagemPerfil).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(TelaIndexActivity.this, "Imagem gravada com sucesso!", Toast.LENGTH_LONG).show();
                ProfileImage(UserID);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(TelaIndexActivity.this, "Erro ao subir a imagem!", Toast.LENGTH_LONG).show();
                Log.e(null,"ERRO AO SUBIR IMAGEM: " + e);

            }
        });


    }

    public void ProfileImage(String IdUsuario){

        dataBase.collection("usuarios").document(IdUsuario).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                 ProfileImage = ViewHeader.findViewById(R.id.imgPerfilUsuario);

                String UrlFoto = documentSnapshot.getString("FotoPerfil");

                if(UrlFoto != null && !UrlFoto.isEmpty()){

                    Glide.with(TelaIndexActivity.this).load(UrlFoto).placeholder(R.drawable.profile_icon).into(ProfileImage);
                    ProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                }
            }
        });

    }



    // Configurando o botao voltar do celular
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        // Verifica se a barra lateral esta aberta, o parametro passado serve para dizer que a barra esta no lado esquerdo da tela
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // Se a barra lateral (Drawer) estiver aberta, sera fechada
            drawer.closeDrawer(GravityCompat.START);

        } else {
            // Criando um TOAST de confirmacao para sair
            TelaLogin OBJ = new TelaLogin();

            OBJ.FunctionVoltar(this);

        }
    }

}

