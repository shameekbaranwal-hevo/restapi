class Main {
  public static void main(String[] args) {
    Database usersDAO = new Database("data.csv");
    Server server = new Server(usersDAO);
    Client client = new Client(server);
    client.start();
    client.cleanup();
  }
}
