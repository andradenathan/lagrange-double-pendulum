package com.github.andradenathan;

public record Arguments(
    double gravity, double mass1, double length1, double mass2, double length2) {

  public static Arguments usingDefault() {
    return new Arguments(9.81, 10.0, 150.0, 10.0, 150.0);
  }
}
