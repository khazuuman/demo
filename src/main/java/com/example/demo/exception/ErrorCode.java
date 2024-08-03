package com.example.demo.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED("Unauthenticated!", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD("Mật khẩu không chính xác!", HttpStatus.BAD_REQUEST),
    DEFAULT_ERROR("Lỗi không xác định", HttpStatus.BAD_REQUEST),
    USERNAME_BLANK("Tài khoản không được để trống!", HttpStatus.BAD_REQUEST),
    PASSWORD_BLANK("Mật khẩu không được để trống!", HttpStatus.BAD_REQUEST),
    NOT_FULL_INFO("Không đủ thông tin!", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND("Không tìm thấy tài khoản!", HttpStatus.NOT_FOUND),
    TOKEN_NOT_EXIST("Token không tồn tại!", HttpStatus.BAD_REQUEST),
    TOKEN_OUT_DATE("Token đã hết hạn!", HttpStatus.BAD_REQUEST),
    TOKEN_SIGN_OUTED("Tài khoản đã đăng xuất!", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_VALID("Token không hợp lệ!", HttpStatus.BAD_REQUEST),
    AREA_EXISTED("Tên khu vực đã tồn tại!", HttpStatus.BAD_REQUEST),
    AREA_BLANK("Tên khu vực không được để trống!", HttpStatus.BAD_REQUEST),
    AREA_NOT_EXISTED("Khu vực không tồn tại!", HttpStatus.BAD_REQUEST),
    FILE_BLANK("Ảnh không được để trống!", HttpStatus.BAD_REQUEST),
    BANNER_NOT_EXIST("Không tìm thấy banner!", HttpStatus.NOT_FOUND),
    MANAGER_BLANK("Tên người quản lý không được để trống!", HttpStatus.BAD_REQUEST),
    PROVINCE_BLANK("Tên thành phố chi nhánh không được để trống!", HttpStatus.BAD_REQUEST),
    PHONE_BLANK("Số điện thoại không được để trống!", HttpStatus.BAD_REQUEST),
    PHONE_INVALID("Số điện thoại phải là 1 số nguyên dương và bao gồm 10 chữ số!", HttpStatus.BAD_REQUEST),
    BRANCH_NOT_EXIST("Chi nhánh không tồn tại!", HttpStatus.NOT_FOUND),
    CATEGORY_BLANK("Tên danh mục không được để trống!", HttpStatus.BAD_REQUEST),
    CATE_NAME_EXISTED("Tên danh mục đã tồn tại!", HttpStatus.FOUND),
    CATE_NOT_EXIST("Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("Không tìm thấy sản phẩm!", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTED("Sản phẩm đã tồn tại!", HttpStatus.FOUND),
    PRODUCT_NAME_BLANK("Tên sản phẩm không được để trống!", HttpStatus.BAD_REQUEST),
    MANUFACTURE_NAME_BLANK("Nguồn gốc không được để trống!", HttpStatus.BAD_REQUEST),
    INGREDIENT_NAME_BLANK("Thành phần không được để trống!",HttpStatus.BAD_REQUEST),
    EXPIRY_NAME_BLANK("Hạn sử dụng không được để trống!", HttpStatus.BAD_REQUEST),
    PRICE_INVALID("Giá tiền phải là 1 số không âm!", HttpStatus.BAD_REQUEST),
    QUANTITY_INVALID("Số lượng phải là 1 số không âm!", HttpStatus.BAD_REQUEST),
    STATUS_BLANK("Trạng thái không được để trống!", HttpStatus.BAD_REQUEST),
    STATUS_INVALID("Chỉ có 2 trạng thái là 'Hiển thị' và 'Ẩn'", HttpStatus.BAD_REQUEST),
    IMAGES_BLANK("Ảnh không được để trống!", HttpStatus.BAD_REQUEST),
    IMAGES_EXCEED("Tối đa 5 ảnh", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXIST("Không tìm thấy đơn hàng", HttpStatus.BAD_REQUEST),
    AVAILABLE_QUANTITY("Số lượng sau khi cập nhật không thể nhỏ hơn hoặc bằng 0!", HttpStatus.BAD_REQUEST),
    ORDER_NAME_BLANK("Tên không được để trống!", HttpStatus.BAD_REQUEST),
    ORDER_ADDRESS_BLANK("Địa chỉ được để trống!", HttpStatus.BAD_REQUEST),
    ORDER_ITEM_BLANK("Sản phẩm không được để trống!", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL("Không thể hủy đơn sau khi đã nhận hàng!", HttpStatus.BAD_REQUEST),
    OLD_STATUS("Không thể cập nhật lại các trạng thái cũ!", HttpStatus.BAD_REQUEST),
    OLD_PASS_INCORRECT("Mật khẩu cũ không chính xác!", HttpStatus.BAD_REQUEST);


    String message;
    HttpStatusCode httpStatusCode;

}
