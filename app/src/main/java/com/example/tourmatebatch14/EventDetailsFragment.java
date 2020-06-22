package com.example.tourmatebatch14;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tourmatebatch14.POJOS.Expense;
import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.example.tourmatebatch14.Utils.EventUtils;
import com.example.tourmatebatch14.ViewModels.EventViewModel;
import com.example.tourmatebatch14.ViewModels.ExpenseViewModel;
import com.example.tourmatebatch14.databinding.FragmentEventDetailsBinding;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

   private FragmentEventDetailsBinding binding;
   private String eventId;
   private EventViewModel eventViewModel;
   private ExpenseViewModel expenseViewModel;
   private int totalBudget = 0;
    int totalExpense = 0;
    private final int REQUEST_STORAGE_CODE = 456;
    private final int REQUEST_CAMERA_CODE = 999;
    private String currentPhotoPath;

    public EventDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailsBinding.inflate(LayoutInflater.from(getActivity()));
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        expenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        Bundle bundle = getArguments();
        if(bundle!=null){
            eventId=bundle.getString("id");
            eventViewModel.getEventDetails(eventId);
            expenseViewModel.getExpenses(eventId);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.imageUploadPB.setProgress(0);
        eventViewModel.eventDetailsLD.observe(this, new Observer<TourmateEvent>() {
            @Override
            public void onChanged(TourmateEvent tourmateEvent) {
                totalBudget = tourmateEvent.getInitialBudget();
                binding.detailsEventName.setText(tourmateEvent.getEventName());
                binding.detailsBudgetStatus.setText(String.valueOf("Your Budget: "+tourmateEvent.getInitialBudget()+" tk"));


            }
        });

        expenseViewModel.expenseListLD.observe(getActivity(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {

                for(Expense e: expenses){
                    totalExpense += e.getExpenseAmount();
                }
                binding.detailsTotalExpense.setText(MessageFormat.format("Expense: {0}", String.valueOf(totalExpense)+" tk"));
                int remainder = totalBudget - totalExpense;
                binding.detailsBudgetRemainder.setText(MessageFormat.format("Remaining: {0}", String.valueOf(remainder)+" tk"));
                binding.budgetPB.setProgress((totalExpense*100)/totalBudget);

            }
        });

        eventViewModel.progressLD.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                //binding.imageUploadPB.setVisibility(View.VISIBLE);
                binding.imageUploadPB.setProgress(integer);
            }
        });
        binding.addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

        binding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()){
                    dispatchCameraIntent();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       if (requestCode == REQUEST_STORAGE_CODE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(), "Permission accepted", Toast.LENGTH_SHORT).show();
            dispatchCameraIntent();
        }
    }

    private boolean checkStoragePermission(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(permissions, REQUEST_STORAGE_CODE);
            return false;
        }
        return true;
    }

    private void dispatchCameraIntent(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.tourmatebatch14",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK){

            Log.e("image", "onActivityResult: "+data );
            File file = new File(currentPhotoPath);
            eventViewModel.saveImagetoFirebaseStorage(file,eventId);
            //Log.e("check", "onActivityResult2: "+currentPhotoPath );

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showAddExpenseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Expense");
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_expense_dialog,null);
        builder.setView(v);
        final EditText expenseAmount = v.findViewById(R.id.addExpenseAmountET);
        final EditText expenseComment = v.findViewById(R.id.addExpenseCommentET);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = expenseAmount.getText().toString();
                String comment = expenseComment.getText().toString();
                if(amount.isEmpty()&&comment.isEmpty()){
                    Toast.makeText(getContext(), "Required Fields Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Expense expense = new Expense(null,eventId,comment,Integer.parseInt(amount), EventUtils.getCurrentDateTime());
                expenseViewModel.saveExpense(expense);

            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}