package com.wsj.so.controller;

import com.wsj.so.common.BaseResponse;
import com.wsj.so.common.ResultUtils;
import com.wsj.so.manager.SearchFacade;
import com.wsj.so.model.dto.search.SearchRequest;
import com.wsj.so.model.vo.SearchVO;
import com.wsj.so.service.PictureService;
import com.wsj.so.service.PostService;
import com.wsj.so.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 *
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

}
