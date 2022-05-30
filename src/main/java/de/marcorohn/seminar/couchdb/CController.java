package de.marcorohn.seminar.couchdb;

import de.marcorohn.seminar.relational.RCustomer;
import de.marcorohn.seminar.relational.dto.ItemDto;
import de.marcorohn.seminar.relational.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/c/")
public class CController {

    @Autowired
    private CCustomerRepository customerRepo;

    @Autowired
    private CItemRepository itemRepo;

    @Autowired
    private COrderRepository orderRepo;

    @GetMapping("/customer/{username}")
    public ResponseEntity<CCustomer> getCustomerByUsername(@PathVariable(name = "username") String username) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/customer")
    public ResponseEntity<CCustomer> createCustomer(@RequestBody CCustomer body) {
        if (body == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CCustomer customer = customerRepo.findByUsername(body.getUsername());
        if (customer != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        customer = new CCustomer();
        customer.setName(body.getName());
        customer.setUsername(body.getUsername());
        customerRepo.add(customer);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/customer/{username}")
    public ResponseEntity<CCustomer> deleteCustomer(@PathVariable(name = "username") String username) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        customerRepo.remove(customer);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customer/{username}/basket")
    public ResponseEntity<List<CItem>> getUserBasket(@PathVariable(name = "username") String username) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Die einfache Variante
        // List<CItem> items = customer.getBasketItems();

        // Die kompliziertere Variante mit view
        List<CItem> items = customerRepo.getCustomerBasket(username);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/customer/{username}/basket-sum")
    public ResponseEntity<Double> getUserBasketSum(@PathVariable(name = "username") String username) {

        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        double sum = customerRepo.getCustomerBasketSum(username);
        return ResponseEntity.ok(sum);
    }

    @PostMapping("/customer/{username}/basket")
    public ResponseEntity<List<ItemDto>> addToBasket(@PathVariable(name = "username") String username, @RequestBody String id) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // As body is json, the string needs to be a correct json string, but we dont like the ""
        id = id.replace("\"", "");
        System.out.println(id);

        if (!itemRepo.contains(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CItem itemToAdd = itemRepo.get(id);

        customer.getBasketItems().add(itemToAdd);
        customerRepo.update(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/customer/{username}/basket")
    public ResponseEntity<List<ItemDto>> removeFromBasket(@PathVariable(name = "username") String username, @RequestBody String id) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        id = id.replace("\"", "");

        if (!itemRepo.contains(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CItem itemToRemove = itemRepo.get(id);

        if (!customer.getBasketItems().contains(itemToRemove)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        customer.getBasketItems().remove(itemToRemove);
        customerRepo.update(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/customer/{username}/checkout")
    public ResponseEntity<COrder> checkoutBasket(@PathVariable(name = "username") String username) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        COrder order = new COrder();
        order.setSubmitted(System.currentTimeMillis());
        order.setItems(customer.getBasketItems().stream().toList());
        customer.getBasketItems().clear();
        orderRepo.add(order);
        customerRepo.update(customer);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{username}/orders")
    public ResponseEntity<List<COrder>> getUserOrders(@PathVariable(name = "username") String username) {
        CCustomer customer = customerRepo.findByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<COrder> orders = customer.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/items")
    public ResponseEntity<List<CItem>> getItems() {
        List<CItem> items = itemRepo.getAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{cat}")
    public ResponseEntity<List<CItem>> getItems(@PathVariable(name = "cat") String category) {
        List<CItem> items = itemRepo.findByCategory(category);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<CItem> createItem(@RequestBody CItem body) {
        CItem item = new CItem();
        item.setType("item");
        item.setId(body.getId());
        item.setName(body.getName());
        item.setCategory(body.getCategory());
        item.setDescription(body.getDescription());
        item.setPrice(body.getPrice());
        itemRepo.add(item);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CItem> deleteItem(@PathVariable String id) {
        if (!itemRepo.contains(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CItem item = itemRepo.get(id);
        itemRepo.remove(item);
        return ResponseEntity.ok(item);
    }
}
