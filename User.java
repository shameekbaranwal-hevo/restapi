

public class User {
  String first_name;
  String last_name;
  int id;
  Role role;

  @Override
  public String toString() {
    return "\n\t=========================="
        + "\n\t||name:\t" + first_name + " " + last_name + "\n\t||id:\t" + id +
        "\n\t||role:\t" + role + "\n\t==========================\n";
  }

  @Override
  public int hashCode() {
    return this.id;
  }
}
