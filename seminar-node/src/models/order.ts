import Nano from "nano";
import {Item} from "@models/item";

export class Order implements Nano.MaybeDocument {
  _id: string;
  _rev: string;

  public items: Item[] = [];
  // date
  public submitted: number;
  public type = 'order';
}
