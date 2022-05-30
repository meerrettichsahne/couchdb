package de.marcorohn.seminar.relational;

import de.marcorohn.seminar.relational.dto.CustomerDto;
import de.marcorohn.seminar.relational.dto.ItemDto;
import de.marcorohn.seminar.relational.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/r/")
public class RController {

    @Autowired
    private RRepository repo;


    @GetMapping("/customer/{username}")
    public ResponseEntity<CustomerDto> getCustomerByUsername(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CustomerDto dto = CustomerDto.wrap(customer);
        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PostMapping("/customer")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto body) {
        if (body == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        RCustomer customer = repo.findCustomerByUsername(body.username);
        if (customer != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        customer = repo.createCustomer(body.username, body.name);
        CustomerDto dto = CustomerDto.wrap(customer);
        return ResponseEntity.ok(dto);
    }

    @Transactional
    @DeleteMapping("/customer/{username}")
    public ResponseEntity<CustomerDto> deleteCustomer(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CustomerDto dto = CustomerDto.wrap(customer);
        repo.deleteCustomer(customer);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer/{username}/basket")
    public ResponseEntity<List<ItemDto>> getUserBasket(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ItemDto> dtos = customer.getBasketItems().stream().map(ItemDto::wrap).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/customer/{username}/basket-sum")
    public ResponseEntity<Double> getUserBasketSum(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        double sum = repo.getCustomerBasketSum(username);
        return ResponseEntity.ok(sum);
    }

    @Transactional
    @PostMapping("/customer/{username}/basket")
    public ResponseEntity<List<ItemDto>> addToBasket(@PathVariable(name = "username") String username, @RequestBody long id) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RItem itemToAdd = repo.findItem(id);
        if (itemToAdd == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        customer.getBasketItems().add(itemToAdd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/customer/{username}/basket")
    public ResponseEntity<List<ItemDto>> removeFromBasket(@PathVariable(name = "username") String username, @RequestBody long id) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RItem itemToRemove = repo.findItem(id);
        if (itemToRemove == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!customer.getBasketItems().contains(itemToRemove)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        customer.getBasketItems().remove(itemToRemove);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/customer/{username}/checkout")
    public ResponseEntity<OrderDto> checkoutBasket(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ROrder order = repo.createOrder(new Date(System.currentTimeMillis()), customer,
                customer.getBasketItems().stream().toList());

        customer.getBasketItems().clear();
        OrderDto dto = OrderDto.wrap(order);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer/{username}/orders")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable(name = "username") String username) {
        RCustomer customer = repo.findCustomerByUsername(username);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<OrderDto> dtos = customer.getOrders().stream().map(OrderDto::wrap).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getItems() {
        List<RItem> items = repo.findAllItems();
        List<ItemDto> dtos = items.stream().map(ItemDto::wrap).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/items/{cat}")
    public ResponseEntity<List<ItemDto>> getItems(@PathVariable(name = "cat") String category) {
        List<RItem> items = repo.findAllItemsInCategory(category);
        List<ItemDto> dtos = items.stream().map(ItemDto::wrap).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Transactional
    @PostMapping("/items")
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto body) {
        RItem item = repo.createItem(body.name, body.description, body.category, body.price);
        return ResponseEntity.ok(ItemDto.wrap(item));
    }

    @Transactional
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ItemDto> deleteItem(@PathVariable long id) {
        RItem item = repo.findItem(id);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ItemDto dto = ItemDto.wrap(item);
        repo.deleteItem(item);
        return ResponseEntity.ok(dto);
    }
}
