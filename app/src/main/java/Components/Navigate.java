package Components;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.showmyexerciseapp.GetStartedActivity;
import com.example.showmyexerciseapp.MainActivity;
import com.example.showmyexerciseapp.R;
import com.example.showmyexerciseapp.SignInActivity;
import com.example.showmyexerciseapp.SignUpActivity;

public class Navigate {

    public static void navigateToNextActivity(Context context, Class targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
    }

    public static void navigateToNextFragment(Context context, Fragment targetFragment) {
        ((FragmentActivity) context)
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame_layout, targetFragment).addToBackStack(null)
            .commit();
    }
}
