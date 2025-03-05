package tacos.repositories.implementations;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tacos.models.Ingredient;
import tacos.models.Taco;
import tacos.models.TacoOrder;
import tacos.repositories.OrderRepository;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcOperations jdbcOperations;

    public OrderRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    @Transactional
    public TacoOrder save(TacoOrder order) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "insert into taco_order "
                        + "(delivery_name, delivery_street, delivery_city, "
                        + "delivery_state, delivery_zip, cc_number,"
                        + "cc_expiration, cc_cvv, placed_at) "
                        + "values (?,?,?,?,?,?,?,?,?) ",
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );
        pscf.setReturnGeneratedKeys(true);

        order.setPlacedAt(new Date());
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        order.getDeliveryName(),
                        order.getDeliveryStreet(),
                        order.getDeliveryCity(),
                        order.getDeliveryState(),
                        order.getDeliveryZip(),
                        order.getCcNumber(),
                        order.getCcExpiration(),
                        order.getCcCVV(),
                        order.getPlacedAt()
                )
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long orderId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        order.setId(orderId);

        List<Taco> tacos = order.getTacos();
        int i = 0;
        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }
        return order;
    }

    private long saveTaco(long orderId, int orderKey, Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "insert into taco "
                        + "(name, created_at, taco_order_id, taco_order_key) "
                        + "values (?,?,?,?)",
                Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER
        );
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        taco.getName(),
                        taco.getCreatedAt(),
                        orderId,
                        orderKey
                )
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long tacoId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        taco.setId(tacoId);
        saveIngredientRefs(tacoId, taco.getIngredients());
        return tacoId;
    }

    private void saveIngredientRefs(long tacoId, List<Ingredient> ingredients) {
        int key = 0;
        for (Ingredient ingredient : ingredients) {
            jdbcOperations.update(
                    "insert into ingredient_ref "
                            + "(ingredient_id, taco_id, taco_key) "
                            + "values (?, ?,?)",
                    ingredient.getId(), tacoId, key++
            );
        }
    }
}
