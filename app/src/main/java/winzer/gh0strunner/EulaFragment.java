package winzer.gh0strunner;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by franschl on 5/23/15.
 */
public class EulaFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static EulaFragment newInstance(int sectionNumber) {
        EulaFragment fragment = new EulaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_eula, container, false);

        // make Link in gpl clickable
        TextView aboutText = (TextView) rootView.findViewById(R.id.eula_text);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
