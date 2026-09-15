= Day 004 - Xoá phần tử khỏi `List`

## Code

`src/main/java/com/thegreatapi/ahundreddaysofjava/day004/Day004.java`

```java
package com.thegreatapi.ahundreddaysofjava.day004;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day004 {

    public static void main(String[] args) {
        List<Person> beatles = new ArrayList<>();
        beatles.add(new Person("1", "John Lennon", LocalDate.of(1940, 10, 9)));
        beatles.add(new Person("2", "Paul McCartney", LocalDate.of(1942, 6, 18)));
        beatles.add(new Person("3", "George Harrison", LocalDate.of(1943, 2, 25)));
        beatles.add(new Person("4", "Ringo Starr", LocalDate.of(1940, 7, 7)));

        removeItemUsingEquals(beatles);
        removeItemUsingAnSpecificFilter(beatles);

        System.out.println(beatles);
    }

    private static void removeItemUsingAnSpecificFilter(List<Person> beatles) {
        beatles.removeIf(person -> "George Harrison".equals(person.getName()));
    }

    private static void removeItemUsingEquals(List<Person> beatles) {
        var lennon = new Person("1", "John Lennon", LocalDate.of(1940, 10, 9));
        beatles.remove(lennon);
    }

    static class Person {
        private final String id;
        private final String name;
        private final LocalDate dateOfBirth;

        Person(String id, String name, LocalDate dateOfBirth) {
            this.id = id;
            this.name = name;
            this.dateOfBirth = dateOfBirth;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var person = (Person) o;
            return Objects.equals(id, person.id) && Objects.equals(name, person.name) && Objects.equals(dateOfBirth, person.dateOfBirth);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, dateOfBirth);
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "'}";
        }
    }
}
```

## Kiến thức chính

- `beatles.remove(lennon)`: xoá phần tử dựa trên `equals()` — vì `Person` đã override `equals()`/`hashCode()`, nên 1 object `Person` **mới tạo** (khác reference) nhưng có cùng `id`/`name`/`dateOfBirth` vẫn được coi là "bằng nhau" và bị xoá đúng phần tử tương ứng trong list.
- `beatles.removeIf(predicate)`: xoá tất cả phần tử thoả điều kiện (lambda), không cần tạo object mẫu để so sánh như `remove()`.

## Q&A: `List` khác `ArrayList` như thế nào?

| | `List` | `ArrayList` |
|---|---|---|
| Là gì | **Interface** — chỉ định nghĩa "hợp đồng" (`add`, `remove`, `get`, `size`...) | **Class** — cài đặt cụ thể của `List`, dùng mảng động bên trong |
| Tạo instance | ❌ Không tạo trực tiếp được (`new List()` → lỗi compile) | ✅ `new ArrayList<>()` |
| Vai trò | Kiểu **khai báo** biến/tham số/kết quả trả về | Kiểu **thực thi thật sự** đứng sau interface |

**Vì sao viết `List<Person> beatles = new ArrayList<>()` mà không viết `ArrayList<Person> beatles = ...`?**
→ Best practice **"lập trình theo interface, không theo implementation"**: khai báo biến kiểu `List` giúp code không phụ thuộc cách `ArrayList` cài đặt bên trong. Muốn đổi sang `LinkedList` (tốt hơn khi insert/remove nhiều ở giữa) hay `CopyOnWriteArrayList` (an toàn đa luồng), chỉ cần sửa **1 chỗ** (`new ArrayList<>()` → `new LinkedList<>()`), phần còn lại của code không cần sửa gì — vì các hàm như `removeItemUsingEquals(List<Person> beatles)` nhận tham số kiểu `List`, dùng được với bất kỳ implementation nào.

## Q&A: Có thể viết `List<Person> beatles1 = [];` không?

**Không.** Đã kiểm chứng bằng `javac` thật, lỗi: `illegal start of expression`. Java **không có cú pháp literal `[]` cho `List`**. Phải tạo qua constructor/factory method:

```java
List<Person> beatles1 = new ArrayList<>();  // rỗng, mutable (thêm/sửa/xoá được)
List<Person> beatles1 = List.of();          // rỗng, immutable
List<Person> beatles1 = List.of(p1, p2);    // có sẵn phần tử, immutable
```

## Q&A: `[]` dùng cho array thì sao, không dùng bao giờ à?

`[]` **có được dùng**, nhưng chỉ ở phần **khai báo kiểu** (array type), không phải cú pháp literal tạo giá trị như JS:

```java
int[] numbers = {1, 2, 3};              // [] là 1 phần của KIỂU ("mảng số int"); {1,2,3} là giá trị khởi tạo
int[] numbers2 = new int[]{1, 2, 3};    // viết đầy đủ, tương đương dòng trên
int[] empty = new int[5];               // mảng rỗng có 5 phần tử (toàn số 0)
```

| | JS/TS | Java |
|---|---|---|
| Khai báo mảng | `const arr: number[] = [1, 2, 3];` | `int[] arr = {1, 2, 3};` |
| Mảng rỗng | `const arr = [];` | `int[] arr = {};` hoặc `new int[0]` |

`List` thì hoàn toàn không dùng `[]` dưới bất kỳ hình thức nào.

## Q&A: Java có "object literal" như JS không?

**Không có.** JS/TS cho tạo object tùy hứng ngay tại chỗ (`const person = { name: "John", age: 30 };`), không cần khai báo class trước. Java **bắt buộc phải có class/record khai báo trước**, rồi mới `new` ra instance:

```java
class Person {
    String name;
    int age;
    Person(String name, int age) { this.name = name; this.age = age; }
}

Person person = new Person("John", 30);
```

## Q&A: `interface` trong Java có giống `interface` trong TS không?

**Khác nhau về ý nghĩa**, dù chung từ khóa — dễ nhầm nhất khi chuyển từ TS sang Java:

| | TS `interface` | Java `interface` |
|---|---|---|
| Mục đích | Mô tả **hình dạng dữ liệu** (structural typing) — object có đủ field đúng kiểu là hợp lệ, không cần khai báo "implements" | Mô tả **hợp đồng hành vi** — tập method mà class `implements` nó phải cài đặt |
| Có field/data không | Có (`interface Person { name: string; age: number }`) | Không (chỉ method signature + hằng số `static final`) |
| Cách dùng | Gán trực tiếp object literal khớp shape | Class phải khai báo `implements InterfaceName` tường minh |

Ví dụ `List` chính là 1 Java interface — không mô tả "dữ liệu có field gì", mà mô tả "có các method `add`, `remove`, `get`..." mà `ArrayList` phải cài đặt.

### Nếu muốn kiểu dữ liệu "gọn nhẹ" gần giống object JS?

Java 16+ có **`record`** — gần nhất với việc định nghĩa nhanh 1 "shape dữ liệu":
```java
record Person(String name, int age) {}
Person person = new Person("John", 30);
```
Ngắn gọn hơn class thường (tự sinh constructor, getter, `equals`, `hashCode`, `toString`) — nhưng vẫn phải khai báo `record Person(...)` trước, không viết `{ name: "John", age: 30 }` trực tiếp như JS được.

## Q&A: Constructor là gì, có phải chỗ nhận giá trị khi `new` không?

```java
Person(String id, String name, LocalDate dateOfBirth) {
    this.id = id;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
}
```

Đúng — đây là **constructor** (hàm khởi tạo). Khi gọi `new Person("1", "John Lennon", LocalDate.of(1940, 10, 9))`, các giá trị truyền vào sẽ khớp với 3 tham số của constructor này.

- Tên constructor **phải trùng tên class**, không có kiểu trả về (khác method thường).
- `this.id = id;` → gán giá trị **tham số** (`id`, vế phải) vào **field của object** (`this.id`, vế trái).

**Vì sao phải viết `this.id = id` chứ không chỉ `id = id`?** Vì tên tham số và tên field trùng nhau (shadowing). `this.id` = field của object đang tạo; `id` (không `this.`) = tham số constructor. Nếu thiếu `this.`, Java hiểu là gán tham số cho chính nó — field sẽ **luôn là `null`**, và Java **không báo lỗi compile** cho việc này — lỗi tinh vi hay gặp khi mới học.

So với TS: gần như y hệt — TS dùng từ khóa `constructor`, cũng dùng `this.` phân biệt field vs tham số (giống `constructor(private foo: Foo)` trong Angular).

## Q&A: 1 class có bắt buộc chỉ 1 constructor không?

**Không bắt buộc** — Java cho phép nhiều constructor trong cùng class, miễn khác chữ ký (số lượng/kiểu tham số) — gọi là **constructor overloading**:

```java
class Person {
    Person(String id, String name, LocalDate dateOfBirth) {
        this.id = id; this.name = name; this.dateOfBirth = dateOfBirth;
    }

    Person(String id, String name) {
        this(id, name, null); // gọi lại constructor kia, tránh lặp code
    }
}
```
Java tự chọn đúng constructor dựa trên số lượng/kiểu tham số truyền vào lúc gọi `new`. TS/JS **không hỗ trợ overload thật sự** — 1 class chỉ có đúng 1 `constructor`; muốn linh hoạt phải dùng tham số optional (`dob?: Date`).

### Thực tế có nên thêm nhiều constructor không?

Hợp lý khi chỉ có **vài cách khởi tạo rõ ràng** (2-3 constructor). Nhưng khi class có **nhiều tham số optional**, nhiều constructor chồng nhau dễ rối — gọi là **Telescoping Constructor** (anti-pattern), ví dụ `new Person("1", "John", null, null, someList)` không rõ `null` ở từng vị trí là gì.

→ Trường hợp đó, Java thường dùng **Builder Pattern** thay thế (sẽ gặp ở `day015` trong repo này):
```java
Person john = Person.builder().name("John").petName("Toto").build();
```
Đọc rõ ràng hơn nhiều, không cần nhớ thứ tự tham số — bù đắp cho việc Java không có "object tham số" tiện như JS/TS (`createPerson({ name, petName })`).

## Q&A: Constructor có chạy khi khởi tạo class không?

Có — constructor chạy **mỗi khi gọi `new`** để tạo 1 **instance (object)** mới, không phải chạy 1 lần duy nhất khi "class được nạp" (đó là khái niệm khác — static initializer, gặp sau).

```java
Person p1 = new Person("1", "John", dob1);   // constructor chạy, tạo object p1
Person p2 = new Person("2", "Paul", dob2);   // constructor chạy thêm lần nữa, tạo object p2 riêng biệt
```
Giống hệt cách `constructor` chạy khi `new SomeClass()` trong TS/Angular.
