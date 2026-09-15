= Day 001 - Sinh số ngẫu nhiên trong khoảng cho trước

## Code

`src/test/java/com/thegreatapi/ahundreddaysofjava/day001/Day001Test.java`

```java
package com.thegreatapi.ahundreddaysofjava.day001;

import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import static org.assertj.core.api.Assertions.assertThat;

class Day001Test {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static int generateRandomNumberBetween(int minimum, int maximum) {
        return SECURE_RANDOM.nextInt(maximum - minimum) + minimum;
    }

    @Test
    void randomNumberBetween() {
        var minimum = 100;
        var maximum = 1000;
        int randomNumber = generateRandomNumberBetween(minimum, maximum);
        assertThat(randomNumber).isBetween(minimum, maximum);
    }
}
```

## Kiến thức chính

Dùng `SecureRandom` (thay vì `Random` thường) để sinh số ngẫu nhiên — an toàn hơn về mặt bảo mật, phù hợp khi cần độ ngẫu nhiên khó đoán (token, OTP, session id...). Test chỉ kiểm tra số sinh ra luôn nằm trong khoảng `[minimum, maximum)`.

## Đối chiếu cú pháp Java vs TypeScript/Angular

| Java | Tương đương TS/Angular | Ghi chú |
|---|---|---|
| `package com.thegreatapi...` | cấu trúc thư mục module | Java **bắt buộc** khai báo package khớp với đường dẫn thư mục thật (`src/test/java/com/thegreatapi/...`). TS không ràng buộc chặt như vậy. |
| `import ...` | `import` trong TS | Giống hệt về ý nghĩa. |
| `import static ...assertThat` | không có tương đương trực tiếp | "static import" cho phép gọi `assertThat(...)` thay vì phải viết `Assertions.assertThat(...)`. |
| `class Day001Test { }` | `class` trong TS | Giống nhau về khái niệm. |
| `public static final SECURE_RANDOM` | gần giống `static readonly` trong TS class | `static` = thuộc về class (không cần tạo instance); `final` = không được gán lại (giống `readonly`/hằng số). |
| `private static int generateRandomNumberBetween(int minimum, int maximum)` | `private static generateRandomNumberBetween(minimum: number, maximum: number): number` | Java khai báo **kiểu trước tên biến/tham số** (`int minimum`), TS thì ngược lại (`minimum: number`). `int` ở đầu hàm là kiểu trả về. |
| `var minimum = 100;` | `const minimum = 100;` | Java suy luận kiểu (type inference) giống TS, nhưng `var` ở Java **không phải biến động** — kiểu vẫn cố định (ở đây là `int`) ngay từ dòng khai báo, chỉ là không cần gõ tường minh. |
| `@Test` | Decorator (`@Component`, `@Injectable`...) | Annotation trong Java hoạt động khá giống decorator: "đánh dấu" cho JUnit biết đây là 1 test case cần chạy. |
| `assertThat(x).isBetween(a, b)` | `expect(x).toBeGreaterThanOrEqual(a)` (Jest/Jasmine) | Cùng ý tưởng fluent assertion trong test, chỉ khác thư viện (đây là AssertJ). |

## Q&A: Kiểu trả về (return type) khai báo ở đâu?

Java viết kiểu trả về **trước** tên hàm, khác thứ tự với TS:

```java
private static int generateRandomNumberBetween(int minimum, int maximum) { ... }
//              ^^^ kiểu trả về ở đây
```

```typescript
function generateRandomNumberBetween(minimum: number, maximum: number): number { ... }
//                                                                     ^^^^^^^^ kiểu trả về ở đây
```

**Nhỡ hàm trả sai kiểu đã khai báo thì sao?** → Không có rủi ro runtime: Java kiểm tra điều này lúc **compile-time**. Nếu method khai báo trả `int` mà `return` một giá trị không phải `int` (và không tự động widen được), code sẽ **không biên dịch được** — báo lỗi kiểu `incompatible types`. Tương tự, khi gán `int randomNumber = generateRandomNumberBetween(...)`, compiler cũng kiểm tra kiểu trả về của hàm có gán được cho biến hay không. Đây là khác biệt lớn so với JS thuần (không có type ở runtime) và ngay cả TS nếu dùng `any`/tắt `strict` — Java luôn ép kiểm tra kiểu, không có cách tắt.

## Cách log (in ra) trong Java

Tương đương `console.log` trong JS/TS, Java có vài cách, từ đơn giản đến "chuẩn":

| Cách | Ví dụ | Khi nào dùng |
|---|---|---|
| `System.out.println(x)` | `System.out.println(result);` | In ra console + tự xuống dòng. Tương đương `console.log(x)`. Dùng để debug nhanh, học/thử nghiệm — **không dùng trong code thật (production)**. |
| `System.out.print(x)` | `System.out.print(result);` | Giống trên nhưng **không** tự xuống dòng. |
| `System.out.printf(...)` | `System.out.printf("Value: %d%n", result);` | In có định dạng (format string), giống `String.format` trong JS/`sprintf`. `%d` = số nguyên, `%s` = chuỗi, `%n` = xuống dòng. |
| `System.err.println(x)` | `System.err.println("Error!");` | Giống `println` nhưng in ra luồng lỗi (stderr) thay vì luồng thường (stdout) — tương đương `console.error`. |

### Vì sao không nên dùng `println` cho code thật?

Trong ứng dụng thực tế, người ta dùng **logging framework** thay vì `println`, vì logger hỗ trợ:
- **Log level** (mức độ quan trọng): `DEBUG`, `INFO`, `WARNING`, `SEVERE`/`ERROR`... → có thể bật/tắt từng loại mà không sửa code (giống cách một số logger JS như `pino`/`winston` cho phép set level).
- Tự thêm timestamp, tên class, tên thread vào mỗi dòng log.
- Ghi ra file, gửi tới hệ thống giám sát... thay vì chỉ hiện trên console.

Ví dụ dùng logger chuẩn của JDK (`java.util.logging`, sẽ gặp ở các bài sau như Day006, Day008 trong repo này):

```java
import java.util.logging.Logger;

public class Example {
    private static final Logger LOGGER = Logger.getLogger(Example.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Giá trị: " + 42);   // mức INFO
        LOGGER.warning("Cẩn thận!");      // mức WARNING
    }
}
```

→ Với bài `day001` hiện tại (chỉ đang học/thử nghiệm), dùng `System.out.println(result)` như bạn đã làm là hoàn toàn hợp lý — chưa cần logger phức tạp.
