package com.lin.controller;

import com.lin.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public void parse(@PathVariable("keyword") String keyword) throws IOException {
        contentService.parseContent(keyword);
    }

    /**
     * 没有提供高亮
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
//    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
//    public List<Goods> search(@PathVariable String keyword, @PathVariable int pageNo, @PathVariable int pageSize) throws IOException {
//        List<Goods> maps = contentService.searchDataFromEs(keyword, pageNo, pageSize);
//        return maps;
//    }

    @ResponseBody
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> highlightParse(@PathVariable String keyword,
                                                    @PathVariable int pageNo,
                                                    @PathVariable int pageSize) throws IOException {
        return contentService.highlightSearch(keyword,pageNo,pageSize);
    }

}
