package android.larrimorea.snapchat;

        import android.app.Activity;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AlertDialog;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.parse.FindCallback;
        import com.parse.GetCallback;
        import com.parse.Parse;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;
        import com.parse.ParseRelation;
        import com.parse.ParseUser;

        import java.text.ParseException;
        import java.util.ArrayList;
        import java.util.List;


public class SendPictureFragment extends Fragment {
    private List<String> arrayStrings  = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;
    protected ListView listView;
    private static Uri selectedPic = null;
    private String mFriendReqName;
    private View mView;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.send_image, container, false);

        pause = false;

        getFriends();

        return mView;
    }

    public void getFriends(){
        ParseRelation relation = ParseUser.getCurrentUser().getRelation("friends");
        ParseQuery<ParseUser> query = relation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, com.parse.ParseException e) {
                if (e == null) {
                    fillFriends(list);
                    displayFriends();
                } else {
                    Toast.makeText(getActivity(), "No Friends Found", Toast.LENGTH_SHORT);
                    Log.e("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void fillFriends(List<ParseUser> list){
        for(ParseObject ob: list){
            arrayStrings.add(ob.get("username").toString());
        }
        if(arrayStrings == null){
            arrayStrings.add("No Friends Yet. Add some Friends!");
        }
    }

    private void displayFriends(){
        listView = (ListView) mView.findViewById(R.id.listViewSend);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayStrings);
        listView.setAdapter(mArrayAdapter);
        setClickListener();
    }

    private void setClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    Intent intent = new Intent(getActivity(), ChoosePicActivity.class);
                    String str = (String) mArrayAdapter.getItem(position);
                    intent.putExtra("to", str);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static void setPicture(Uri pic){
        selectedPic = pic;
    }

    public static Uri getPicture(){
        return selectedPic;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.send_image, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_add_friend:
                makePopup();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makePopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Type your Friend's Name");

// Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFriendReqName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(mFriendReqName!= null){
                    searchForFriend();
                    dialog.cancel();
                }
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    public void searchForFriend(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", mFriendReqName);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {

                if (e == null) {
                    sendFriendRequest(user);
                } else {
                    Toast.makeText(getActivity(), "Friend not found!", Toast.LENGTH_SHORT).show();
                    Log.e("SendPictureFragment", "AddFriend " + e);
                }
            }
        });
    }

    public void sendFriendRequest(ParseUser user){
        ParseObject fr = new ParseObject("FriendRequests");
        fr.put("From", ParseUser.getCurrentUser().getUsername());
        fr.put("To", user.getUsername());
        fr.saveInBackground();


        Log.i("SendPicture", "Sending Friend Request to " + user.getUsername());
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
