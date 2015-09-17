package cat.lafosca.facecropper.sample;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import cat.lafosca.facecropper.FaceCropper;

public class MainActivity extends ActionBarActivity {

    private String[] sampleImages =

            {
                    "http://assets3.introme.com/volo_photo/static/images/uploads/profile/768x767/762bc813e8ad1723e297ceefa6b48d93ba3ba63c2a2b3738a3637a21.jpeg",
                    "http://assets4.introme.com/volo_photo/static/images/uploads/profile/768x768/e2263220161f752ee27c8359e99a6de0872c51cb882acb6a95983a26.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x768/210f520f46020bdf6e4628c1a45ab154834ec393b7da1e2ded049cc1.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x767/ffb05fb51971fe32ea0032da65411e015c744027893a793aabd02fac.jpeg",
                    "http://assets5.introme.com/volo_photo/static/images/uploads/profile/768x767/faff33537819f031eef485e3b148da6ac597fa0782ff7eaeafc0a198.jpeg",
                    "http://assets5.introme.com/volo_photo/static/images/uploads/profile/768x767/1a02d4bc324cc6f52bbc70938bb00a0e13c826064fb5a1f8829adb0f.jpeg",
                    "http://assets4.introme.com/volo_photo/static/images/uploads/profile/768x767/99bfe7d603dce2faf12e0dab69dd4b8a462187ffbf9948d60c5e14f6.jpeg",
                    "http://assets5.introme.com/volo_photo/static/images/uploads/profile/768x767/b8089144c345931c0ce361f11a7998ba18fa8ef331513f4c54c060c7.jpeg",
                    "http://assets1.introme.com/volo_photo/static/images/uploads/profile/768x1022/c5e004c3290525633e3a7bd1f16d56ca70c7fd944f4b2464e2919386.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x1152/fb_861e427fe268561e92f96968e2ada326c53e86a1fab3190c744c51fe.jpeg",
                    "http://assets3.introme.com/volo_photo/static/images/uploads/profile/768x768/df332a05f1123ea62fd3565b82b763e7b61e78dba27bf5a21c925de3.jpeg",
                    "http://assets5.introme.com/volo_photo/static/images/uploads/profile/768x768/617ac272141a2c35e6abda4f8bc79a17f815b45eac65e83da638160e.jpeg",
                    "http://assets4.introme.com/volo_photo/static/images/uploads/profile/768x768/f60a9dddae7678e9ae1ea69850bbc5db7e5925c468a8ec7e687d0718.jpeg",
                    "http://assets4.introme.com/volo_photo/static/images/uploads/profile/768x768/4896c3b738580f17acec830047c7d2943a92517698703fbcf5704409.jpeg",
                    "http://assets5.introme.com/volo_photo/static/images/uploads/profile/768x807/cc46f4cd6315a2b0694131591c170584d1cc4172297efc3fa0fceec6.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x768/c8b36cf04c43b51647079c383e22deb3e6144888b8ff1fc9697e7128.jpeg",
                    "http://assets1.introme.com/volo_photo/static/images/uploads/profile/768x768/5af81cd007ed4e87a29f25c403c227ae6b6b99ab7e057be751c26fc1.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x768/750e0a8d5c2e505199da120363329af4f6ebfed61cae52a70bf342bc.jpeg",
                    "http://assets3.introme.com/volo_photo/static/images/uploads/profile/768x768/77ccd1852ad3b2f7d3c3900ca623f1dbf6f98438e05234e583865a45.jpeg",
                    "http://assets2.introme.com/volo_photo/static/images/uploads/profile/768x768/4cda3b7866951d5fed82a649b8f66aaa2c530f4ee61296710d743da5.jpeg"
            };

    private Picasso mPicasso;
    private FaceCropper mFaceCropper;
    private ViewPager mViewPager;

    private Transformation mCropTransformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            return mFaceCropper.getCroppedImage(source);
        }

        @Override
        public String key() {
            StringBuilder builder = new StringBuilder();

            builder.append("faceCrop(");
            builder.append("minSize=").append(mFaceCropper.getFaceMinSize());
            builder.append(",maxFaces=").append(mFaceCropper.getMaxFaces());

            FaceCropper.SizeMode mode = mFaceCropper.getSizeMode();
            if (FaceCropper.SizeMode.EyeDistanceFactorMargin.equals(mode)) {
                builder.append(",distFactor=").append(mFaceCropper.getEyeDistanceFactorMargin());
            } else if (FaceCropper.SizeMode.FaceMarginPx.equals(mode)) {
                builder.append(",margin=").append(mFaceCropper.getFaceMarginPx());
            }

            return builder.append(")").toString();
        }
    };

    private Transformation mDebugCropTransformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            return mFaceCropper.getFullDebugImage(source);
        }

        @Override
        public String key() {
            StringBuilder builder = new StringBuilder();

            builder.append("faceDebugCrop(");
            builder.append("minSize=").append(mFaceCropper.getFaceMinSize());
            builder.append(",maxFaces=").append(mFaceCropper.getMaxFaces());

            FaceCropper.SizeMode mode = mFaceCropper.getSizeMode();
            if (FaceCropper.SizeMode.EyeDistanceFactorMargin.equals(mode)) {
                builder.append(",distFactor=").append(mFaceCropper.getEyeDistanceFactorMargin());
            } else if (FaceCropper.SizeMode.FaceMarginPx.equals(mode)) {
                builder.append(",margin=").append(mFaceCropper.getFaceMarginPx());
            }

            return builder.append(")").toString();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFaceCropper = new FaceCropper(1f);
        mFaceCropper.setFaceMinSize(0);
        mFaceCropper.setDebug(true);
        mPicasso = Picasso.with(this);

        final ImageAdapter adapter = new ImageAdapter();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                adapter.updateView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mFaceCropper.setEyeDistanceFactorMargin((float) i / 10);
                adapter.updateView(mViewPager.getCurrentItem());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar.setProgress(100);
    }

    class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return (sampleImages == null) ? 0 : sampleImages.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getLayoutInflater().inflate(R.layout.pager_item, null, false);

            setupView(v, position);

            v.setTag(position);
            container.addView(v);
            return v;
        }

        public void setupView(View v, int position) {
            if (v == null) return;
            ImageView image = (ImageView) v.findViewById(R.id.imageView);
            ImageView imageCropped = (ImageView) v.findViewById(R.id.imageViewCropped);

            Log.d("","ImageView height:"+imageCropped.getHeight()+" Width:"+imageCropped.getWidth());

            mPicasso.load(sampleImages[position]).transform(mDebugCropTransformation).into(image);

            mPicasso.load(sampleImages[position])
                    .config(Bitmap.Config.RGB_565)
                    .transform(mCropTransformation)
                    .into(imageCropped);
        }

        public void updateView(int position) {
            setupView(mViewPager.findViewWithTag(position), position);
        }
    }
}
