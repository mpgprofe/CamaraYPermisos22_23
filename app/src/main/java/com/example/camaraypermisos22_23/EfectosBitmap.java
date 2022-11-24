package com.example.camaraypermisos22_23;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import android.transition.Transition;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.Random;

public class EfectosBitmap {
    public static Bitmap efectoSepia(Bitmap original) {
        //Devolvemos el bitmap en escala de grises.
        Bitmap sepia = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(sepia);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);

        // Convert to grayscale, then apply brown color
        colorMatrix.postConcat(colorScale);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(original, 0, 0, paint);
        return sepia;
    }

    public static Bitmap efectoMirror(Bitmap original) {
        //Devolvemos el bitmap con un flip horizontal.
        Bitmap mirror;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        mirror = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

        return mirror;
    }

    public static Bitmap efectoBlackAndWhiteLento(Bitmap bitmap) {
        Bitmap bwBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        float[] hsv = new float[3];
        for (int col = 0; col < bitmap.getWidth(); col++) {
            for (int row = 0; row < bitmap.getHeight(); row++) {
                Color.colorToHSV(bitmap.getPixel(col, row), hsv);
                if (hsv[2] > 0.5f) {
                    bwBitmap.setPixel(col, row, 0xffffffff);
                } else {
                    bwBitmap.setPixel(col, row, 0xff000000);
                }
            }
        }
        return bwBitmap;
    }

    public static Bitmap efectoBlackAndWhite(Bitmap original) {
        //Devolvemos el bitmap en escala de grises.
        Bitmap bmpMonochrome = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float m = 255f;
        float t = -255 * 128f;
        ColorMatrix threshold = new ColorMatrix(new float[]{
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });

        // Convert to grayscale, then scale and clamp
        colorMatrix.postConcat(threshold);
        ;
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(original, 0, 0, paint);
        return bmpMonochrome;
    }

    public static Bitmap efectoThermalBasico(Bitmap original) {
        Bitmap image2 = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        float[][] val = new float[original.getWidth()][original.getHeight()];
        int pixel;
        int red;
        int blue;
        int green;
        float[] hsbvals = new float[3];
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                pixel = original.getPixel(x, y);


                red = (pixel & 0x00ff0000) >> 16;
                blue = (pixel & 0x0000ff00) >> 8;
                green = (pixel & 0x000000ff);

                Color.colorToHSV(pixel, hsbvals);
                //RGBtoHSB(red, green, blue, hsbvals);
                if (hsbvals[2] > 0.7) {
                    image2.setPixel(x, y, Color.RED);
                } else if (hsbvals[2] >= 0.5 && hsbvals[2] < 0.7) {
                    image2.setPixel(x, y, Color.GREEN);
                } else if (hsbvals[2] >= 0.2 && hsbvals[2] < 0.5) {
                    image2.setPixel(x, y, Color.BLUE);
                }
            }
        }

        //Devolvemos el bitmap
        return image2;
    }

    public static Bitmap vignette(Bitmap image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        float radius = (float) (width / 1.2);
        int[] colors = new int[]{0, 0x55000000, 0xff000000};
        float[] positions = new float[]{0.0f, 0.5f, 1.0f};

        RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, colors, positions, Shader.TileMode.CLAMP);

        //RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

        Canvas canvas = new Canvas(image);
        canvas.drawARGB(1, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setShader(gradient);

        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        final RectF rectf = new RectF(rect);

        canvas.drawRect(rectf, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, rect, rect, paint);

        return image;
    }

    public static Bitmap grayscale(Bitmap src) {
        //Array to generate Gray-Scale image
        float[] GrayArray = {
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };

        ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult = Bitmap
                .createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        src.recycle();
        src = null;

        return bitmapResult;
    }

    public static Bitmap noise(Bitmap source) {
        final int COLOR_MAX = 0xFF;

        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        source.recycle();
        source = null;

        return bmOut;
    }

    public static Bitmap buscarCaras(Bitmap original, Bitmap gafas, Context ctx) {
        Bitmap caras = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(caras);
        canvas.drawBitmap(original, 0, 0, null);

        Paint pincel = new Paint();
        pincel.setColor(Color.GREEN);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(10);

        FaceDetector faceDetector = new FaceDetector.Builder(ctx).setProminentFaceOnly(false).
                setLandmarkType(FaceDetector.ALL_LANDMARKS).
                setClassificationType(FaceDetector.ALL_LANDMARKS).
                setMode(FaceDetector.ACCURATE_MODE).
                build();

        Frame frame = new Frame.Builder().setBitmap(original).build();

        SparseArray<Face> misCaras = faceDetector.detect(frame);


        for (int i = 0; i < misCaras.size(); i++) {
            Face caraAux = misCaras.valueAt(i);
            float rotación = caraAux.getEulerX();//*180/(float) Math.PI;

Log.d("CARA", "z:"+caraAux.getEulerZ()+" y:"+caraAux.getEulerY()+" x: "+caraAux.getEulerX()+" r: "+rotación);
            Rect rectanguloCara = new Rect((int) caraAux.getPosition().x,
                    (int) caraAux.getPosition().y,
                    (int) (caraAux.getPosition().x + caraAux.getWidth()),
                    (int) (caraAux.getPosition().y + caraAux.getHeight()));
            canvas.drawRect(rectanguloCara, pincel);
            boolean tieneojoderecho = false, tieneojoizquierdo = false;
            int xOjoD = 0, yOjoD = 0, xOjoI = 0, yOjoI = 0;
            for (Landmark landmark : caraAux.getLandmarks()) {
                //int cx = (int) landmark.getPosition().x;
                //int cy = (int) landmark.getPosition().y;

                if (landmark.getType() == Landmark.LEFT_EYE) {
                    xOjoI = (int) landmark.getPosition().x;
                    yOjoI = (int) landmark.getPosition().y;
                    tieneojoizquierdo = true;
                }
                if (landmark.getType() == Landmark.RIGHT_EYE) {
                    xOjoD = (int) landmark.getPosition().x;
                    yOjoD = (int) landmark.getPosition().y;
                    tieneojoderecho = true;

                }


                if (tieneojoderecho && tieneojoizquierdo) {
                    if (xOjoI > xOjoD) {
                        int aux = xOjoI;
                        xOjoI = xOjoD;
                        xOjoD = aux;

                        aux = yOjoI;
                        yOjoI = yOjoD;
                        yOjoD = aux;
                    }
                    int distanciaOjos = Math.abs(xOjoD - xOjoI);

                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotación);

                    Bitmap gafasR = Bitmap.createBitmap(gafas, 0, 0, gafas.getWidth(),
                            gafas.getHeight(), matrix, true);

                    int anchoGafas = distanciaOjos;
                    float proporciónGafas = gafasR.getWidth() / gafasR.getHeight();
                    int altoGafas = (int) (anchoGafas * proporciónGafas);

                    Rect rectanguloGafas = new Rect(xOjoI-(anchoGafas/2), yOjoI - (altoGafas / 2), xOjoD+(anchoGafas/2), yOjoD + (altoGafas / 2));


                    canvas.drawBitmap(gafasR, new Rect(0, 0, gafasR.getWidth(), gafasR.getHeight()), rectanguloGafas, null);
                }

                //canvas.drawCircle(cx,cy, 10, pincel);
            }


        }
        return caras;

    }

}
