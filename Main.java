class Main {
  public static void main(String[] args) {
    Server server = new Server();
    Client client = new Client(server);
    client.start();
  }
}
