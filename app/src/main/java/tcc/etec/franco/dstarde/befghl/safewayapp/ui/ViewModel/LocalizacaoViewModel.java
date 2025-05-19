package tcc.etec.franco.dstarde.befghl.safewayapp.ui.ViewModel;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocalizacaoViewModel extends ViewModel {

    private MutableLiveData<Location> ActualLocation = new MutableLiveData<>();

    // Setter para receber a localizacao da TelaIndex
    public void setActualLocation(Location localizacao){

        ActualLocation.setValue(localizacao);

    }

    // Getter para os fragments terem acesso a localizacao
    public MutableLiveData<Location> getActualLocation(){

        return ActualLocation;

    }

}
