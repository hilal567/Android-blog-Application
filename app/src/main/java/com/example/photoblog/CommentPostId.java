package com.example.photoblog;

import com.google.firebase.firestore.Exclude;

public class CommentPostId {

    @Exclude
    public String CommentPostId;

    public <T extends CommentPostId> T withId(final String id) {
        this.CommentPostId = id;
        return (T) this;
    }
}
