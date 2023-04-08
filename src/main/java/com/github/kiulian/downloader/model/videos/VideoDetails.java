package com.github.kiulian.downloader.model.videos;


import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.kiulian.downloader.model.AbstractVideoDetails;

public class VideoDetails extends AbstractVideoDetails {

    private List<String> keywords;
    private String shortDescription;
    private long viewCount;
    private int averageRating;
    private boolean isLiveContent;
    private String liveUrl;

    public VideoDetails(String videoId) {
        this.videoId = videoId;
    }

    public VideoDetails(JSONObject json, String liveHLSUrl) {
        super(json);
        title = json.getString("title");
        author = json.getString("author");
        isLive = json.getBooleanValue("isLive");

        keywords = json.containsKey("keywords") ? json.getJSONArray("keywords").toJavaList(String.class) : new ArrayList<String>();
        shortDescription = json.getString("shortDescription");
        averageRating = json.getIntValue("averageRating");
        viewCount = json.getLongValue("viewCount");
        isLiveContent = json.getBooleanValue("isLiveContent");
        liveUrl = liveHLSUrl;
    }

    @Override
    public boolean isDownloadable()  {
        return !isLive() && !(isLiveContent && lengthSeconds() == 0);
    }

    @JsonGetter
    public List<String> keywords() {
        return keywords;
    }

    @JsonGetter
    public String description() {
        return shortDescription;
    }

    @JsonGetter
    public long viewCount() {
        return viewCount;
    }

    @JsonGetter
    public int averageRating() {
        return averageRating;
    }

    @JsonGetter
    public boolean isLiveContent() {
        return isLiveContent;
    }

    @JsonGetter
    public String liveUrl() {
        return liveUrl;
    }
}
