= Day 003 - Lên lịch chạy 1 task mỗi 2 giây

## Code

`src/main/java/com/thegreatapi/ahundreddaysofjava/day003/Day003.java`

```java
package com.thegreatapi.ahundreddaysofjava.day003;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Day003 {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws InterruptedException {
        var day003 = new Day003();
        day003.printCurrentTimeEvery2Seconds();
        Thread.sleep(15_000);
        day003.stopPrinting();
    }

    public void printCurrentTimeEvery2Seconds() {
        Runnable task = () -> System.out.println(LocalTime.now());
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }

    public void stopPrinting() {
        scheduledExecutorService.shutdown();
    }

}
```

## Kiến thức chính

`ScheduledExecutorService`, `Executors`, `TimeUnit` đều là class/interface **có sẵn trong JDK** (package `java.util.concurrent`) — bộ API xử lý đa luồng (concurrency) chuẩn của Java, không phải thư viện ngoài.

| Thành phần | Vai trò |
|---|---|
| `ScheduledExecutorService` | Interface đại diện cho "thread pool" có khả năng lên lịch chạy task (chạy trễ, hoặc lặp lại theo chu kỳ). |
| `Executors` | Class tiện ích (factory) chứa các static method để tạo executor, ví dụ `newSingleThreadScheduledExecutor()`, `newFixedThreadPool(n)`... |
| `TimeUnit` | Enum đơn vị thời gian (`SECONDS`, `MILLISECONDS`, `MINUTES`...) giúp truyền tham số thời gian rõ ràng, tránh nhầm đơn vị. |

Giải thích luồng code:
- `Executors.newSingleThreadScheduledExecutor()` → tạo 1 "bộ lên lịch" chạy trên **1 thread riêng**.
- `scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS)` → chạy `task` lặp lại: bắt đầu ngay (delay = `0`), cứ mỗi `2` giây chạy lại 1 lần.
- `shutdown()` → dừng lại, giải phóng thread.

## Đối chiếu cú pháp/khái niệm Java vs TypeScript/JS

| Java | Tương đương JS/TS | Ghi chú |
|---|---|---|
| `scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS)` | `setInterval(task, 2000)` | Cùng ý tưởng lặp lại theo chu kỳ, nhưng Java chạy trên **thread nền thật sự (song song thực)**, không chung luồng với code chính như JS. |
| `scheduledExecutorService.shutdown()` | `clearInterval(intervalId)` | Cùng ý tưởng dừng việc lặp lại. |
| `TimeUnit.SECONDS` | truyền số mili-giây thô (`2000`) | Java dùng enum tường minh cho đơn vị thời gian, tránh nhầm giây/mili-giây; JS không có cơ chế tương đương chuẩn. |
| `Executors` (factory tạo executor) | không có tương đương trực tiếp | JS không có khái niệm thread pool vì JS (browser/Node) mặc định **single-threaded**. |

## Điểm khác biệt quan trọng: multithreading thật sự

JS/TS (trình duyệt/Node) chỉ có **1 luồng chính (single-threaded)** — `setInterval`/`setTimeout` chỉ đưa callback vào hàng đợi event loop, chạy xen kẽ với code khác, không có gì chạy "song song" thật. Java tạo **thread riêng biệt, chạy song song thực sự** để xử lý task — đây là khái niệm **true multithreading**, một trong những khác biệt lớn nhất so với JS (JS phải dùng Worker Threads mới có multi-thread thật, và cách dùng khác hẳn).

## Q&A: `Runnable` là kiểu dữ liệu gì?

`Runnable` **không phải kiểu dữ liệu** như `int`/`String` — nó là **interface** có sẵn trong `java.lang` (không cần `import`), thuộc nhóm **functional interface** (interface chỉ có đúng 1 method trừu tượng):

```java
@FunctionalInterface
public interface Runnable {
    void run();
}
```

Nó đại diện cho "một tác vụ không nhận tham số, không trả về giá trị gì".

```java
Runnable task = () -> System.out.println(LocalTime.now());
```

`() -> ...` là **lambda expression** — cú pháp rút gọn tạo 1 implementation của `Runnable` ngay tại chỗ, tương đương cách viết dài (trước Java 8):
```java
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println(LocalTime.now());
    }
};
```

### Đối chiếu TS/JS

| Java | TS/JS | Ghi chú |
|---|---|---|
| `Runnable` | kiểu function `() => void` | Cùng ý nghĩa: hàm không tham số, không trả về gì. |
| `Runnable task = () -> ...;` | `const task: () => void = () => { ... };` | Lambda Java `() -> body` giống arrow function TS `() => body`. |
| `task.run()` | `task()` | Cách gọi thực thi. |

Khác biệt: TS/JS coi function là "công dân hạng nhất", gán trực tiếp vào biến kiểu function được. Java **không có kiểu "function" trần trụi** — lambda luôn phải gán vào 1 functional interface cụ thể (`Runnable`, `Callable`, `Function<T,R>`, `Supplier<T>`...) tùy theo hàm nhận/trả gì.

## Q&A: `task` được gọi ở đâu?

Trong code không có dòng nào gọi `task()`/`task.run()` trực tiếp. `task` chỉ được **truyền vào** `scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS)` như tham số — chính `scheduledExecutorService` (nội bộ JDK) mới là bên **thực sự gọi `task.run()`**, lặp lại mỗi 2 giây trên thread nền riêng.

Luồng chạy:
1. `Runnable task = () -> ...;` → chỉ **định nghĩa** task, chưa chạy gì.
2. `scheduleAtFixedRate(task, ...)` → **đăng ký** task với executor kèm lịch trình.
3. Bên trong executor có 1 timer nội bộ (chạy trên thread riêng) — đến đúng thời điểm, nó tự gọi `task.run()` giúp bạn.

Đây gọi là **callback pattern**: truyền 1 hàm (chưa chạy) cho cơ chế khác, để cơ chế đó tự quyết định *khi nào* gọi lại. Giống hệt cách `setInterval` hoạt động trong JS:

```typescript
const task = () => console.log(new Date());
setInterval(task, 2000);
// không tự gọi task(), mà truyền cho setInterval,
// và runtime tự gọi task() mỗi 2000ms.
```
