package mg.memegenerator;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int RequestCode = 10;
    public static final int REQUESTCODE = 10;
    public int textboxid = 1;
    public int dragged = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button share = findViewById(R.id.share_button);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareimage();
            }
        });
        Button addbutton = findViewById(R.id.add_text);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                make_editext();
                final EditText newtext = findViewById(textboxid - 1);
                /*newtext.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                        ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(newtext);

                        v.startDrag(dragData,myShadow,v,0);
                        return true;
                    }
                });*/
                newtext.setOnTouchListener(new MyTouchListener());
                FrameLayout flout = findViewById(R.id.frame);
                flout.setOnDragListener(new ChoiceDragListener());
            }
        });

       // EditText newtext = findViewById(textboxid - 1);

      /*  newtext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(newtext);

                v.startDrag(dragData,myShadow,v,0);
                return true;
            }
        });*/
        //newtext.setOnTouchListener(new MyTouchListener());
       // FrameLayout flout = findViewById(R.id.frame);
        //flout.setOnDragListener(new ChoiceDragListener());
    }

    private void make_editext() {
        LinearLayout flout = findViewById(R.id.empty_space);
        EditText newtext = new EditText(MainActivity.this);
        //newtext.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        newtext.setHint("add text here");
        newtext.setId(textboxid);
        ++textboxid;
        newtext.setBackgroundColor(0x00000000);
        newtext.setEms(10);
        newtext.setGravity(Gravity.CENTER);
        newtext.setLines(2);
        newtext.setTextColor(0xff000000);
        newtext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        flout.addView(newtext);
        String str = Integer.toString(textboxid);
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
    }

    private void shareimage() {
        make_meme_image();
        share_image();
    }

    private void share_image() {
        Intent share_intent = new Intent(Intent.ACTION_SEND);
        File shared_file = new File(getCacheDir(), "images/image.png");
        Uri imguri = FileProvider.getUriForFile(this, "com.mg.memegenerator.fileprovider", shared_file);
        share_intent.putExtra(Intent.EXTRA_STREAM, imguri);
        share_intent.setType("image/png");
        startActivity(share_intent);
    }

    private void make_meme_image() {
        FrameLayout fl = (FrameLayout) findViewById(R.id.frame);
        fl.setDrawingCacheEnabled(true);

        Bitmap bm = fl.getDrawingCache();

        File sharing_file = new File(getCacheDir(), "images");
        sharing_file.mkdirs();

        try {
            FileOutputStream stream = new FileOutputStream(sharing_file + "/image.png");
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fl.setDrawingCacheEnabled(false);
        fl.destroyDrawingCache();
    }

    public void pickimage(View view) {
        TextView tv = findViewById(R.id.editText);
        tv.setText("");
        TextView tv2 = findViewById(R.id.editText2);
        tv2.setText("");
        requestperm();
    }

    public void createintent() {
        Intent photointent = new Intent(Intent.ACTION_PICK);
        File photodirec = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Uri photouri = Uri.parse(photodirec.getPath());

        photointent.setDataAndType(photouri, "image/*");


        startActivityForResult(photointent, RequestCode);
    }

    public void requestperm() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUESTCODE);
        } else {
            createintent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT);
                    createintent();
                } else {

                    Toast.makeText(this, "permission failed", Toast.LENGTH_SHORT);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                Uri photouri = data.getData();

                ImageView imgview = (ImageView) findViewById(R.id.meme_image);
                Picasso.with(MainActivity.this).load(photouri).into(imgview);
            }

        }


        //Picasso.with(MainActivity.this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }

    private class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int x = (int)motionEvent.getRawX();
            final int y = (int)motionEvent.getRawY();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);


                //view.setVisibility(View.INVISIBLE);
                return true;
            } else{
                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent dragEvent) {
            View object = (View)dragEvent.getLocalState();

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:

                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    int x = (int)dragEvent.getX();
                    int y = (int)dragEvent.getY();
                    String str = Integer.toString(x) +"," +Integer.toString(y);
                    Log.d("posx", "onDrag:" + str);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    String dragData = item.getText().toString();

                    // Displays a message containing the dragged data.
                    Toast.makeText(MainActivity.this, "Dragged data is text ", Toast.LENGTH_SHORT).show();

                    // Turns off any color tints
                   // view.getBackground().clearColorFilter();

                    // Invalidates the view to force a redraw



                    ViewGroup owner = (ViewGroup) object.getParent();
                    owner.removeView(object);//remove the dragged view
                    FrameLayout container = (FrameLayout) v;//caste the view into LinearLayout as our drag acceptable layout is LinearLayout


                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins((int)dragEvent.getX(), (int)dragEvent.getY(), -250, -250);
                     object.setLayoutParams(layoutParams);
                    container.addView(object);//Add the dragged view
                    object.setOnTouchListener(null);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:


                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
