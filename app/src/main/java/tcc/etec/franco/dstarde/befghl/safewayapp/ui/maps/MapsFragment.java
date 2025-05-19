package tcc.etec.franco.dstarde.befghl.safewayapp.ui.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import tcc.etec.franco.dstarde.befghl.safewayapp.R;
import tcc.etec.franco.dstarde.befghl.safewayapp.databinding.FragmentMapsBinding;
import tcc.etec.franco.dstarde.befghl.safewayapp.ui.ViewModel.LocalizacaoViewModel;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    // Criando um objeto Binding do layout XML;
    private FragmentMapsBinding BindingReference;

    // Variaveis para configuracao do mapa
    private GoogleMap Map;
    private com.google.android.gms.maps.model.Marker Ponteiro;
    private LatLng Localizacao;

    //Objeto da ViewModel
    private LocalizacaoViewModel transporter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflando o layout, isto e, transformando os objetos XML em objetos Java, para que possamos os manipular
        BindingReference = FragmentMapsBinding.inflate(inflater, container, false);

        SupportMapFragment MapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);

        if (MapFragment != null) {

            MapFragment.getMapAsync(this);


        }

        // Criação do objeto que dá acesso ao DataLive no ViewModel
        transporter = new ViewModelProvider(requireActivity()).get(LocalizacaoViewModel.class);

        // retornando o layout XML, para o android o construir e mostrar na tela
        return BindingReference.getRoot();


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Map = googleMap;

        // Ativa botão de localização do usuário, se ele der permissão de localização
        if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            Map.setMyLocationEnabled(true);

        }


        transporter.getActualLocation().observe(getViewLifecycleOwner(), location -> {

            Localizacao = new LatLng(location.getLatitude(), location.getLongitude());

            if(Ponteiro == null) {

                UpdateMarker();

            } else {

                Ponteiro.setPosition(Localizacao);

            }
        });

    }

    public void UpdateMarker(){

        Ponteiro = Map.addMarker( new MarkerOptions().position(Localizacao).title("Você está aqui!"));

        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(Localizacao,16));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BindingReference = null;
    }
}



