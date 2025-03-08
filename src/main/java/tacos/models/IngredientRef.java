package tacos.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ingredients_ref")
public class IngredientRef {
    private final String ingredient;
}
