package com.lin.pojo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

//注意indexName要小写
@Document(indexName = "jd_goods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Goods {
    @Id
    private String id;
    private String title;
    private String price;
    private String imgUrl;
}
