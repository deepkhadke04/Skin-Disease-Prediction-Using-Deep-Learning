package com.example.skincare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skincare.ml.Resnet;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    Button Gallerybtn;
    Button CameraBtn;
    Button PredictBtn;
    ImageView imageView;
    TextView result1;
    TextView result2;
    TextView result3;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        Gallerybtn = findViewById(R.id.gbtn);
        CameraBtn = findViewById(R.id.cbtn);
        PredictBtn = findViewById(R.id.pbtn);
        imageView = findViewById(R.id.imageView1);
        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);
        result3 = findViewById(R.id.result3);

        Gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        CameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,12);
            }
        });

        PredictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true);

                try {
                    Resnet model = Resnet.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Resnet.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.


                    float[] con = outputFeature0.getFloatArray();
                    int maxpos = 0;
                    float maxcon = 0;
                    for (int i = 0; i < con.length; i++) {
                        if (con[i] > maxcon) {
                            maxcon = con[i];
                            maxpos = i;
                        }
                    }

                    String[] classes = {"Actinic Keratosis", "Basal Cell Carcinoma", "Benign Keratosis-Like Lesions", "Dermatofibroma", "Melanocytic Nevi", "Pyogenic Granuloma", "Melanoma"};

                    //String[] classes1 = {"ak","bcc","bkl","df","mn","pg","mel"};



                    int z = (int) (maxcon * 100);


                    if (z<=20 || z>=90){
                        result1.setText("No Disease Found!");
                        result2.setText(" ");
                        result3.setText(" ");

                    }else {
                        result1.setText("Disease: "+classes[maxpos]+"    Confidence: "+z+"%");
                        //result2.setText(classes1[maxpos]);

                        switch (maxpos){
                            case 0:
                                result2.setText("Actinic keratosis is a rough, scaly patch of skin that can develop on sun-damaged skin. It is caused by long-term exposure to ultraviolet (UV) radiation from the sun or from indoor tanning beds. AKs are not always dangerous, but they can sometimes develop into skin cancer.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/14148-actinic-keratosis");
                                break;
                            case 1:
                                result2.setText("Basal cell carcinoma also known as benign tumor is the most common type of skin cancer. It can appear as a small, pink or red bump on the skin that may bleed or crust over. It is caused by exposure to ultraviolet (UV) radiation from the sun or tanning beds. Basal cell carcinoma is usually curable if it is caught early and treated.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/22121-benign-tumor");
                                break;
                            case 2:
                                result2.setText("Benign keratosis-like lesions also known as seborrheic keratosis are noncancerous skin growths that look like precancerous lesions. They are caused by sun damage and are more common in people with fair skin. They can be removed by a doctor if they are bothersome or if there is concern that they may become cancerous.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/21721-seborrheic-keratosis");
                                break;
                            case 3:
                                result2.setText("A dermatofibroma is a small, benign growth on the skin that is usually painless. It is most common on the legs and is thought to be caused by a reaction to an injury or insect bite. Dermatofibromas are not harmful and usually go away on their own within a few months.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/22668-cellular-dermatofibroma");
                                break;
                            case 4:
                                result2.setText("Melanocytic nevi, commonly known as moles or nevus, are benign skin growths that are caused by a cluster of pigment-producing cells. They can be present at birth or develop later in life. Melanocytic nevi are usually harmless, but they can sometimes be a sign of skin cancer.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/4410-moles");
                                break;
                            case 5:
                                result2.setText("A pyogenic granuloma is a small, red, fleshy growth on the skin that is not cancerous. It is caused by an overgrowth of blood vessels and can be caused by injury, pregnancy, or hormonal changes. Pyogenic granulomas usually go away on their own, but they can be removed by a doctor if they are bothersome.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/22717-pyogenic-granuloma");
                                break;
                            case 6:
                                result2.setText("Melanoma is a type of skin cancer that develops from the cells that produce melanin, the pigment that gives skin its color. Melanoma is the most serious type of skin cancer because it can spread to other parts of the body.");
                                result3.setText("https://my.clevelandclinic.org/health/diseases/14391-melanoma");
                                break;

                        }
                    }
                    model.close();

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });

    }

    void getPermission(){
        if(checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},11);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==11){
            if(grantResults.length>0){
                if (grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==10){
            if(data!=null){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else if (requestCode==12) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}