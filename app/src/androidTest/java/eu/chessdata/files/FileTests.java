package eu.chessdata.files;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.chessdata.R;
import eu.chessdata.utils.Constants;
import javafo.pairings.JaVaFo;

/**
 * Created by Bogdan Oloeriu on 7/18/2016.
 */
public class FileTests extends AndroidTestCase{
    private String TAG = Constants.LOG_TAG;
    public void test1CreateFile() throws Exception{
        String filename = "my_file1";
        String stringContent = "Hello world!";
        FileOutputStream outputStream;

        outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(stringContent.getBytes());

        outputStream.close();
    }

    public void test2OpenTRFXSample() throws Exception{
        //InputStream inputStream = mContext.getAssets().open("/raw/trfx_sample.txt");
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.trfx_sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String currentLine;
        while ((currentLine=reader.readLine())!=null){
            Log.d(TAG,currentLine);
        }

        Log.d(TAG,"test2OpenTRFXSample ok");
    }

    public void test3JaVaFo() throws Exception{
        String fileName = "test3JaVaFo.txt";

        File file = new File( mContext.getFilesDir()+"/"+fileName);
        if (!file.exists()){
            file.createNewFile();
        }
        String filePath = file.getAbsolutePath();



        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        InputStream inputStream = mContext.getResources().openRawResource(R.raw.trfx_sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String currentLine;
        while ((currentLine=reader.readLine())!=null){
            bufferedWriter.write(currentLine+"\n");
        }

        bufferedWriter.close();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while((line = bufferedReader.readLine())!= null){
            stringBuffer.append(line +"\n");
        }
        Log.d(TAG,stringBuffer.toString());
        Log.d(TAG,"Created file: " +filePath);

        String paringFilePath = filePath.replace(fileName,"parings.txt");
        Log.d(TAG,"Paring file: " + paringFilePath);

        File paringFile = new File(paringFilePath);
        if (paringFile.exists()){
            Log.d(TAG,"paringFile already exists");
        }

        String args[] = {filePath,"-p", paringFilePath};
        //the OneJar raper is not working
        //Boot.main(args);


        //directly using JaVaFo inside main.jar from OneJar is not working
        JaVaFo.main(args);

        if (!paringFile.exists()){
            throw new IllegalStateException("paringFile was not created");
        }
    }
}
