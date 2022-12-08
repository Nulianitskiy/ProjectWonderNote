package com.example.wondernote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.wondernote.adapter.ListItem;
import com.example.wondernote.db.Constants;
import com.example.wondernote.db.DbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {
    private final int PICK_IMAGE_CODE = 123;
    private ImageView imageView;
    private ConstraintLayout imageContainer;
    private ImageButton imEditImage, imDeleteImage;
    private FloatingActionButton fabAddImage;
    private EditText edTitle, edDesc;
    private DbManager dbManager;
    private boolean isEditState = true;
    private ListItem item;
    private String tempUri = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        getIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDb();
    }

    private void init() {
        dbManager = new DbManager(this);
        imageContainer = findViewById(R.id.imageContainer);
        imageView = findViewById(R.id.imageView);
        fabAddImage = findViewById(R.id.floatingActionButton3);
        imEditImage = findViewById(R.id.imEditButton);
        imDeleteImage = findViewById(R.id.imDeleteButton);
        edTitle = findViewById(R.id.edTitle);
        edDesc = findViewById(R.id.edDesc);
    }

    private void getIntents() {
        Intent intent = getIntent();
        if (intent != null) {
            ListItem item = (ListItem) intent.getSerializableExtra(Constants.LIST_ITEM_INTENT);
            isEditState = intent.getBooleanExtra(Constants.EDIT_STATE, true);

            if (!isEditState) {
                edTitle.setText(item.getTitle());
                edDesc.setText(item.getDesc());
                if(!item.getUri().equals("empty")){
                    tempUri = item.getUri();
                    imageContainer.setVisibility(View.VISIBLE);
                    imageView.setImageURI(Uri.parse(item.getUri()));
                    imEditImage.setVisibility(View.GONE);
                    imDeleteImage.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){
            tempUri = data.getData().toString();
            imageView.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public void onClickSave(View view) {
        String title = edTitle.getText().toString();
        String desc = edDesc.getText().toString();

        if (title.equals("") || desc.equals("")) {
            Toast.makeText(this, "Заголовок или содержание пустые!", Toast.LENGTH_SHORT).show();
        } else {
            if (isEditState) {
                dbManager.insertToDb(title, desc, tempUri);
            } else {
                dbManager.updateItem(title, desc, tempUri, item.getId());
            }
            dbManager.closeDb();
            finish();
        }
    }

    public void onClickEdit(View view){
        imageContainer.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    public void onClickDelete(View view){
        imageView.setImageResource(R.drawable.ic_img);
        tempUri = "empty";
        imageContainer.setVisibility(View.GONE);
        fabAddImage.setVisibility(View.VISIBLE);
    }
    public void onClickChoose(View view){
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser,PICK_IMAGE_CODE);
    }
}
