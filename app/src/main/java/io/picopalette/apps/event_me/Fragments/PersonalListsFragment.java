package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.picopalette.apps.event_me.Activities.ListCreationActivity;
import io.picopalette.apps.event_me.Activities.ListDisplayActivity;
import io.picopalette.apps.event_me.Adapters.PersonalListsViewHolder;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 25-06-2017.
 */

public class PersonalListsFragment extends Fragment {

    public static final String mListTitle = "List Title";
    private RecyclerView personalListsRecyclerView;
    private FirebaseRecyclerAdapter<Object, PersonalListsViewHolder> listsAdapter;
    private DatabaseReference mListReference;
    private PersonalListsViewHolder lastViewHolder;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_personal_lists, container, false);

        FloatingActionButton createListFAB = (FloatingActionButton) v.findViewById(R.id.personalListsFAB);
        personalListsRecyclerView = (RecyclerView) v.findViewById(R.id.personal_lists_RecyclerView);
        personalListsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mListReference = FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.lists);

        createListFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder mB = new AlertDialog.Builder(getContext());
                View alertView = inflater.inflate(R.layout.dialog_list_title,null);
                final EditText listTitle = (EditText) alertView.findViewById(R.id.listTitleEditText);
                Button nextButton = (Button) alertView.findViewById(R.id.nextButton);
                mB.setView(alertView);
                final AlertDialog dialog = mB.create();
                dialog.show();

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(listTitle.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(),"Please enter the List's Title",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String title = listTitle.getText().toString();
                            Intent intent = new Intent(getContext(), ListCreationActivity.class);
                            intent.putExtra(mListTitle,title);
                            intent.putExtra("type", "personal");
                            startActivity(intent);
                            dialog.dismiss();
                            Toast.makeText(getContext(),"Click the add/remove button to add/remove the list_item_edit item",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        listsAdapter = new FirebaseRecyclerAdapter<Object, PersonalListsViewHolder>(Object.class, R.layout.card_list, PersonalListsViewHolder.class, mListReference) {
            @Override
            protected void populateViewHolder(final PersonalListsViewHolder viewHolder, Object model, final int position) {
                viewHolder.personalListTitle.setText(getRef(position).getKey());
                viewHolder.personalListCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.personalListActions.getVisibility() == View.VISIBLE)
                            viewHolder.personalListActions.setVisibility(View.GONE);
                        else {
                            clicked(viewHolder);
                            viewHolder.personalListActions.setVisibility(View.VISIBLE);
                        }
                    }
                });
                viewHolder.personalListView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ListDisplayActivity.class);
                        intent.putExtra("type", "personal");
                        intent.putExtra("title", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
                viewHolder.personalListEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ListCreationActivity.class);
                        intent.putExtra(mListTitle,getRef(position).getKey());
                        intent.putExtra("type", "personal");
                        startActivity(intent);
                    }
                });
                viewHolder.personalListDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                            builder = new AlertDialog.Builder(getActivity());
//                        }
                        builder.setTitle("Delete List")
                                .setMessage("All the entries under the list will be Deleted")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        getRef(position).removeValue();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });
            }
        };
        personalListsRecyclerView.setAdapter(listsAdapter);
        return v;
    }

    private void clicked(PersonalListsViewHolder viewHolder) {
        if(lastViewHolder != null) {
            lastViewHolder.personalListActions.setVisibility(View.GONE);
        }
        lastViewHolder = viewHolder;
    }
}
