package vix.local.api.modules.capital_source.domain.model;

/**
 * Định nghĩa 4 nhóm danh mục BẮT BUỘC, CỐ ĐỊNH của hệ thống.
 * Đây KHÔNG phải "type" người dùng tự tạo.
 * Người dùng chỉ quản lý các mục con bên trong mỗi nhóm.
 */
public enum CategoryGroup {

    /**
     * Nhóm ngân hàng — VD: Vietcombank, BIDV, Techcombank...
     */
    BANK,

    /**
     * Nhóm loại hạn mức — VD: clean, có tài sản bảo đảm, margin, TPCP, thấu chi, khác
     */
    LIMIT_TYPE,

    /**
     * Nhóm loại tài sản — VD: HĐTG, CD, TPCP, TPRL, TPNY, cổ phiếu, tiền phong tỏa, khác
     */
    ASSET_TYPE,

    /**
     * Nhóm mục đích vay — VD: mua TPCP, margin bổ sung vốn, thanh toán giao dịch, khác
     */
    LOAN_PURPOSE
}
