package hello.itemservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity // 테이블과 같이 맵핑돼서 관리되는 객체
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 값 증가
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    //jpa는 public or protected 기본 생성자가 필수임(프록시 관련 기술을 쓸 수 있으므로)
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
