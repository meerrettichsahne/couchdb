import Nano from "nano";

export class Item implements Nano.MaybeDocument {
  _id: string;
  _rev: string;

  public name: string;
  public description: string;
  public category: string;
  public price: number;
  public type = 'item';
}
