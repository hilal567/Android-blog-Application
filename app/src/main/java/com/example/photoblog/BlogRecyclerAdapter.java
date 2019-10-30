package com.example.photoblog;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost>blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){

    this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        //after we receive data as a string do this
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        //get the ser id from the database
        String user_id = blog_list.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName , userImage);

                }else{
                    //Firebase exceptions
                }

            }
        });

        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }




    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private  View mView;
        private TextView descView;

        private ImageView blogImageView;

        private TextView blogDate;

        private TextView blogUserName;
        private CircleImageView blogUserImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }
        //method to get the post description
        public void setDescText(String descText){
              descView = mView.findViewById(R.id.blog_desc);
              descView.setText(descText);
        }
        //method to get the blog image
        public void setBlogImage(String downloadUri){

            blogImageView = mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).into(blogImageView);

        }
        public void setTime (String date){

        blogDate = mView.findViewById(R.id.blog_date);
        blogDate.setText(date);

        }
    public void setUserData(String name, String image){

            //get data from the  fields in the Xml file
          blogUserImage = mView.findViewById(R.id.blog_user_image);
          blogUserName = mView.findViewById(R.id.blog_user_name);

          //set the data into the strings and display it.
        blogUserName.setText(name);


        //set a placeholder in case the user does not have a profile picture.
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);
        Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

    }

    }


}
