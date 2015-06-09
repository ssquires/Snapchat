package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {
    protected ProgressBar progressBar;
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        listView = (ListView)findViewById(R.id.listView);

        listView.setEmptyView(progressBar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id==0){
                    Intent intent = new Intent(getApplicationContext(), Inbox.class);
                    startActivity(intent);
                }else if(id==1){
                    Intent intent = new Intent(getApplicationContext(), TakePicture.class);
                    startActivity(intent);
                }else if(id==2){
                    Intent intent = new Intent(getApplicationContext(), SendPicture.class);
                    startActivity(intent);
                }else{
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                }

                //Uri blogUri = Uri.parse(BlogPostParser.get().posts.get(position).url);
                //intent.setData(blogUri);


            }
        });

    }
}
