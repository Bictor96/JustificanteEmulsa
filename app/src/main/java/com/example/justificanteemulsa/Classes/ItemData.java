package com.example.justificanteemulsa.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {
  private ItemType itemType;
  private ItemState itemState;

  public ItemData(ItemType itemType, ItemState itemState) {
    this.itemType = itemType;
    this.itemState = itemState;
  }

  protected ItemData(Parcel in) {
    itemType = in.readParcelable(ItemType.class.getClassLoader());
    itemState = in.readParcelable(ItemState.class.getClassLoader());
  }

  public ItemState getItemState() {
    return itemState;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
    @Override
    public ItemData createFromParcel(Parcel in) {
      return new ItemData(in);
    }

    @Override
    public ItemData[] newArray(int size) {
      return new ItemData[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(itemType, flags);
    dest.writeParcelable(itemState, flags);
  }

  @Override
  public String toString() {
    return "Tipo del Objeto: " + itemType.toString() +
            "\nEstado del Objeto: " + itemState.toString();
  }
}