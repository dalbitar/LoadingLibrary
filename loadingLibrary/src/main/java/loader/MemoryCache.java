package loader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10,1.5f,true));//Last argument true for LRU ordering
    private long currentAllocatedSize = 0;
    private long maxLimit = 1000000;//max memory in bytes

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
    
    public void setLimit(long new_limit){
        maxLimit = new_limit;
        Log.i(TAG, "MemoryCache will use up to "+maxLimit/1024./1024.+"MB");
    }

    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            return cache.get(id);
        }catch(NullPointerException ex){
    		ex.printStackTrace();
        }
        return null;
    }
    
    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id)) {
                currentAllocatedSize -= getSizeInBytes(cache.get(id));
            }
            
            cache.put(id, bitmap);
            currentAllocatedSize += getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    private void checkSize() {
        Log.i(TAG, "cache size="+currentAllocatedSize+" length=" + cache.size());
        if(currentAllocatedSize > maxLimit){
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            
            while(iter.hasNext()){
                Entry<String, Bitmap> entry = iter.next();
                currentAllocatedSize -= getSizeInBytes(entry.getValue());
                iter.remove();
                
                if(currentAllocatedSize <= maxLimit) {
                    break;
                }
            }
            
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            cache.clear();
            currentAllocatedSize = 0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
