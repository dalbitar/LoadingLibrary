package loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.widget.ImageView;


public class ImageLoader {
    
    MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread
    
    public ImageLoader(Context context){
        executorService = Executors.newFixedThreadPool(5);
    }
        
    public void DisplayOrQueueImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        
        if(bitmap == null) {
        	queuePhoto(url, imageView);
            imageView.setImageDrawable(null);
            //imageView.setImageBitmap(GetNoImageBitmap());
        } else {
        	imageView.setImageBitmap(bitmap);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(photoToLoad));
    }
    
    public Bitmap getBitmap(String url) 
    {
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           
           if(ex instanceof OutOfMemoryError) {
               memoryCache.clear();
           }
           
           return null;
        }
    }

    /*public Bitmap getBitmapFromService(String picturePathUrl) 
    {
    	return new DistributionServiceClient(null).GetImageFromService(picturePathUrl);
    }*/
          
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        
        public PhotoToLoad(String u, ImageView i){
            url = u; 
            imageView = i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad = photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad)) {
                    return;
                }
                
                Bitmap bmp = getBitmap(photoToLoad.url);
                //Bitmap bmp = getBitmapFromService(photoToLoad.url);
                
                if (bmp != null) {
	                memoryCache.put(photoToLoad.url, bmp);
	                
	                if(imageViewReused(photoToLoad)) {
	                    return;
	                }
	                
	                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
	                handler.post(bd);
                }
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag = imageViews.get(photoToLoad.imageView);
        
        if(tag == null || !tag.equals(photoToLoad.url)) {
            return true;
        }
        
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
        	bitmap = b;
        	photoToLoad = p;
    	}
        
        public void run()
        {
            if(imageViewReused(photoToLoad)) {
                return;
            }
            
            if(bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageDrawable(null);
            }
        }
    }
    
    public void clearCache() {
        memoryCache.clear();
    }
}
