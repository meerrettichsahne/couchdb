import { Router } from 'express';
import IndexController from '@controllers/index.controller';
import { Routes } from '@interfaces/routes.interface';

class IndexRoute implements Routes {
  public path = '/';
  public router = Router();
  public indexController = new IndexController();

  constructor() {
    this.initializeRoutes();
  }

  private initializeRoutes() {
    this.router.get(`${this.path}customer/:username`, this.indexController.getCustomerByUsername);
    this.router.post(`${this.path}customer`, this.indexController.createCustomer);
    this.router.delete(`${this.path}customer/:username`, this.indexController.deleteCustomer);
    this.router.get(`${this.path}customer/:username/basket`, this.indexController.getUserBasket);
    this.router.get(`${this.path}customer/:username/basket-sum`, this.indexController.getUserBasketSum);
    this.router.post(`${this.path}customer/:username/basket`, this.indexController.addToBasket);
    this.router.delete(`${this.path}customer/:username/basket`, this.indexController.removeFromBasket);
    this.router.post(`${this.path}customer/:username/checkout`, this.indexController.checkoutBasket);
    this.router.get(`${this.path}customer/:username/orders`, this.indexController.getUserOrders);
    this.router.get(`${this.path}items`, this.indexController.getItems);
    this.router.get(`${this.path}items/:category`, this.indexController.getItemsByCategory);
    this.router.post(`${this.path}items`, this.indexController.createItem);
    this.router.delete(`${this.path}items/:id`, this.indexController.deleteItem);
  }
}

export default IndexRoute;
