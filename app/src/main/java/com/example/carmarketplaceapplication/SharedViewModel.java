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

    public void clearCarModel() {
        carModel.setValue(null);
    }


    public LiveData<CarListModel> getCarListModel() {
        return carModel;
    }

    private MutableLiveData<List<String>> imageUrls = new MutableLiveData<>();

    public LiveData<List<String>> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> urls) {
        imageUrls.setValue(urls);
    }

    public void clearImageUrls() {
        List<String> emptyUrlList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emptyUrlList.add("");
        }
        imageUrls.postValue(emptyUrlList); // Use postValue to update the LiveData
    }
}

