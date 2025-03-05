package tacos.repositories;

import tacos.models.TacoOrder;

public interface OrderRepository {
    TacoOrder save (TacoOrder order);
}
