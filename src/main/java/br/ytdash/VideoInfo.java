package br.ytdash;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfo {

    private String id;
    private String title;
    private double duration;
    private List<VideoFormat> formats;

    public VideoInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<VideoFormat> getFormats() {
        return formats;
    }

    public void setFormats(List<VideoFormat> formats) {
        this.formats = formats;
    }
}