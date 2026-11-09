# FUCarRentingSystem Frontend

Frontend React/Vite cho hệ thống quản lý thuê xe MSS301. Tất cả request đi qua API Gateway; không gọi trực tiếp microservice.

## Cài đặt và chạy

Yêu cầu Node.js 18 trở lên.

```bash
npm install
```

Sao chép `.env.example` thành `.env` và chỉnh URL Gateway nếu cần:

```env
VITE_API_BASE_URL=http://127.0.0.1:8088
```

```bash
npm run dev
npm run build
npm run preview
```

## Kiến trúc

- `src/api`: Axios client, interceptor và API module theo domain.
- `src/components`: layout, thành phần dùng chung và thẻ xe.
- `src/context`: trạng thái xác thực toàn ứng dụng.
- `src/pages`: public, customer và admin screens.
- `src/utils`: chuẩn hóa list, ngày, tiền tệ và JWT.

## Tài khoản và phân quyền

Đăng nhập tại `/login`. Backend mẫu khai báo Admin trong cấu hình API Gateway; Customer tạo tài khoản tại `/register`. Token và user được lưu ở `localStorage` khi bật “Ghi nhớ”, ngược lại lưu trong `sessionStorage`. Route ADMIN và CUSTOMER được bảo vệ riêng.

## Routes

Public: `/`, `/login`, `/register`, `/cars`, `/cars/:id`, `/unauthorized`, `/forbidden`.

Admin: `/admin/dashboard`, `/admin/customers`, `/admin/customers/:id`, `/admin/cars`, `/admin/cars/create`, `/admin/cars/:id/edit`, `/admin/reports`.

Customer: `/customer/dashboard`, `/customer/profile`, `/customer/rentings/create`, `/customer/rentings`, `/customer/rentings/:id`.

## API Gateway endpoints

- Auth: `POST /api/auth/login`.
- Customer: `POST /api/customers/register`, `GET /api/customers`, `GET /api/customers/:id`, `GET|PUT /api/customers/profile`, `PUT|DELETE /api/customers/:id`.
- Car: `GET|POST /api/cars`, `GET|PUT|DELETE /api/cars/:id`, `GET /api/cars/available`.
- Reference data: `GET /api/manufacturers`, `GET /api/suppliers`.
- Renting: `POST /api/rentings`, `GET /api/rentings`, `GET /api/rentings/my-history`, `GET /api/rentings/report`.

Endpoint được tập trung trong `src/api`; khi backend thay đổi chỉ cần sửa các module tại đây.
