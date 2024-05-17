package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository //@Repository 기능 : 1. 컴포넌트 스캔 대상 2. 예외변환 aop 적용 대상
@Transactional //jpa에서 데이터 변경 시 Transactional을 무조건 넣어줘야함(이번 예제는 단순해서 리포지토리에 트랜잭션을 걸었지만 대부분 비즈니스 로직이 시작되는 서비스 계층에서 걸어둠)
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em; //이게 jpa?!

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //update query 자동으로 나감(트랜잭션이 커밋되는 시점에 변경된 데이터들을 update하는 쿼리를 자동으로 날려줌)
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id); // 1건 조회
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        //jpql - 객체지향 쿼리 언어로 엔티티 객체를 대상으로 sql 실행
        String jpql = "select i from Item i"; // Item - Item entity를 말하는거임. i - alias

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        return query.getResultList();
    }
}
