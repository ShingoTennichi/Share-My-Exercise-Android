package Components;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CustomFirebase {
    Context context;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public CustomFirebase(Context context) {
        this.context = context;

        // init firebase connection
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void uploadImage(Uri imgUri, String imgName) {

        // the destination to store images on firebase storage
        StorageReference imgRef = storageReference.child("images/" + imgName);

        // upload
        imgRef.putFile(imgUri).addOnSuccessListener(
            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                }
            }
        ).addOnFailureListener(
            new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    public void getImage(ImageView imageView, String imgUrl) {
        // the destination to store images on firebase storage
        StorageReference imgRef = storageReference.child("images/" + imgUrl);

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            imgRef.getFile(localFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                    }
                }
            ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "read file failed try again", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        } catch (Exception e) {
            Log.e("getImage", e.getMessage());
        }
    }
}
