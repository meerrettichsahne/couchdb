import {NextFunction, Request, Response} from 'express';
import Nano from 'nano';
import {Customer} from "@models/customer";
import {Item} from "@models/item";
import {Order} from "@models/order";

class IndexController {
  public nano = Nano('https://admin:password@localhost');
  public db = this.nano.db.use('onlineshop');

  public constructor() {
    this.nano.info().then(info => console.log(info))
    this.db.info().then(info => console.log(info))
  }

  public getCustomerByUsername = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      if (!req.params.username) {
        res.sendStatus(400);
      }
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }
      res.status(200).json(customer);
    } catch (error) {
      next(error);
    }
  };

  public createCustomer = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const existing = await this.getCustomerByUsernameFromDb(req.body.username);
      if (existing) {
        res.sendStatus(409);
      }

      const customer = new Customer();
      customer.name = req.body.name;
      customer.username = req.body.username;
      const dbRes = await this.db.insert(customer);
      if (dbRes.ok) {
        customer._id = dbRes.id;
        customer._rev = dbRes.rev;
        res.status(200).json(customer);
      } else {
        res.status(500);
      }
    } catch (error) {
      next(error);
    }
  };

  public deleteCustomer = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      if (!req.params.username) {
        res.sendStatus(400);
      }
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }

      const dbRes = await this.db.destroy(customer._id, customer._rev);
      if (dbRes.ok) {
        customer._rev = dbRes.rev;
      }
      res.status(200).json(customer);
    } catch (error) {
      next(error);
    }
  };

  public getUserBasket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }
      const basketItems = await this.getCustomerBasketItemsFromDb(req.params.username);
      res.status(200).json(basketItems);
    } catch (error) {
      next(error);
    }
  };

  public getUserBasketSum = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }
      const sum = await this.getCustomerBasketSumFromDb(req.params.username);
      res.status(200).json(sum);
    } catch (error) {
      next(error);
    }
  };

  public addToBasket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }

      const id = req.body[0];
      const item = await this.getItemFromDb(id);

      if (!item) {
        res.sendStatus(404);
      }

      customer.basketItems.push(item);
      const dbRes = await this.db.insert(customer);

      if (dbRes.ok) {
        res.status(200).json(customer);
      } else {
        res.status(500).json(dbRes);
      }
    } catch (error) {
      next(error);
    }
  };

  public removeFromBasket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }

      const id = req.body[0];
      const itemToDelete = await this.getItemFromDb(id);

      if (!itemToDelete) {
        res.sendStatus(404);
      }

      const index = customer.basketItems.findIndex(item => item._id === itemToDelete._id && item._rev === itemToDelete._rev);
      if (index < 0) {
        res.sendStatus(404);
      }

      customer.basketItems.splice(index, 1);
      const dbRes = await this.db.insert(customer);

      if (dbRes.ok) {
        res.status(200).json(customer);
      } else {
        res.status(500).json(dbRes);
      }
      res.status(200).json({  });
    } catch (error) {
      next(error);
    }
  };

  public checkoutBasket = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }

      const order = new Order();
      order.items = customer.basketItems;
      order.submitted = Date.now();
      customer.basketItems = [];
      customer.orders.push(order);
      const dbRes = await this.db.insert(customer);
      if (!dbRes.ok) {
        res.sendStatus(500);
      }

      res.status(200).json(order);
    } catch (error) {
      next(error);
    }
  };

  public getUserOrders= async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const customer = await this.getCustomerByUsernameFromDb(req.params.username);
      if (!customer) {
        res.sendStatus(404);
      }

      res.status(200).json(customer.orders);
    } catch (error) {
      next(error);
    }
  };

  public getItems = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const items = await this.getAllItemsFromDb();
      res.status(200).json(items);
    } catch (error) {
      next(error);
    }
  };

  public getItemsByCategory = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const items = await this.getAllItemsOfCategoryFromDb(req.params.category)
      res.status(200).json(items);
    } catch (error) {
      next(error);
    }
  };

  public createItem = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const item = new Item();
      item.name = req.body.name;
      item.description = req.body.description;
      item.category = req.body.category;
      item.price = req.body.price;
      const dbRes = await this.db.insert(item);
      if (!dbRes.ok) {
        res.sendStatus(500);
      }
      item._id = dbRes.id;
      item._rev = dbRes.rev;
      res.status(200).json(item);
    } catch (error) {
      next(error);
    }
  };

  public deleteItem = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const itemToDelete = await this.getItemFromDb(req.params.id);
      if (!itemToDelete) {
        res.sendStatus(404);
      }
      const dbRes = await this.db.destroy(itemToDelete._id, itemToDelete._rev);
      if (!dbRes.ok) {
        res.sendStatus(500)
      }

      const customersHavingThisItemInBasket = await this.db.view('items', 'customers-having-item-in-basket',
        {group: true});
      const customerKeys = customersHavingThisItemInBasket.rows.map(row => row.key);
      for (let customerKey of customerKeys) {
        const customer = await this.db.get(customerKey) as Customer;
        do {
          const idx = customer.basketItems.findIndex(item => item._id === itemToDelete._id);
          if (idx >= 0) {
            customer.basketItems.splice(idx, 1);
          }
        } while (customer.basketItems.findIndex(item => item._id === itemToDelete._id) >= 0);
        const dbRes = await this.db.insert(customer)
        if (!dbRes.ok) {
          res.sendStatus(500);
        }
      }
      res.status(200).json(itemToDelete);
    } catch (error) {
      next(error);
    }
  };

  private async getCustomerByUsernameFromDb(username: string): Promise<Customer> {
    const rl = await this.db.view('customer', 'by-username',
      {key: username, include_docs: true});
    return rl.rows.length === 0 ? null : rl.rows[0].doc as Customer;
  }

  private async getCustomerBasketItemsFromDb(username: string): Promise<Item[]> {
    const rl = await this.db.view('customer', 'customerBasket',
      {key: username});
    return rl.rows.map(row => row.value as Item);
  }

  private async getCustomerBasketSumFromDb(username: string): Promise<number> {
    const rl = await this.db.view('customer', 'customerBasketSum',
      {key: username});
    return rl.rows.length === 0 ? 0 : rl.rows[0].value as number;
  }

  private async getItemFromDb(id: string): Promise<Item> {
    return await this.db.get(id) as Item;
  }

  private async getAllItemsFromDb(): Promise<Item[]> {
    const rl = await this.db.view('items', 'all',
      {include_docs: true});
    return rl.rows.map(row => row.doc as Item);
  }

  private async getAllItemsOfCategoryFromDb(category: string): Promise<Item[]> {
    const rl = await this.db.view('items', 'by-category',
      {key: category, include_docs: true});
    return rl.rows.map(row => row.doc as Item);
  }
}

export default IndexController;
