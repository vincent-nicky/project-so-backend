package com.wsj.so.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.so.datasource.*;
import com.wsj.so.model.dto.post.PostQueryRequest;
import com.wsj.so.model.dto.search.SearchRequest;
import com.wsj.so.model.dto.user.UserQueryRequest;
import com.wsj.so.model.entity.Picture;
import com.wsj.so.model.enums.SearchTypeEnum;
import com.wsj.so.model.vo.PostVO;
import com.wsj.so.model.vo.SearchVO;
import com.wsj.so.model.vo.UserVO;
import com.wsj.so.model.vo.VideoVo;
import com.wsj.so.datasource.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class SearchFacade {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    @Resource
    private VideoDataSource videoDataSource;

    public SearchVO searchAll(SearchRequest searchRequest, HttpServletRequest request){
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);

        //ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchRequest.getSearchText();

        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);
        if (searchTypeEnum == null) {
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                return pictureDataSource.doSearch(searchText, 1, 20);
            });
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                return userDataSource.doSearch(searchText,current,pageSize);
            });
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                return postDataSource.doSearch(searchText,current,pageSize);
            });
            CompletableFuture<Page<VideoVo>> videoTask = CompletableFuture.supplyAsync(() -> {
                return videoDataSource.doSearch(searchText,current,pageSize);
            });

            CompletableFuture.allOf(pictureTask, userTask, pictureTask).join();

            SearchVO searchVO = new SearchVO();
            try {
                searchVO.setPostList(postTask.get().getRecords());
                searchVO.setUserList(userTask.get().getRecords());
                searchVO.setPictureList(pictureTask.get().getRecords());
                searchVO.setVideoList(videoTask.get().getRecords());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return searchVO;

//            Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 20);
//
//            UserQueryRequest userQueryRequest = new UserQueryRequest();
//            userQueryRequest.setUserName(searchText);
//            Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
//
//            PostQueryRequest postQueryRequest = new PostQueryRequest();
//            postQueryRequest.setSearchText(searchText);
//            Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
//
//            Page<VideoVo> videoVoPage = videoDataSource.doSearch(searchText, current, pageSize);
//            SearchVO searchVO = new SearchVO();
//            searchVO.setPictureList(picturePage.getRecords());
//            searchVO.setUserList(userVOPage.getRecords());
//            searchVO.setPostList(postVOPage.getRecords());
//            searchVO.setVideoList(videoVoPage.getRecords());
//            return searchVO;

        } else {
            DataSource<?> dataSource = dataSourceRegistry.getDataSource(searchTypeEnum.getValue());
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            SearchVO searchVO = new SearchVO();
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
