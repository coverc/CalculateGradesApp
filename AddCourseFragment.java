
package edu.uncc.inclass11;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import edu.uncc.inclass11.databinding.FragmentAddCourseBinding;

public class AddCourseFragment extends Fragment {
    public AddCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    FragmentAddCourseBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCourseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToGrades();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String courseNumber = binding.editTextCourseNumber.getText().toString();
                String courseName = binding.editTextCourseName.getText().toString();
                double courseHours = Double.parseDouble(binding.editTextCourseHours.getText().toString());
                int selectedId = binding.radioGroupGrades.getCheckedRadioButtonId();

                if(courseName.isEmpty() || courseNumber.isEmpty() || binding.editTextCourseHours.getText().toString().isEmpty()) {
                   Toast.makeText(getContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else if(selectedId == -1){
                    Toast.makeText(getContext(), "Please select a letter grade !!", Toast.LENGTH_SHORT).show();
                } else {
                    String courseLetterGrade;
                    int GPAGrade;
                    if(selectedId == R.id.radioButtonA) {
                        courseLetterGrade = "A";
                        GPAGrade = 4;
                    } else if(selectedId == R.id.radioButtonB) {
                        courseLetterGrade = "B";
                        GPAGrade = 3;
                    } else if(selectedId == R.id.radioButtonC) {
                        courseLetterGrade = "C";
                        GPAGrade = 2;
                    } else if(selectedId == R.id.radioButtonD) {
                        courseLetterGrade = "D";
                        GPAGrade = 1;
                    } else {
                        courseLetterGrade = "F";
                        GPAGrade = 0;
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    HashMap<String, Object> course = new HashMap<>();
                    course.put("GPAGrade", GPAGrade);
                    course.put("classCode", courseNumber);
                    course.put("className", courseName);
                    course.put("creditHours", courseHours);
                    course.put("grade", courseLetterGrade);
                    course.put("userID", FirebaseAuth.getInstance().getUid());
                    db.collection("classes").add(course).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mListener.goBackToGrades();
                        }
                    });


                }
            }
        });

    }
    AddClassListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddClassListener) context;
    }
    interface AddClassListener {
        void goBackToGrades();
    }
}