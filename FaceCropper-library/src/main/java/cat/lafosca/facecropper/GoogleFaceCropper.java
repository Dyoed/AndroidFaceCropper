/*
 * Copyright (C) 2014 lafosca Studio, SL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cat.lafosca.facecropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * An utility that crops faces from bitmaps.
 * It support multiple faces (max 8 by default) and crop them all, fitted in the same image.
 */
public class GoogleFaceCropper {

    private static final String LOG_TAG = GoogleFaceCropper.class.getSimpleName();

    public enum SizeMode {FaceMarginPx, EyeDistanceFactorMargin}

    ;

    private static final int MAX_FACES = 8;
    private static final int MIN_FACE_SIZE = 200;

    private int mFaceMinSize = MIN_FACE_SIZE;
    private int mFaceMarginPx = 100;
    private float mEyeDistanceFactorMargin = 2f;
    private int mMaxFaces = MAX_FACES;
    private SizeMode mSizeMode = SizeMode.EyeDistanceFactorMargin;
    private boolean mDebug;
    private Paint mDebugPainter;
    private Paint mDebugAreaPainter;
    FaceDetector mFaceDetector;


    public GoogleFaceCropper(FaceDetector faceDetector){
        initPaints();
        mFaceDetector = faceDetector;
    }

    private void initPaints() {
        mDebugPainter = new Paint();
        mDebugPainter.setColor(Color.RED);
        mDebugPainter.setAlpha(80);

        mDebugAreaPainter = new Paint();
        mDebugAreaPainter.setColor(Color.GREEN);
        mDebugAreaPainter.setAlpha(80);
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    protected CropResult cropFace(Bitmap original, boolean debug) {
        Bitmap fixedBitmap = BitmapUtils.forceEvenBitmapSize(original);
        Bitmap mutableBitmap = fixedBitmap.copy(fixedBitmap.getConfig(), true);

        if (fixedBitmap != mutableBitmap) {
            fixedBitmap.recycle();
        }

        Frame frame = new Frame.Builder().setBitmap(mutableBitmap).build();
        SparseArray<Face> faces = mFaceDetector.detect(frame);

        // The bitmap must be in 565 format (for now).
        int faceCount = faces.size();

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, faceCount + " faces found");
        }

        if (faceCount == 0) {
            return new CropResult(mutableBitmap);
        }

        int endX = mutableBitmap.getWidth();
        int endY = mutableBitmap.getHeight();
        int initX = 0;
        int initY = 0;

        PointF centerFace = null;

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, new Matrix(), null);

        // Calculates minimum box to fit all detected faces
        for (int i = 0; i < faceCount; i++) {
            Face face = faces.get(i);

            centerFace = face.getPosition();

            float x1 = face.getPosition().x;
            float y1 = face.getPosition().y;
            float x2 = x1 + face.getWidth();
            float y2 = y1 + face.getHeight();

            Log.d("","Face dimension"+face.getWidth()+"x"+face.getHeight()+" Img Height:"+mutableBitmap.getHeight()+" Img Width:"+mutableBitmap.getWidth()+"\n Face height:"+face.getHeight()+" Face width:"+face.getWidth());

            if (debug) {
                canvas.drawPoint(centerFace.x, centerFace.y, mDebugPainter);
                canvas.drawRoundRect(new RectF(x1, y1, x2, y2), 0, 0, mDebugPainter);
            }

            initX = (int) (centerFace.x - face.getWidth());
            if(face.getWidth() < 100){
                initX -= 100;
                endX = (int) ((int) (face.getWidth() * 4) + centerFace.x);
            }

            initY = (int) (centerFace.y - face.getHeight());
            endY = (int) ((face.getHeight()));
            if(y1 > 10){
                endY += y2 + (face.getHeight()/2.5);
            }
            else{
                endY += (face.getHeight()/6);
            }

        }

        initX = Math.max(0, initX);
        initY = Math.max(0, initY);
        endX = Math.min(mutableBitmap.getWidth(), endX);
        endY = Math.min(mutableBitmap.getHeight(), endY);

        Point init = new Point(initX, initY);
        Point end = new Point(endX, endY);

        CropResult cropResult = new CropResult(mutableBitmap, init, end);
        return cropResult;
    }

    @Deprecated
    public Bitmap cropFace(Context ctx, int resDrawable) {
        return getCroppedImage(ctx, resDrawable);
    }

    @Deprecated
    public Bitmap cropFace(Bitmap bitmap) {
        return getCroppedImage(bitmap);
    }

    public Bitmap getFullDebugImage(Context ctx, int resDrawable) {
        // Set internal configuration to RGB_565
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        return getFullDebugImage(BitmapFactory.decodeResource(ctx.getResources(), resDrawable, bitmapOptions));
    }

    public Bitmap getFullDebugImage(Bitmap bitmap) {
        CropResult result = cropFace(bitmap, true);
        Canvas canvas = new Canvas(result.getBitmap());

        canvas.drawBitmap(result.getBitmap(), new Matrix(), null);
        canvas.drawRect(result.getInit().x,
                result.getInit().y,
                result.getEnd().x,
                result.getEnd().y,
                mDebugAreaPainter);

        return result.getBitmap();
    }

    public Bitmap getCroppedImage(Context ctx, int resDrawable) {
        // Set internal configuration to RGB_565
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        return getCroppedImage(BitmapFactory.decodeResource(ctx.getResources(), resDrawable, bitmapOptions));
    }

    public Bitmap getCroppedImage(Bitmap bitmap) {
        CropResult result = cropFace(bitmap, mDebug);
        Bitmap croppedBitmap = Bitmap.createBitmap(result.getBitmap(),
                result.getInit().x,
                result.getInit().y,
                (result.getEnd().x - result.getInit().x),
                result.getEnd().y - result.getInit().y);

        if (result.getBitmap() != croppedBitmap) {
            result.getBitmap().recycle();
        }


//        if(result.getAvgFaceDiameter() > 280){
//            croppedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 450, 380, false);
//        }

        return croppedBitmap;
    }


    protected class CropResult {
        Bitmap mBitmap;
        Point mInit;
        Point mEnd;
        int mAvgFaceDiameter;

        public int getAvgFaceDiameter() {
            return mAvgFaceDiameter;
        }

        public void setAvgFaceDiameter(int avgFaceDiameter) {
            mAvgFaceDiameter = avgFaceDiameter;
        }

        public CropResult(Bitmap bitmap, Point init, Point end) {
            mBitmap = bitmap;
            mInit = init;
            mEnd = end;
        }

        public CropResult(Bitmap bitmap) {
            mBitmap = bitmap;
            mInit = new Point(0, 0);
            mEnd = new Point(bitmap.getWidth(), bitmap.getHeight());
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public Point getInit() {
            return mInit;
        }

        public Point getEnd() {
            return mEnd;
        }
    }
}