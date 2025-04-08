package com.example.jjsminventoria.ui.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryHomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public InventoryHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Inventory fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}