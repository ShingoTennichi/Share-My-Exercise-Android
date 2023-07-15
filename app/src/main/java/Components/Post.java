package Components;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.showmyexerciseapp.BuildConfig;
import com.example.showmyexerciseapp.MainFragment;

import org.json.JSONObject;

public class Post implements SavePostInterface {
    private int authorId;
    private String imgUrl;
    private String text;


    public int getAuthorId() {
        return authorId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void savePost(Context context) {
        // save post data as json object
        JSONObject json = new JSONObject();
        try {
            json.put("authorId", authorId);
            json.put("imgUrl", imgUrl);
            json.put("text", text);
        } catch (Exception e) {
            Log.e("create JSONObject", "" + e.getMessage());
            return;
        }

        // create http request
        // make sure Build Variant is debug or release
        String requestPath = "/api/post/new-post";
        final String url = BuildConfig.BASE_URL + requestPath;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // get json data from response
                        JSONObject data = response.getJSONObject("data");

                        // if there is errors, show error message by toast
                        String status = data.getString("status");
                        if(status.equals("Error")) throw new Exception(data.getString("message"));

                        // navigate to main fragment
                        Navigate.navigateToNextFragment(context, new MainFragment());
                        Toast.makeText(context, "onResponse: Successfully posted", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("onResponse", "" + e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Server error: Try later", Toast.LENGTH_SHORT).show();
                }
            }
        );

        // execute the request
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
        Toast.makeText(context, "posting...", Toast.LENGTH_SHORT).show();
    }
}
