package com.example.carmarketplaceapplication;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<CarListModel> carModel = new MutableLiveData<>();

    public void setCarListModel(CarListModel model) {
        carModel.setValue(model);
    }

    public LiveData<CarListModel> getCarListModel() {
        return carModel;
    }

    private MutableLiveData<List<Uri>> imageUris = new MutableLiveData<>();

    public LiveData<List<Uri>> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> uris) {
        imageUris.setValue(uris);
    }

    public void clearImageUris() {
        List<Uri> emptyUriList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emptyUriList.add(Uri.EMPTY);
        }
        imageUris.postValue(emptyUriList); // Use postValue to update the LiveData
    }
}

