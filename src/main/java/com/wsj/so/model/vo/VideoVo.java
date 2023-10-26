package com.wsj.so.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoVo implements Serializable {
    private String arcurl;
    private String pic;
    private String title;
    private String description;
    private String author;
    private Integer pubdate;
    private String upic;
    private static final long serialVersionUID = 7037843325406822290L;
}
