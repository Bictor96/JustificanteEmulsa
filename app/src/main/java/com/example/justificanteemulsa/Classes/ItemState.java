package com.example.justificanteemulsa.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemState implements Parcelable {
  private String State;

  public String getState() {
    return State;
  }

  public String getStateDescription() {
    return StateDescription;
  }

  private String StateDescription;

  public ItemState(String State, String StateDescription) {
    this.State = State;
    this.StateDescription = StateDescription;
  }

  public ItemState(String State) {
    this.State = State;
    this.StateDescription = "";
  }

  protected ItemState(Parcel in) {
    State = in.readString();
    StateDescription = in.readString();
  }

  public static final Creator<ItemState> CREATOR = new Creator<ItemState>() {
    @Override
    public ItemState createFromParcel(Parcel in) {
      return new ItemState(in);
    }

    @Override
    public ItemState[] newArray(int size) {
      return new ItemState[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(State);
    if (this.StateDescription != null)
      dest.writeString(StateDescription);
  }

  @Override
  public String toString() {
    return "Estado: " + this.State + "\nDescripción estado: '" + this.StateDescription;
  }
}