package com.example.camaraypermisos22_23;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int VENGO_DE_LA_CAMARA = 1;
    private static final int PIDO_PERMISO_ESCRITURA = 1;
    private static final int VENGO_DE_LA_CAMARA_CON_CALIDAD = 2;
    private static final int VENGO_DE_LA_GALERIA = 3;
    ImageView imageView;
    Button buttonHacerFoto, buttonHacerFotoCalidad, buttonGaleria, buttonDibujaCirculo,buttonBYN;
    File fichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonHacerFoto = findViewById(R.id.buttonHacerFoto);
        buttonHacerFotoCalidad = findViewById(R.id.buttonHacerFotoCalidad);
        imageView = findViewById(R.id.imageView);
        buttonGaleria = findViewById(R.id.buttonGaleria);
        buttonDibujaCirculo=findViewById(R.id.buttonDibujaCirculo);
        buttonBYN = findViewById(R.id.buttonBlancoYNegro);

        buttonBYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                imageView.setImageBitmap( EfectosBitmap.efectoBlackAndWhiteLento(b));
            }
        });

        buttonDibujaCirculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                Bitmap copia = b.copy(b.getConfig(), true);
                Canvas canvas = new Canvas(copia);
                //Necesito un pincel
                Paint pincel = new Paint();
                pincel.setColor(Color.GREEN);
                pincel.setStrokeWidth(9);
                pincel.setStyle(Paint.Style.STROKE);
                //Ahora a puedo dibujar
                canvas.drawCircle(canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,pincel);
                imageView.setImageBitmap(copia);



            }
        });

        buttonHacerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) { //En móviles nuevos es necesario modificar androidManifest.xml con una query
                    startActivityForResult(intent, VENGO_DE_LA_CAMARA);
                } else {
                    Toast.makeText(MainActivity.this, "Necesitas instalar o tener cámara.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        buttonHacerFotoCalidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirPermisoParaFoto();
            }
        });
        buttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galeria, VENGO_DE_LA_GALERIA);
            }
        });
    }

    private void pedirPermisoParaFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//No tengo permiso
            //Pido permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PIDO_PERMISO_ESCRITURA);
            }
        } else {
            hacerFotoCalidad();
        }
    }

    private void hacerFotoCalidad() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                "com.example.camaraypermisos22_23.fileprovider", fichero));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_CALIDAD);
        } else {
            Toast.makeText(this, "Necitas cámara para poder hacer fotos!!", Toast.LENGTH_SHORT).show();
        }
    }

    private File crearFicheroFoto() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(new Date());
        String nombreFichero = "misFotos_" + fechaYHora;
        File carpetaFotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        carpetaFotos.mkdirs();
        File imagenAGranResolucion = File.createTempFile(nombreFichero, ".jpg", carpetaFotos);
        return imagenAGranResolucion;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VENGO_DE_LA_CAMARA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == VENGO_DE_LA_CAMARA_CON_CALIDAD) {
            if (resultCode == RESULT_OK) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));
                actualizarGaleria(fichero.getAbsolutePath());
            } else {
                fichero.delete();
            }
        }else if (requestCode== VENGO_DE_LA_GALERIA){
            Uri imagenUri = data.getData();
            imageView.setImageURI(imagenUri);
        }
    }

    void actualizarGaleria(String path){
        MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.d("ACTUALIZAR", "Se ha actualizado la galería");
            }
        });
    }



}