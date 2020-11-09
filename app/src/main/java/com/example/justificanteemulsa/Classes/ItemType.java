package com.example.justificanteemulsa.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemType implements Parcelable {
  private String Type;

  public String getType() {
    return Type;
  }

  public String getBrand() {
    return Brand;
  }

  public String getSerialNumber() {
    return SerialNumber;
  }

  private String Brand;
  private String SerialNumber;

  public ItemType(String Type, String Brand, String SerialNumber) {
    this.Type = Type;
    this.Brand = Brand;
    this.SerialNumber = SerialNumber;
  }


  protected ItemType(Parcel in) {
    Type = in.readString();
    Brand = in.readString();
    SerialNumber = in.readString();
  }

  public static final Creator<ItemType> CREATOR = new Creator<ItemType>() {
    @Override
    public ItemType createFromParcel(Parcel in) {
      return new ItemType(in);
    }

    @Override
    public ItemType[] newArray(int size) {
      return new ItemType[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(Type);
    dest.writeString(Brand);
    dest.writeString(SerialNumber);
  }

  @Override
  public String toString() {
    return "Tipo: " + Type +
            "\nMarca: '" + Brand +
            "\nNúmero Serial: " + SerialNumber;
  }
}
