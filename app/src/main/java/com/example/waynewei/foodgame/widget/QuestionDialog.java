package com.example.waynewei.foodgame.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.waynewei.foodgame.GameActivity;
import com.example.waynewei.foodgame.R;
import com.example.waynewei.foodgame.info.model.Question;
import com.example.waynewei.foodgame.info.model.Food;
import com.example.waynewei.foodgame.info.viewholder.FoodListViewHolder;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.melnykov.fab.FloatingActionButton;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class QuestionDialog extends DialogFragment {

    public static final String TAG = "QuestionDialog";
    private MaterialDialog dialog;
    private Food food;
    private FoodListViewHolder holder;
    private String question_number;
    private FABProgressCircle fabProgressCircle;
    private FloatingActionButton mAudio;
    private AudioPlayer audioPlayer;
    private CircularProgressView progressView;
    private Realm realm;
    private RealmList<Question> questions;
    private int num;
    private ImageView mImage;
    private TextView mText;

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public OnChangedListener onChangedListener = null;

    public QuestionDialog(){
        realm = Realm.getDefaultInstance();
    }

    public void setInstance(RealmList<Question> questions, String question_number, int num, Food food, FoodListViewHolder holder){
        this.questions = questions;
        this.question_number = question_number;
        this.num = num;
        this.food = food;
        this.holder = holder;
    }


    public interface OnChangedListener {
        void onChanged();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (audioPlayer != null) {
            audioPlayer.restAudio();
        }

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_food, null);

        
        mText = (TextView) view.findViewById(R.id.question);
        mImage = (ImageView) view.findViewById(R.id.image);
        fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle);
        progressView = (CircularProgressView) view.findViewById(R.id.loading_progress);
        mAudio = (FloatingActionButton) view.findViewById(R.id.audio);

        Log.d("type", food.getType());
        switch (food.getType()){
            case "text":
                mText.setText(food.getQuestion());
                mText.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.INVISIBLE);
                fabProgressCircle.setVisibility(View.INVISIBLE);
                mAudio.setVisibility(View.INVISIBLE);
                break;
            case "poem":
                String tmp = "";
                String[] array = food.getQuestion().split(" ");
                for (String string : array){
                    tmp += string + "\n";
                }
                Log.d("test", food.getQuestion());
                Log.d("test", String.valueOf(array));
                mText.setText(tmp);
                mText.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.INVISIBLE);
                fabProgressCircle.setVisibility(View.INVISIBLE);
                mAudio.setVisibility(View.INVISIBLE);
                break;
            case "image":
                mText.setVisibility(View.INVISIBLE);
                fabProgressCircle.setVisibility(View.INVISIBLE);
                mAudio.setVisibility(View.INVISIBLE);
                mImage.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(food.getQuestion()).into(mImage);
                break;
            case "audio":
                mText.setVisibility(View.INVISIBLE);
                mImage.setVisibility(View.INVISIBLE);
                setAudio(food.getQuestion());
                fabProgressCircle.setVisibility(View.VISIBLE);
                mAudio.setVisibility(View.VISIBLE);
                break;
        }


        final EditText answerInput = (EditText) view.findViewById(R.id.answer);
        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.inputLayout);

        final RealmResults<Question> questionResult = realm.where(Question.class).equalTo("answer", food.getName()).findAll();

        Log.d("text", questionResult.toString());

            if (!questionResult.isEmpty()){
                answerInput.setText(food.getName());
                answerInput.setEnabled(false);
                answerInput.setFocusableInTouchMode(false);
                answerInput.setFocusable(false);
                textInputLayout.setEnabled(false);
                answerInput.setTextColor(getActivity().getResources().getColor(R.color.photo_tint));
            }

        textInputLayout.setCounterMaxLength(food.getName().length());
        answerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>food.getName().length()){
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(getString(R.string.input_length_warning) + food.getName().length() + getString(R.string.character));
                }
                else{
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>food.getName().length()){
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(getString(R.string.input_length_warning) + food.getName().length() + getString(R.string.character));
                }
                else{
                    textInputLayout.setErrorEnabled(false);
                }

            }
        });

        dialog = new MaterialDialog.Builder(getActivity())
                .customView(view, true)
                .autoDismiss(false)
                .title(question_number)
                .titleGravity(GravityEnum.CENTER)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if(questionResult.isEmpty()) {
                            if (answerInput.getText().toString().equals(food.getName())) {
                                Glide.with(getActivity()).load(food.getPhoto()).into(holder.photo);
                                MediaPlayer.create(getActivity(), R.raw.correct).start();
                                holder.title.setText(food.getName());

                                realm.beginTransaction();
                                Question question = realm.createObject(Question.class);
                                question.setAnswer(food.getName());
                                questions.add(question);
                                realm.commitTransaction();
                                dialog.dismiss();

                                if (getActivity() instanceof GameActivity) {
                                    int size = realm.where(Question.class).findAll().size();
                                    Log.d("test", size+"");
                                    int x = size*100/num+ size*100%num;
                                    ((GameActivity) getActivity()).setCurrentProgress(x);
                                }

                                Toast.makeText(getActivity(), getActivity().getString(R.string.correct), Toast.LENGTH_SHORT).show();
                            } else {
                                textInputLayout.setErrorEnabled(true);
                                textInputLayout.setError(getActivity().getString(R.string.wrong));
                                MediaPlayer.create(getActivity(), R.raw.wrong).start();
                            }
                        }
                        else {
                            dialog.dismiss();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();


        return dialog;
    }

    private void setAudio(final String url) {
        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioPlayer==null){
						audioPlayer = new AudioPlayer(getActivity(), mAudio, progressView, fabProgressCircle, url);
					}
					else{
						audioPlayer.playAudio();
					}
            }
        });

        mAudio.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if(audioPlayer!=null){
						audioPlayer.stopAudio();
					}
					return true;
				}
			});

    }


}
