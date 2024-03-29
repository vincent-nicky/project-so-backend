package com.wsj.so.datasource;

import com.wsj.so.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Component
public class DataSourceRegistry {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private VideoDataSource videoDataSource;

    private Map<String,DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void init(){
        typeDataSourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);
            put(SearchTypeEnum.VIDEO.getValue(),videoDataSource);
        }};
    }

    public DataSource getDataSource(String type){
        if(type==null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }

}
