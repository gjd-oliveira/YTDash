package br.ytdash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoFormat {

    private String formatId;
    private String ext;
    private String vcodec;
    private String acodec;
    private int width;
    private int height;
    private double fps;
    private Double abr;

    public VideoFormat() {
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getVcodec() {
        return vcodec;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public Double getAbr() {
        return abr;
    }

    public void setAbr(Double abr) {
        this.abr = abr;
    }
}