package ca.philipyoung.philssampler.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

import ca.philipyoung.philssampler.R;

public class DataSamples {
    private static final String TAG = "DataSamples";

    private Context mContext;

    public DataSamples(Context context) {
        this.mContext = context;
    }

    // Build a record object. Needs an ID, a photo, 3 short descriptions, and a detailed description
    public class DataRecord {
        private Integer idRecord;
        private String strDescription1, strDescription2, strDescription3, strDescription4;
        private byte[] bytPhoto;

        public DataRecord(Integer id) {
            this.idRecord = id;
            this.strDescription1 = mContext.getString(R.string.sample_short_text);
            this.strDescription2 = mContext.getString(R.string.sample_short_text);
            this.strDescription3 = mContext.getString(R.string.sample_long_text);
            this.strDescription4 = mContext.getString(R.string.sample_short_text);

            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tabletennis);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            this.bytPhoto = outputStream.toByteArray();
        }

        public Integer getIdRecord() {
            return idRecord;
        }

        public void setIdRecord(Integer idRecord) {
            this.idRecord = idRecord;
        }

        public String getStrDescription1() {
            return strDescription1;
        }

        public void setStrDescription1(String strDescription1) {
            this.strDescription1 = strDescription1;
        }

        public String getStrDescription2() {
            return strDescription2;
        }

        public void setStrDescription2(String strDescription2) {
            this.strDescription2 = strDescription2;
        }

        public String getStrDescription3() {
            return strDescription3;
        }

        public void setStrDescription3(String strDescription3) {
            this.strDescription3 = strDescription3;
        }

        public String getStrDescription4() {
            return strDescription4;
        }

        public void setStrDescription4(String strDescription4) {
            this.strDescription4 = strDescription4;
        }

        public byte[] getBytPhoto() {
            return bytPhoto;
        }

        public Bitmap getPhoto() {
            return BitmapFactory.decodeByteArray(this.bytPhoto, 0, this.bytPhoto.length);
        }

        public void setBytPhoto(byte[] bytPhoto) {
            this.bytPhoto = bytPhoto;
        }

        public void setBytPhoto(Integer intPhotoId) {
            setBytPhoto(BitmapFactory.decodeResource(mContext.getResources(), intPhotoId));
        }

        public void setBytPhoto(Bitmap bitmap) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            setBytPhoto(outputStream.toByteArray());
        }
    }
}
