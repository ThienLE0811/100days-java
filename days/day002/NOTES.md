= Day 002 - Định dạng `LocalDateTime`

## Code

`src/test/java/com/thegreatapi/ahundreddaysofjava/day002/Day002Test.java`

```java
package com.thegreatapi.ahundreddaysofjava.day002;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.assertj.core.api.Assertions.assertThat;

class Day002Test {

    @Test
    void formatDate() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime currentDateTime = LocalDateTime.of(2020, 1, 31, 20, 35, 45);

        assertThat(currentDateTime.format(FORMATTER))
                .isEqualTo("31/01/2020 20:35:45");
    }
}
```

## Kiến thức chính

`LocalDateTime` và `DateTimeFormatter` là class **có sẵn trong JDK** (package `java.time`), không phải thư viện ngoài — đây là bộ API chuẩn xử lý ngày-giờ của Java từ JDK 8.

- `LocalDateTime`: biểu diễn ngày + giờ **không có múi giờ** (local). Java còn có `LocalDate` (chỉ ngày), `LocalTime` (chỉ giờ), `ZonedDateTime` (có múi giờ) cho các nhu cầu khác.
- `DateTimeFormatter.ofPattern(...)`: định nghĩa khuôn mẫu hiển thị — `dd`=ngày, `MM`=tháng, `yyyy`=năm, `HH:mm:ss`=giờ:phút:giây.
- `LocalDateTime.of(2020, 1, 31, 20, 35, 45)`: tạo 1 thời điểm cụ thể (cố định, không dùng `.now()`) để test luôn cho ra kết quả giống nhau, không phụ thuộc thời điểm chạy.
- `.format(FORMATTER)`: chuyển `LocalDateTime` → `String` theo pattern đã định nghĩa.

## Đối chiếu cú pháp Java vs TypeScript/JS

| Java | TS/JS | Ghi chú |
|---|---|---|
| `LocalDateTime` | Không có type tách biệt sẵn; thường dùng `Date`, hoặc thư viện `date-fns`/`dayjs`/`luxon` | Java tách riêng `LocalDate`/`LocalTime`/`LocalDateTime`/`ZonedDateTime` theo nhu cầu, JS chỉ có 1 `Date` chung xử lý mọi trường hợp. |
| `DateTimeFormatter` | `Intl.DateTimeFormat`, hoặc `.format()` của dayjs/date-fns | Định dạng ngày-giờ theo pattern tùy chỉnh. |
| `LocalDateTime.of(2020, 1, 31, 20, 35, 45)` | `new Date(2020, 0, 31, 20, 35, 45)` | **Khác biệt quan trọng**: Java đánh số tháng **từ 1** (tháng 1 = `1`), còn JS đánh số tháng **từ 0** (tháng 1 = `0`) — lỗi rất hay gặp khi lập trình JS/TS, Java trực quan hơn ở điểm này. |

## Q&A: `Instant` có `.format()` không?

**Không.** `Instant` **không có** method `.format(DateTimeFormatter)` — thử gọi `Instant.now().format(FORMATTER)` sẽ báo lỗi biên dịch `cannot find symbol: method format(DateTimeFormatter)` (đã kiểm chứng bằng `javac` thật).

Lý do: `Instant` chỉ là 1 mốc thời gian tuyệt đối tính bằng giây/nano-giây từ epoch (`1970-01-01T00:00:00Z`) — nó **không có khái niệm ngày/tháng/năm/giờ theo múi giờ**, nên không thể tự format theo pattern như `"dd/MM/yyyy HH:mm:ss"`. Chỉ những lớp có đủ thông tin lịch (`LocalDateTime`, `LocalDate`, `ZonedDateTime`...) mới có sẵn `.format(...)`.

**Cách sửa (đã áp dụng trong code)** — chuyển `Instant` sang múi giờ cụ thể trước bằng `.atZone(...)`, được `ZonedDateTime` (có đủ thông tin lịch) rồi mới `.format(...)`:

```java
ZonedDateTime zonedTime = Instant.now().atZone(ZoneId.systemDefault());
String instantTime = zonedTime.format(FORMATTER);
```

Cách khác: gán zone thẳng vào formatter rồi gọi ngược `formatter.format(instant)` (thay vì `instant.format(formatter)`):
```java
DateTimeFormatter formatterWithZone = FORMATTER.withZone(ZoneId.systemDefault());
String instantTime = formatterWithZone.format(Instant.now());
```

So sánh TS/JS: giống việc có 1 timestamp thuần (`Date.now()` — số mili-giây từ epoch) và muốn hiển thị "ngày/giờ đẹp" — phải `new Date(timestamp)` trước rồi mới `.toLocaleString()` được, không thể format trực tiếp trên con số timestamp thô.
