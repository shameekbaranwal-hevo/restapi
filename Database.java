import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Database {
  Map<Integer, User> users;

  public Map<Integer, User> getUsers() { return users; }

  Database() {
    users = new TreeMap<>();
    // users = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader("data.csv"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(", ");
        assert (values.length == 4);

        User curr = new User();
        curr.first_name = values[0];
        curr.last_name = values[1];
        curr.id = Integer.parseInt(values[2]);

        try {
          curr.role = Role.valueOf(values[3]);
        } catch (IllegalArgumentException e) {
          curr.role = Role.unassigned;
        }

        this.INSERT(curr);
      }

    } catch (IOException e) {
      System.err.println("system error in initialising database");
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void show() { users.forEach((id, user) -> System.out.println(user)); }

  // returns false if there was an ID conflict (does not update the pre-existing
  // record)
  public boolean INSERT(User user) {
    if (users.get(user.id) != null)
      return false;

    users.put(user.id, user);
    return true;
  }

  // returns false if there was no object found with the provided ID
  public boolean DELETE(int userID) {
    if (users.get(userID) == null)
      return false;

    users.remove(userID);
    return true;
  }

  // returns false if there was no object found with the provided ID (does not
  // make any changes to the database)
  public boolean UPDATE(User user) {
    if (users.get(user.id) == null)
      return false;

    users.put(user.id, user);
    return true;
  }

  // runs the provided predicate on all users, and returns a list of the users
  // that pass
  public List<User> SELECT(Predicate<User> query) {
    List<User> result =
        users.values().stream().filter(query).collect(Collectors.toList());
    return result;
  }
}
