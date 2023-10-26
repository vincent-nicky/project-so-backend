package com.wsj.so.model.vo;

import com.wsj.so.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchVO implements Serializable {

    private List<PostVO> postList;

    private List<UserVO> userList;

    private List<Picture> pictureList;

    private List<VideoVo> videoList;

    private List<?> dataList;

    private static final long serialVersionUID = 5265687897079265408L;
}
