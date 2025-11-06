package com.github.andradenathan;

public record Arguments(
    double gravity, double mass1, double length1, double mass2, double length2) {

  public static Arguments usingDefault() {
    return new Arguments(9.81, 10.0, 150.0, 10.0, 150.0);
  }

  private void validate(double gravity, double m1, double m2, double l1, double l2) {
    if (gravity <= 0) {
      throw new IllegalArgumentException("Gravity must be positive.");
    }
    if (m1 <= 0 || m2 <= 0) {
      throw new IllegalArgumentException("Masses must be positive.");
    }
    if (l1 <= 0 || l2 <= 0) {
      throw new IllegalArgumentException("Lengths must be positive.");
    }
  }

  @Override
  public String toString() {
    return String.format(
        "g=%.2f, m1=%.2f, m2=%.2f, L1=%.2f, L2=%.2f", gravity, mass1, length1, mass2, length2);
  }
}
