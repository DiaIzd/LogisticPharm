package com.example.logisticpharm.employeeGUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logisticpharm.R;
import com.example.logisticpharm.model.MedicineInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EmployeeAddMEdicine extends AppCompatActivity {

    // creating variables for
    // EditText and buttons.
    private EditText medicineNameEdt, medicineCostEdt, medicineKindEdt,medicineInsideEdt,medicineBoxesEdt;
    private Button sendDatabtn;
    private Button btnBackMedicineAdd;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // creating a variable for
    // our object class
    MedicineInfo medicineInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add_medicine);

        // initializing our edittext and button
        medicineNameEdt = findViewById(R.id.idEdtMedicineName);
        medicineKindEdt = findViewById(R.id.idEdtMedicineKind);
        medicineCostEdt = findViewById(R.id.idEdtMedicineCost);
        medicineInsideEdt = findViewById(R.id.idEdtMedicineInside);
        medicineBoxesEdt = findViewById(R.id.idEdtMedicineBoxes);
        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("MedicineInfo");

        // initializing our object
        // class variable.
        medicineInfo = new MedicineInfo();

        sendDatabtn = findViewById(R.id.idBtnSendData);
        btnBackMedicineAdd = findViewById(R.id.idBtnBackAddMedicine);
        btnBackMedicineAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeAddMEdicine.this, RecylcerShow.class));
            }
        });
        // adding on click listener for our button.

        sendDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // getting text from our edittext fields.
                String name = medicineNameEdt.getText().toString();
                String kind = medicineKindEdt.getText().toString();
                String inside = medicineInsideEdt.getText().toString();
                String cost = medicineCostEdt.getText().toString();//String
                int box = Integer.parseInt(medicineBoxesEdt.getText().toString());
               // String cost =  //Double.parseDouble(costString);
                if (!TextUtils.isEmpty(name) && !Character.isUpperCase(name.charAt(0))) {
                    Toast.makeText(EmployeeAddMEdicine.this, "Nazwa leku musi zaczynać się dużą literą.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(kind) || TextUtils.isEmpty(cost) || TextUtils.isEmpty(inside) || TextUtils.isEmpty(medicineBoxesEdt.getText().toString())) {
                    Toast.makeText(EmployeeAddMEdicine.this, "Proszę uzupełnić wszystkie pola.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //  else call the method to add data to our database.

                    addDatatoFirebase(name, kind, inside, cost, box);
                }//if (TextUtils.isEmpty(name) || TextUtils.isEmpty(kind) || TextUtils.isEmpty(cost) || TextUtils.isEmpty(inside) || TextUtils.isEmpty(box)) {
                 //   Toast.makeText(EmployeeAddMEdicine.this, "Proszę uzupełnić wszystkie pola.", Toast.LENGTH_SHORT).show();

             //   } else {
                  //  else call the method to add
                  // data to our database.
             //       addDatatoFirebase(name, kind, inside,cost,box);
          //     }
              //  databaseReference.push().setValue(medicineInfo)
                   //     .addOnSuccessListener(new OnSuccessListener<Void>() {
                   //        @Override
                    //        public void onSuccess(Void aVoid) {
                    //            Toast.makeText(EmployeeAddMEdicine.this, "Lek dodany", Toast.LENGTH_SHORT).show();
                    //        }
                     //   })
                    //    .addOnFailureListener(new OnFailureListener() {
                     //       @Override
                     //       public void onFailure(@NonNull Exception e) {
                     //           Toast.makeText(EmployeeAddMEdicine.this, "Niepowodzenie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    //        }
                       // });
            }
        });
    }

    private void addDatatoFirebase(String name, String kind, String inside, String  cost,int box) {
        // below 3 lines of code is used to set
        // data in our object class.
       HashMap<String, Object> medicineHashmap = new HashMap<>();
      //  String medicineId = databaseReference.push().getKey();
       medicineInfo.setMedicineName(name);
        medicineInfo.setMedicineKind(kind);
       medicineInfo.setMedicineInside(inside);
        medicineInfo.setMedicineCost(cost);
medicineInfo.setMedicineBoxes(box);
medicineInfo.setPreviousMedicineBoxes(box);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(medicineInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EmployeeAddMEdicine.this, "lek dodany", Toast.LENGTH_SHORT).show();
                 medicineNameEdt.getText().clear();
                 medicineKindEdt.getText().clear();
                 medicineInsideEdt.getText().clear();
                 medicineCostEdt.getText().clear();
                medicineBoxesEdt.getText().clear();

            }
        } /*{
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(medicineInfo);

                // after adding this data we are showing toast message.
                Toast.makeText(EmployeeAddMEdicine.this, "lek dodany", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(EmployeeAddMEdicine.this, "Niepowodzenie " + error, Toast.LENGTH_SHORT).show();
            }
        }*/
        );
    }
}