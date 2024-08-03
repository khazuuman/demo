package com.example.demo.dto.response;

import com.example.demo.model.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BannerResponse {

    String _id;
    String url;
    Image image;
    Date created_at;
    Date updated_at;

}
