package com.example.justificanteemulsa.Classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
  public String getName() {
    return Name;
  }

  public String getNIF() {
    return NIF;
  }

  public String getPhoneNumber() {
    return PhoneNumber;
  }

  public String getEmail() {
    return Email;
  }

  private String Name;
  private String NIF;
  private String PhoneNumber;
  private String Email;

  public UserData(String Name, String NIF, String PhoneNumber, String Email) {
    this.Name = Name;
    this.NIF = NIF;
    this.PhoneNumber = PhoneNumber;
    this.Email = Email;
  }

  protected UserData(Parcel in) {
    Name = in.readString();
    NIF = in.readString();
    PhoneNumber = in.readString();
    Email = in.readString();
  }

  public static final Creator<UserData> CREATOR = new Creator<UserData>() {
    @Override
    public UserData createFromParcel(Parcel in) {
      return new UserData(in);
    }

    @Override
    public UserData[] newArray(int size) {
      return new UserData[size];
    }
  };

  @Override
  public String toString() {
    return
            "Nombre: " + Name  +
                    "\nNIF: " + NIF  +
                    "\nTeléfono: " + PhoneNumber +
                    "\nEmail: " + Email;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(Name);
    dest.writeString(NIF);
    dest.writeString(PhoneNumber);
    dest.writeString(Email);
  }
}