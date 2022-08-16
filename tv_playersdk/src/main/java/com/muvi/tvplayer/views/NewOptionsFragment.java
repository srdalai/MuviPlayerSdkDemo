package com.muvi.tvplayer.views;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.muvi.tvplayer.R;
import com.muvi.tvplayer.interfaces.PlayerOptionsSelectedListener;

import java.util.ArrayList;

//TODO Rename to PlayerOptionsFragment
public class NewOptionsFragment extends DialogFragment {

    private static final String ARG_PARAM_SUB = "param_sub";
    private static final String ARG_PARAM_AUDIO = "param_audio";
    private static final String ARG_PARAM_HQ = "param_hq";

    private static final String ARG_CUR_SUB_POS = "param_sub_cur_pos";
    private static final String ARG_CUR_AUDIO_POS = "param_audio_cur_pos";
    private static final String ARG_CUR_HQ_POS = "param_hq_cur_pos";

    ArrayList<String> subList;
    ArrayList<String> audioList;
    ArrayList<String> resolutionList;

    int subTrackPos = 0;
    int audioTrackPos = 0;
    int videoTrackPos = 0;

    private PlayerOptionsSelectedListener listener = null;

    enum CurrentList {VIDEO, AUDIO, TEXT}
    CurrentList currentList = CurrentList.VIDEO;

    public void setPlayerOptionsSelectedListener(PlayerOptionsSelectedListener listener) {
        this.listener = listener;
    }

    public static NewOptionsFragment newInstance(ArrayList<String> subList, int currentSubPos, ArrayList<String> audioList, int currentAudioPos, ArrayList<String> resolutionList, int currentHQPos) {
        NewOptionsFragment fragment = new NewOptionsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM_SUB, subList);
        args.putStringArrayList(ARG_PARAM_AUDIO, audioList);
        args.putStringArrayList(ARG_PARAM_HQ, resolutionList);

        args.putInt(ARG_CUR_SUB_POS, currentSubPos);
        args.putInt(ARG_CUR_AUDIO_POS, currentAudioPos);
        args.putInt(ARG_CUR_HQ_POS, currentHQPos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.MuviPlayer_FullScreen_Dialog);
        if (getArguments() != null) {
            subList = getArguments().getStringArrayList(ARG_PARAM_SUB);
            audioList = getArguments().getStringArrayList(ARG_PARAM_AUDIO);
            resolutionList = getArguments().getStringArrayList(ARG_PARAM_HQ);

            subTrackPos = getArguments().getInt(ARG_CUR_SUB_POS, 0);
            audioTrackPos = getArguments().getInt(ARG_CUR_AUDIO_POS, 0);
            videoTrackPos = getArguments().getInt(ARG_CUR_HQ_POS, 0);
        }
    }

    ImageView imageViewAction;
    TextView headingTextView;
    LinearLayout optionsLinear, resolutionLinear, audioLinear, subLinear, settingsLinear, detailsLinear;
    ListView optionsListView;
    TextView tvCurResolution, tvCurAudio, tvCurSub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewAction = view.findViewById(R.id.imageViewAction);
        headingTextView = view.findViewById(R.id.headingTextView);

        optionsLinear = view.findViewById(R.id.optionsLinear);
        resolutionLinear = view.findViewById(R.id.resolutionLinear);
        audioLinear = view.findViewById(R.id.audioLinear);
        subLinear = view.findViewById(R.id.subLinear);
        settingsLinear = view.findViewById(R.id.settingsLinear);
        detailsLinear = view.findViewById(R.id.detailsLinear);

        optionsListView = view.findViewById(R.id.optionsListView);

        tvCurResolution = view.findViewById(R.id.tvCurResolution);
        tvCurAudio = view.findViewById(R.id.tvCurAudio);
        tvCurSub = view.findViewById(R.id.tvCurSub);

        resolutionLinear.setOnClickListener(view1 -> {
            currentList = CurrentList.VIDEO;
            PlayerOptionsAdapter optionsAdapter = new PlayerOptionsAdapter(requireContext(), resolutionList, videoTrackPos);
            optionsListView.setAdapter(optionsAdapter);
            optionsListView.setSelection(videoTrackPos);
            headingTextView.setText("Settings \t \u203A \t Resolution");
            showListView();
        });

        audioLinear.setOnClickListener(view1 -> {
            currentList = CurrentList.AUDIO;
            PlayerOptionsAdapter optionsAdapter = new PlayerOptionsAdapter(requireContext(), audioList, audioTrackPos);
            optionsListView.setAdapter(optionsAdapter);
            optionsListView.setSelection(audioTrackPos);
            headingTextView.setText("Settings \t \u203A \t Audio");
            showListView();
        });

        subLinear.setOnClickListener(view1 -> {
            currentList = CurrentList.TEXT;
            PlayerOptionsAdapter optionsAdapter = new PlayerOptionsAdapter(requireContext(), subList, subTrackPos);
            optionsListView.setAdapter(optionsAdapter);
            optionsListView.setSelection(subTrackPos);
            headingTextView.setText("Settings \t \u203A \t Subtitles");
            showListView();
        });

        settingsLinear.setOnClickListener(view1 -> {
            listener.openSettings();
        });

        detailsLinear.setOnClickListener(view1 -> {
            listener.showDetails();
            requireDialog().cancel();
        });

        optionsListView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (currentList) {
                case VIDEO:
                    if (position != videoTrackPos) {
                        listener.onVideoTrackSelected(position);
                        dismiss();
                    } else {
                        requireDialog().cancel();
                    }
                    break;
                case AUDIO:
                    if (position != audioTrackPos) {
                        listener.onAudioTrackSelected(position);
                        dismiss();
                    } else {
                        requireDialog().cancel();
                    }
                    break;
                case TEXT:
                    if (position != subTrackPos) {
                        listener.onSubtitleSelected(position);
                        dismiss();
                    } else {
                        requireDialog().cancel();
                    }
            }

        });

        requireDialog().setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (optionsListView.getVisibility() == View.VISIBLE) {
                    optionsListView.setVisibility(View.GONE);
                    optionsLinear.setVisibility(View.VISIBLE);
                    requestOptionsFocus();
                    return true;
                }
            }
            return false;
        });

        imageViewAction.setOnClickListener(view12 -> {
            if (optionsListView.getVisibility() == View.VISIBLE) {
                optionsListView.setVisibility(View.GONE);
                optionsLinear.setVisibility(View.VISIBLE);
                requestOptionsFocus();
            } else {
                requireDialog().cancel();
            }
        });

        if (resolutionList.size() > 1) {
            tvCurResolution.setText(resolutionList.get(videoTrackPos));
            resolutionLinear.setVisibility(View.VISIBLE);
        }
        if (audioList.size() > 1) {
            tvCurAudio.setText(audioList.get(audioTrackPos));
            audioLinear.setVisibility(View.VISIBLE);
        }
        if (subList.size() > 1) {
            tvCurSub.setText(subList.get(subTrackPos));
            subLinear.setVisibility(View.VISIBLE);
        }

        requestOptionsFocus();
    }

    private void showListView() {
        imageViewAction.setImageResource(R.drawable.muvi_player_arrow_back);
        optionsLinear.setVisibility(View.GONE);
        optionsListView.setVisibility(View.VISIBLE);
        optionsListView.requestFocus();
    }

    private void requestOptionsFocus() {
        if (resolutionList.size() > 1) {
            resolutionLinear.requestFocus();
        } else if (audioList.size() > 1) {
            audioLinear.requestFocus();
        } else if (subList.size() > 1) {
            subLinear.requestFocus();
        }
        imageViewAction.setImageResource(R.drawable.muvi_player_round_cancel);
        headingTextView.setText("Settings");
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        listener.onCancelled();
    }
}