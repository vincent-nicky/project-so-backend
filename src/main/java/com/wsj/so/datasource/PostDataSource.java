package com.wsj.so.datasource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.so.model.dto.post.PostQueryRequest;
import com.wsj.so.model.entity.Post;
import com.wsj.so.model.vo.PostVO;
import com.wsj.so.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务实现
 *
* 
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        postQueryRequest.setSearchText(searchText);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postService.getPostVOPage(postPage,request);
    }
}




