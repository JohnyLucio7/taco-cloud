package tacos.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.TacoOrder;
import tacos.TacoUDT;
import tacos.Taco;
import tacos.data.IngredientRepository;

@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {

	private final IngredientRepository ingredientRepository;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepository) {
		this.ingredientRepository = ingredientRepository;
	}

	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepository.findAll().forEach(i -> ingredients.add(i));

		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}
	}

	@ModelAttribute(name = "tacoOrder")
	public TacoOrder order() {
		return new TacoOrder();
	}

	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}

	@GetMapping
	public String showDesignForm() {
		return "design";
	}

	@PostMapping
	public String processTaco(@Valid Taco taco, Errors errors, @ModelAttribute TacoOrder tacoOrder) {

		if (errors.hasErrors()) {
			errors.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
			return "design";
		}

		tacoOrder.addTaco(new TacoUDT(taco.getName(), taco.getIngredients()));

		return "redirect:/orders/current";
	}

	private Iterable<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		return ingredients.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
	}

}