package com.wsj.so.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.Retryer;
import com.wsj.so.common.ErrorCode;
import com.wsj.so.exception.BusinessException;
import com.wsj.so.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class PictureDataSource implements DataSource<Picture> {

    @Resource
    private Retryer<Document> retryer;

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {

        long current = (pageNum - 1) * pageSize;

        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;
        try {
            doc = retryer.call(() -> Jsoup.connect(url).get());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }

        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : elements) {
            //图片地址
            Picture picture = new Picture();
            String m = element.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(m, Map.class);
            String mUrl = (String) map.get("murl");

            //图片标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            picture.setTitle(title);
            picture.setUrl(mUrl);
            pictureList.add(picture);
            if (pictureList.size() >= pageSize) {
                break;
            }
        }
        Page<Picture> page = new Page(pageNum, pageSize);
        page.setRecords(pictureList);
        return page;
    }
}
