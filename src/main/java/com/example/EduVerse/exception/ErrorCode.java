package com.example.EduVerse.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // System Errors (1000 - 1999)
    UNCATEGORIZED(9999, "Lỗi hệ thống không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Mã lỗi (Error Key) không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1001, "Yêu cầu không hợp lệ hoặc dữ liệu bị sai định dạng.", HttpStatus.BAD_REQUEST),

    // Auth & User Errors (2000 - 2999)
    UNAUTHENTICATED(2001, "Xác thực thất bại, vui lòng đăng nhập lại.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Bạn không có quyền thực hiện hành động này.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(2003, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND),
    USER_EXISTED(2004, "Tên người dùng đã tồn tại trên hệ thống.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2005, "Email này đã được sử dụng.", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2007, "Mật khẩu phải có ít nhất {min} ký tự.", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(2008, "Email không được để trống và phải đúng định dạng.", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(2009, "Tên người dùng không hợp lệ hoặc để trống.", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(2010, "Mật khẩu không được để trống.", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(2011, "Mật khẩu cũ không chính xác.", HttpStatus.BAD_REQUEST),
    USER_BANNED(2012, "Tài khoản của bạn đã bị khóa do vi phạm tiêu chuẩn cộng đồng.", HttpStatus.FORBIDDEN),

    // Wallet & Transaction Errors (3000 - 3999)
    WALLET_NOT_FOUND(3001, "Không tìm thấy ví điện tử của người dùng.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE(3002, "Số dư tài khoản không đủ để thực hiện giao dịch này.", HttpStatus.BAD_REQUEST),
    TRANSACTION_FAILED(3003, "Giao dịch tài chính thất bại, vui lòng thử lại.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_TRANSACTION(3004, "Yêu cầu giao dịch bị trùng lặp (Idempotency Triggered).", HttpStatus.CONFLICT),
    WITHDRAWAL_LIMIT_EXCEEDED(3005, "Số tiền rút vượt quá hạn mức tối thiểu hoặc tối đa cho phép.", HttpStatus.BAD_REQUEST),

    // Category & Document Errors (4000 - 4999)
    CATEGORY_NOT_FOUND(4001, "Danh mục môn học không tồn tại.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EMPTY(4002, "Không thể xóa danh mục đang chứa tài liệu bên trong.", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_FOUND(4003, "Tài liệu không tồn tại hoặc đã bị ẩn.", HttpStatus.NOT_FOUND),
    DOCUMENT_NOT_OWNED(4004, "Bạn chưa mua bản quyền để tải tài liệu này.", HttpStatus.FORBIDDEN),
    CANNOT_BUY_OWN_DOCUMENT(4005, "Bạn không thể tự mua tài liệu do chính mình đăng tải.", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(4006, "Lưu trữ file thất bại, vui lòng kiểm tra lại đường truyền.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Chat & Social Errors (5000 - 5999)
    CONVERSATION_NOT_FOUND(5001, "Phòng chat không tồn tại hoặc bạn đã rời nhóm.", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(5002, "Không tìm thấy nội dung tin nhắn.", HttpStatus.NOT_FOUND),
    NOT_FRIENDS(5003, "Hai người phải là bạn bè mới có thể thực hiện hành động này.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_EXISTED(5004, "Lời mời kết bạn giữa hai người đã được gửi trước đó.", HttpStatus.BAD_REQUEST);
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
