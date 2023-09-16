package com.example.logisticpharm.model;

import com.example.logisticpharm.R;

import java.io.Serializable;

public class MedicineInfo implements Serializable {

    // string variable for
    // storing employee name.
    private  String medicineName;
    private String medicineKey;
String key;
    // string variable for storing
    // employee contact number
    private  String medicineKind;

    // string variable for storing
    // employee address.
    private  String medicineCost;

    private int previousMedicineBoxes;
    private  String medicineInside;
    private int medicineBoxes;
    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public MedicineInfo() {

    }

    // created getter and setter methods
    // for all our variables.
    public  String getMedicineName() {
        return medicineName;
    }
public String getKey(){return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public  String getMedicineKind() {
        return medicineKind;
    }

    public void setMedicineKind(String medicineKind) {
        this.medicineKind = medicineKind;
    }

    public  String getMedicineInside() {
        return medicineInside;
    }

    public void setMedicineInside(String medicineInside) {
        this.medicineInside = medicineInside;
    }
    //public double getMedicineCost() {
   //     return medicineCost;
    //}

   // public void setMedicineCost(double medicineCost) {
   //     this.medicineCost = medicineCost;
   // }
    public String getMedicineCost() {
         return medicineCost;
    }
    public void setMedicineCost(String medicineCost) {
             this.medicineCost = medicineCost;
         }

    public int getMedicineBoxes() {
        return medicineBoxes;
    }
    public void setMedicineBoxes(int medicineBoxes){
        this.medicineBoxes =medicineBoxes;
    }

    public String getAvailabilityStatus() {
        int quantity = medicineBoxes;
        if (quantity==0){
            return "Niedostępny";
        } else if(quantity < 3) {
            return "Ciężko dostępny";
        } else if (quantity >= 3 && quantity <= 6) {
            return "Średnio dostępny";
        } else {
            return "Dostępny";
        }
    }
    public int getAvailabilityStatusColor() {
        int quantity = medicineBoxes;
        if (quantity == 0) {
            return R.color.red;
        } else if (quantity < 3) {
            return R.color.orange;
        }
        else if (quantity >= 3 && quantity <= 6) {
            return R.color.blue;

        }else {
            return R.color.green;
        }
    }
    public String getMedicineKey() {
        return medicineKey;
    }

    public void setMedicineKey(String medicineKey) {
        this.medicineKey = medicineKey;
    }
    public int getPreviousMedicineBoxes() {
        return previousMedicineBoxes;
    }

    public void setPreviousMedicineBoxes(int previousMedicineBoxes) {
        this.previousMedicineBoxes = previousMedicineBoxes;
    }
}