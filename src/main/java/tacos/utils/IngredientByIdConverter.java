package tacos.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;
import tacos.models.Ingredient;
import tacos.repositories.IngredientRepository;

@Controller
public class IngredientByIdConverter implements Converter<String, Ingredient> {
    private final IngredientRepository ingredientRepository;

    public IngredientByIdConverter(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }


    @Override
    public Ingredient convert(String id) {
        return ingredientRepository.findById(id).orElse(null);
    }
}
