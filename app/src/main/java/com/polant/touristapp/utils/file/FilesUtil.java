package com.polant.touristapp.utils.file;

import java.io.File;

/**
 * Created by Антон on 04.02.2016.
 */
public class FilesUtil {

    public static boolean deleteFile(String path){
        File image = new File(path);
        return image.delete();
    }

}
