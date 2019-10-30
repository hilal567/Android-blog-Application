package com.example.photoblog;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String BloggPostId;

    public <T extends BlogPostId> T withId(final String id) {
        this.BloggPostId = id;
        return (T) this;
    }

}
