package com.wsj.so.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.so.common.ErrorCode;
import com.wsj.so.datasource.*;
import com.wsj.so.exception.BusinessException;
import com.wsj.so.model.dto.post.PostQueryRequest;
import com.wsj.so.model.dto.search.SearchRequest;
import com.wsj.so.model.dto.user.UserQueryRequest;
import com.wsj.so.model.entity.Picture;
import com.wsj.so.model.enums.SearchTypeEnum;
import com.wsj.so.model.vo.PostVO;
import com.wsj.so.model.vo.SearchVO;
import com.wsj.so.model.vo.UserVO;
import com.wsj.so.model.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

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

    public SearchVO searchData(SearchRequest searchRequest, HttpServletRequest request) {
        // 获取数据
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);

        // 如果没有传入搜索类型，一次搜索全部数据
        if (searchTypeEnum == null) {
            return searchAllData(searchText, pageSize, current);
        } else {
            // 搜索单项数据
            DataSource<?> dataSource = dataSourceRegistry.getDataSource(searchTypeEnum.getValue());
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            SearchVO searchVO = new SearchVO();
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }

    private SearchVO searchAllData(String searchText, long pageSize, long current) {

        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() ->
                pictureDataSource.doSearch(searchText, 1, 20)
        );

        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            return userDataSource.doSearch(searchText, current, pageSize);
        });

        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            return postDataSource.doSearch(searchText, current, pageSize);
        });

        CompletableFuture<Page<VideoVo>> videoTask = CompletableFuture.supplyAsync(() ->
                videoDataSource.doSearch(searchText, current, pageSize)
        );

        CompletableFuture.allOf(pictureTask, userTask, pictureTask).join();

        SearchVO searchVO = new SearchVO();
        try {
            searchVO.setPostList(postTask.get().getRecords());
            searchVO.setUserList(userTask.get().getRecords());
            searchVO.setPictureList(pictureTask.get().getRecords());
            searchVO.setVideoList(videoTask.get().getRecords());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "searchVO.set 获取数据失败");
        }
        return searchVO;
    }
}
