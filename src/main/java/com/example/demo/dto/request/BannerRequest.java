package com.example.demo.dto.request;

import com.example.demo.model.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BannerRequest {

    String url;
    Image image;

}
