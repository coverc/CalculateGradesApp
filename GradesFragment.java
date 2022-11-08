

package edu.uncc.inclass11;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.uncc.inclass11.databinding.FragmentGradesBinding;
import edu.uncc.inclass11.databinding.GradeRowItemBinding;

public class GradesFragment extends Fragment {
    String classCode;
    String className;
    String letterGrade;
    String createdByUID;
    String creditHours;
    String gpa;
    String docID;
    Grade addClass;
    int creditHoursInt;
    double gpaInt;
    double totalGPA;
 //   MenuInflater menuInflater;
    public GradesFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.actionbar1, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.plus:
                mListener.addClass();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    FragmentGradesBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
//        menuInflater.inflate(R.menu.actionbar1, Menu menu);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DecimalFormat threeDForm = new DecimalFormat("#.##");

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

        db.collection("classes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mGrades.clear();
                creditHoursInt = 0;
                gpaInt = 0;
                totalGPA = 0;
                for (QueryDocumentSnapshot document: value){
                    createdByUID = document.getData().get("userID").toString();
                    classCode = document.getString("classCode");
                    className = document.getString("className");
                    letterGrade = document.getString("grade");
                    gpa = String.valueOf(document.getLong("GPAGrade"));
                    creditHours = String.valueOf(document.getLong("creditHours"));
                    docID = document.getId().toString();

                    addClass = new Grade(classCode, className, creditHours, letterGrade, createdByUID, gpa, docID);
                    if(addClass.creadted_by_uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        mGrades.add(addClass);
                    }
                }
                gradesAdapter.notifyDataSetChanged();
                for (int i = 0; i < mGrades.size(); i++){
                    creditHoursInt = creditHoursInt + Integer.parseInt(mGrades.get(i).creditHours);
                    gpaInt = gpaInt + (Integer.parseInt(mGrades.get(i).gpa) * (Integer.parseInt(mGrades.get(i).creditHours)));
                }
                binding.textView2.setText("Hours " + String.valueOf(creditHoursInt));
                totalGPA = Double.parseDouble(threeDForm.format(gpaInt/creditHoursInt));
                if (mGrades.isEmpty()){
                    binding.textViewGPA.setText("GPA: 4.0");
                }else {
                    binding.textViewGPA.setText("GPA: " + String.valueOf(totalGPA));
                }
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gradesAdapter = new GradesAdapter(mGrades);
        binding.recyclerView.setAdapter(gradesAdapter);
    }

    GradesAdapter gradesAdapter;
    ArrayList<Grade> mGrades = new ArrayList<>();

    class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradesViewHolder>{
        ArrayList<Grade> grades = new ArrayList<>();

        public GradesAdapter(ArrayList<Grade> gradeData){
            this.grades = gradeData;
        }

        @NonNull
        @Override
        public GradesAdapter.GradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GradeRowItemBinding binding = GradeRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new GradesViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull GradesAdapter.GradesViewHolder holder, int position) {
            Grade grade = mGrades.get(position);
            holder.setupUI(grade);
        }

        @Override
        public int getItemCount() {
            return mGrades.size();
        }

        class GradesViewHolder extends RecyclerView.ViewHolder{
            GradeRowItemBinding mBinding;
            Grade mGrade;
            public GradesViewHolder(GradeRowItemBinding binding){
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Grade grade){
                mGrade = grade;

                mBinding.textViewCourseNumber.setText(grade.getClassCode());
                mBinding.textViewCourseName.setText(grade.getClassName());
                mBinding.textViewCourseHours.setText(grade.getCreditHours());
                mBinding.textViewCourseLetterGrade.setText(grade.getGrade());
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("classes").document(grade.docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("demo", "onSuccess: deleted");
                            }
                        });
                    }
                });
            }
        }
    }

    GradesListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (GradesListener) context;
    }
    interface GradesListener{
        void addClass();
        void logout();
    }
}