-- =========================================================================
-- SCRIPT MIGRATION: ĐƯA TRƯỞNG PHÒNG VỀ 1 BẢNG DUY NHẤT (user_departments) - nếu vẫn dùng db cũ thì chạy lệnh không thì thôi
-- =========================================================================

-- 1. Xóa cột manager_id khỏi bảng departments
ALTER TABLE shared.departments DROP COLUMN IF EXISTS manager_id;

-- 2. Tạo Partial Unique Index để đảm bảo 1 phòng ban tại 1 thời điểm CHỈ có duy nhất 1 DEPT_ADMIN (Trưởng phòng)
CREATE UNIQUE INDEX IF NOT EXISTS uq_one_dept_admin
  ON shared.user_departments (department_id)
  WHERE role = 'DEPT_ADMIN';
