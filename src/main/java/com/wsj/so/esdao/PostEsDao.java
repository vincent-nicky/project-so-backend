package com.wsj.so.esdao;

import com.wsj.so.model.dto.post.PostEsDTO;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
* 
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
}