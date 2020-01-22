package com.ast.taskApp;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.graphics.Typeface.BOLD;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Subscription3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Subscription3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Subscription3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   /* private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
*/
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*private OnFragmentInteractionListener mListener;*/

    public Subscription3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Subscription3.
     */
    // TODO: Rename and change types and number of parameters
    public static Subscription3 newInstance() {
        Subscription3 fragment = new Subscription3();
       /* Bundle args = new Bundle();
        *//*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*//*
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription3,container,false);
        Spannable spannable = new SpannableString("$6.99/yr");
        spannable.setSpan(new StyleSpan(BOLD),0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.5f),0,5,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = view.findViewById(R.id.heading);
        textView.setText(spannable);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri tuneuri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(tuneuri);
        }
    }*/

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri tuneuri);
    }*/
}
