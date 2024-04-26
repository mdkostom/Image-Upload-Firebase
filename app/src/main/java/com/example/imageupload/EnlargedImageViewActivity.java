package com.example.imageupload;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import android.content.DialogInterface;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;

public class EnlargedImageViewActivity extends AppCompatActivity {

    private String imageUrl;
    private String imageKey;
    private StorageReference storageReference;
    private static final String TAG = "EnlargedImageViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarged_image_view);

        // Get image URL and key from intent extras
        imageUrl = getIntent().getStringExtra("imageUrl");
        imageKey = getIntent().getStringExtra("imageKey");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Load the image into the image view using Glide
        ImageView imageView = findViewById(R.id.enlargedImageView);
        Glide.with(this).load(imageUrl).into(imageView);

        // Add OnClickListener to the delete button
        FloatingActionButton deleteButton = findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // Add OnClickListener to the image view to navigate back to the grid view
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the grid view (MainActivity)
                Intent intent = new Intent(EnlargedImageViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish this activity to go back
            }
        });
    }

    // Method to show delete confirmation dialog
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm deletion?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed deletion, proceed with deletion
                        deleteImageFromFirebase();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    // Method to delete image from Firebase Storage and corresponding entry from Realtime Database
    private void deleteImageFromFirebase() {
        // Get a reference to the image in Firebase Storage
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // Delete the image
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully, now remove entry from Firebase Database
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Images");

                // Query to find the entry with matching image URL
                Query query = databaseRef.orderByChild("imageURL").equalTo(imageUrl);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Iterate through the matched entries and remove them
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Entry removed successfully
                                    Toast.makeText(EnlargedImageViewActivity.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Finish the activity or navigate back to the previous screen
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to remove entry
                                    Toast.makeText(EnlargedImageViewActivity.this, "Failed to delete image entry", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle cancellation of the query
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to delete image
                Toast.makeText(EnlargedImageViewActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        });
    }

}