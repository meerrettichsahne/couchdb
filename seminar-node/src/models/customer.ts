import Nano from "nano";
import {Item} from "@models/item";
import {Order} from "@models/order";

export class Customer implements Nano.MaybeDocument {
  _id: string;
  _rev: string;

  public username: string;
  public name: string;
  public basketItems: Item[] = [];
  public orders: Order[] = [];
  public type = 'customer';
}
