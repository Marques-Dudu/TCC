package tcc.etec.franco.dstarde.befghl.safewayapp.ui.maps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import tcc.etec.franco.dstarde.befghl.safewayapp.R;
import tcc.etec.franco.dstarde.befghl.safewayapp.TelaIndexActivity;
import tcc.etec.franco.dstarde.befghl.safewayapp.databinding.FragmentMapsBinding;
import tcc.etec.franco.dstarde.befghl.safewayapp.ui.ViewModel.LocalizacaoViewModel;


public class MapsFragment extends Fragment {

  // Criando um objeto Binding do layout XML;
    private FragmentMapsBinding BindingReference
          ;
    private MapView Map;

    private GeoPoint startPoint;

    private Marker Ponteiro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      // Inflando o layout, isto e, transformando os objetos XML em objetos Java, para que possamos os manipular
      BindingReference = FragmentMapsBinding.inflate(inflater, container, false);

      //Configurando cache do carregamento das imagens
        Configuration.getInstance().setOsmdroidBasePath(getContext().getCacheDir());
        Configuration.getInstance().setOsmdroidTileCache(getContext().getCacheDir());
      
    // Carregando as imagens do mapa (ruas, casas...)
      Configuration.getInstance().load(getContext(), getContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        Map = BindingReference.mapa;
        Map.setTileSource(TileSourceFactory.MAPNIK);
        Map.setMultiTouchControls(true);
        Map.setBuiltInZoomControls(true);

        // Centralizar o mapa
        IMapController MapController = Map.getController();
        MapController.setZoom(18.0);

        startPoint = new GeoPoint(-23.5505, -46.6333);
        MapController.setCenter(startPoint);

        // Chegada da localizacao e insercao no mapa
        LocalizacaoViewModel transporter = new ViewModelProvider(requireActivity()).get(LocalizacaoViewModel.class);

        transporter.getActualLocation().observe(getViewLifecycleOwner(),
         location -> {

          startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
          MapController.setCenter(startPoint);
          PersonalizarPonteiro(Map, startPoint);


        } );



      // retornando o layout XML, para o android o construir e mostrar na tela
      return BindingReference.getRoot();


    }

    // Funcao para personalizar o ponteiro de localizacao do usuario
    private void PersonalizarPonteiro(MapView Map, GeoPoint StartPoint){

        if (Ponteiro == null) {
             Ponteiro = new Marker(Map);
            Ponteiro.setPosition(startPoint);
            Ponteiro.setTitle("Você está aqui!");
            Ponteiro.setIcon(ActivityCompat.getDrawable(requireContext(), R.drawable.peopple_icon));
            Ponteiro.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Map.getOverlays().add(Ponteiro);
        } else {

            Ponteiro.setPosition(startPoint);
            Map.invalidate();
        }
    }

}