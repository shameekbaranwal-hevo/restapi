import java.util.Scanner;

/**
 * Client
 */
public class Client {
  Server server;
  Scanner sc;

  Client(Server server) {
    this.server = server;
    sc = new Scanner(System.in);
  }

  public void start() {
    assert (server != null);
    System.out.println("--------starting client---------");
    System.out.println("Welcome to crud.tui.");

    home();
  }

  private void home() {
    System.out.println("\n---------HOME---------");
    System.out.println("You have the following options:\n"
                       + "1. Create a new User.\n"
                       + "2. View Users.\n"
                       + "3. Update User details.\n"
                       + "4. Delete a User.\n"
                       + "5. Close client.\n"
                       + "Select any one and press Enter:\n");
    int option = getUserInput(1, 5);
    switch (option) {
    case 1: {
      create();
      break;
    }
    case 2: {
      view();
      break;
    }
    case 3: {
      update();
      break;
    }
    case 4: {
      delete();
      break;
    }
    default: {
      System.out.println("--------closing client---------");
      return;
    }
    }

    home();
  }

  private void create() {
    // first_name, last_name, id, role
    Request req = new Request(RequestType.POST);
    req.user = new User();
    System.out.println("\n-------CREATE-------");
    System.out.println("\nEnter first name of the new user: ");
    req.user.first_name = sc.nextLine();
    System.out.println("\nEnter last name of the new user: ");
    req.user.last_name = sc.nextLine();
    System.out.println("\nEnter a (unique) id for the new user: ");
    req.user.id = getUserInput(1, Integer.MAX_VALUE);
    req.id = req.user.id;
    System.out.println("\nSelect a role for the new user: ");
    for (Role role : Role.values())
      System.out.println(role.ordinal() + 1 + ": " + role);
    int role_selection = getUserInput(1, Role.values().length);
    req.user.role = Role.values()[role_selection];

    System.out.println();
    System.out.println("\nInserting user: " + req.user +
                       " into the database\n");
    Response res = server.POST(req);
    System.out.println(res);
  }

  private void view() {
    System.out.println("\n-------VIEW-------");

    Request req = new Request(RequestType.GET);

    // keep taking filter preferences from user until they enter 7
    while (true) {
      System.out.println(
          "\nSelect any filters for your search, or use 7 to continue with selected filters.\n"
          + "You have the following options:\n"
          + "1. Filter by ID.\n"
          + "2. Exclude Roles.\n"
          + "3. First Name must contain.\n"
          + "4. Last name must contain.\n"
          + "5. Set maximum number of results.\n"
          + "6. Set page number.\n"
          + "7. Continue.\n");

      int option = getUserInput(1, 7);
      if (option == 7)
        break;

      switch (option) {
      case 1: {
        req.id = getUserInput(0, Integer.MAX_VALUE);
        break;
      }
      case 2: {
        // update req.excludeRoles
        handleRoleExcludeSelection(req);
        break;
      }
      case 3: {
        // update req.first_name_contains
        req.first_name_includes = sc.nextLine();
        break;
      }
      case 4: {
        // update req.last_name_contains
        req.last_name_includes = sc.nextLine();
        break;
      }
      case 5: {
        // update req.count
        req.count = getUserInput(1, Integer.MAX_VALUE);
        break;
      }
      case 6: {
        // update req.page
        req.page = getUserInput(1, Integer.MAX_VALUE);
        break;
      }
      }
    }

    Response res = server.GET(req);
    System.out.println(res.body);
    // add option to see next page or previous page
  }

  private void update() {}
  private void delete() {}

  private void handleRoleExcludeSelection(Request req) {
    System.out.println("\nThe following roles are available:\n");

    // +1 to maintain the 1-based indexing used for options
    for (Role role : Role.values())
      System.out.println((role.ordinal() + 1) + ": " + role);
    System.out.println(Role.values().length + 1 + ": Continue");

    System.out.println("\nSelect a role to exclude, and press Enter:\n");
    while (true) {
      int exclude = getUserInput(1, Role.values().length + 1);
      if (exclude == Role.values().length + 1)
        break;
      req.excludeRoles.add(Role.values()[exclude - 1]);
      System.out.println("You can select more roles, or 6 to continue.");
    }
  }

  private int getUserInput(int lower, int upper) {
    int option = sc.nextInt();
    while (option < lower || option > upper) {
      System.out.println("Invalid option, try again.");
      option = sc.nextInt();
    }
    sc.nextLine(); // this is to consume the \n right after an integer, which
                   // would interfere with any nextLine ahead
    return option;
  }

  public void cleanup() { sc.close(); }
}
