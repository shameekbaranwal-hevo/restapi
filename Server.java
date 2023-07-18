import java.util.ArrayList;
import java.util.List;

enum RequestType { GET, POST, PUT, DELETE }

class Request {
  RequestType type;
  int count; // for GET, in case of a lot of results
  int page;  // for GET, in case of a lot of results
  int id;    // for GET, POST, PUT, DELETE, for reading or writing to a single
             // object
  User user; // for POST, PUT, for writing to that single object

  // following may be used for further filtering in GET requests
  List<Role> excludeRoles; // for GET, for filtering based on acceptable roles
  String first_name_includes; // for GET, for filtering based on first name
  String last_name_includes;  // for GET, for filtering based on last name

  Request(RequestType type) {
    this.count = 0;
    this.type = type;
    this.page = 0;
    this.id = -1;
    this.user = null;
    this.excludeRoles = new ArrayList<>();
    this.first_name_includes = "";
    this.last_name_includes = "";
  }
}

class Response {
  List<User> body;
  int statusCode;
  boolean isSuccessful;

  @Override
  public String toString() {
    return "---------------------\n" + (!isSuccessful ? "NOT " : "") +
        "SUCCESSFUL\n"
        + "STATUS CODE: " + statusCode + "\n" + body;
  }
}

class Server {
  private Database usersDAO;

  Server() { this.usersDAO = new Database(); }

  public Response GET(Request request) {
    Response res = new Response();

    // invalid request
    if (request.type != RequestType.GET) {
      res.isSuccessful = false;
      res.statusCode = 400; // bad request
      return res;
    }

    // user requested for complete data of one user
    if (request.id > -1) {
      res.body = usersDAO.SELECT(user -> user.id == request.id);
      if (res.body.size() == 1) {
        res.statusCode = 200; // ok
        res.isSuccessful = true;
      } else {
        res.statusCode = 404; // not found
        res.isSuccessful = false;
      }
      return res;
    }

    // user requested all users some filter
    res.body = usersDAO.SELECT(
        user
        // role doesnt match any excluded roles (empty list by default)
        -> (request.excludeRoles.indexOf(user.role) == -1 &&
            // first name contains (default)
            user.first_name.contains(request.first_name_includes) &&
            // last name contains the (default)
            user.last_name.contains(request.last_name_includes)));

    res.statusCode = 200; // ok
    res.isSuccessful = true;

    return res;
  }

  public Response POST(Request request) {
    Response res = new Response();
    res.isSuccessful = usersDAO.INSERT(request.user);
    res.statusCode =
        res.isSuccessful ? 200 : 400; // ok or bad request (id conflict)
    return res;
  }

  public Response PUT(Request request) {
    Response res = new Response();
    res.isSuccessful = usersDAO.UPDATE(request.user);
    res.statusCode =
        res.isSuccessful ? 200 : 404; // ok or not found (unknown id)
    return res;
  }

  public Response DELETE(Request request) {
    Response res = new Response();
    res.isSuccessful = usersDAO.DELETE(request.user.id);
    res.statusCode =
        res.isSuccessful ? 200 : 404; // ok or not found (unknown id)
    return res;
  }
}
