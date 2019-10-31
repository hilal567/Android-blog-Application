package com.example.photoblog;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost>blog_list;
    public List<User>user_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list){

    this.blog_list = blog_list;
    this.user_list = user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).BloggPostId;
        final String currentUserId  = firebaseAuth.getCurrentUser().getUid();


        //after we receive data as a string do this
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);


        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumbUri);

        //get the ser id from the database
        String blog_user_id = blog_list.get(position).getUser_id();

        //check if the person logged in is the one who posted the image
        if(blog_user_id.equals(currentUserId)){

            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        }


        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();

        holder.setUserData(userName , userImage);

                //get the date on which the post was made
        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);


        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        try {
    //Get Likes Count
    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e == null) {
                if (!documentSnapshots.isEmpty()) {
                    int count = documentSnapshots.size();
                    holder.updateLikesCount(count);

                } else {
                    holder.updateLikesCount(0);
                }
            }
        }
    });
            }catch (Exception e){

    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

}

try {
    //Get Likes
    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
    if (e == null) {
        if (documentSnapshot.exists()) {

            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_accent));

        } else {

            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_gray));

        }
    }

        }
    });
}catch (Exception e){

    Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();


}
        //Likes feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){

                            Map <String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        }else{


                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        }

                    }
                });


            }
        });


   //this is the on click listener to when a person presses the comment button
    holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
       @Override
        public void onClick(View view) {

           Intent commentIntent = new Intent(context, CommentsActivity.class);
            commentIntent.putExtra("blog_post_id", blogPostId);
            context.startActivity(commentIntent);

       }
  });

    //add an onclick Listener  when a user wants to delete there post
        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the button is clicked use this query to delete the post
                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        blog_list.remove(position);
                        user_list.remove(position);


                    }
                });
            }
        });

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

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        private ImageView blogCommentBtn;

        private ImageButton blogDeleteBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            //link the like button with the adapter class with the use  of the like id.
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);

            blogDeleteBtn = mView.findViewById(R.id.blog_delete_btn);


        }
        //method to get the post description
        public void setDescText(String descText){
              descView = mView.findViewById(R.id.blog_desc);
              descView.setText(descText);
        }
        //method to get the blog image
        public void setBlogImage(String downloadUri,String thumbUri){

            blogImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_image);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);
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

        public void updateLikesCount(int count){

            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");

        }

    }


}
