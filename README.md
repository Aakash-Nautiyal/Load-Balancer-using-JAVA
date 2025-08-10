# Java Load Balancer Simulation ⚖️💻

This project is a **Java-based Load Balancer Simulation** that demonstrates how a load balancer distributes client requests to multiple servers using different load balancing algorithms.  
It includes a **Swing-based GUI** 🎨 for real-time monitoring and control.

---

## ✨ Features

- **Three Load Balancing Algorithms** ⚙️  
  1. 🔄 **Round Robin** – Requests are distributed evenly in a circular order.  
  2. 📉 **Least Connections** – Requests are assigned to the server with the fewest active connections.  
  3. 🔢 **IP Hashing** – Requests are mapped to servers based on the client IP hash.  

- **Auto-Scaling** 📈📉  
  Automatically adds or removes server threads based on the average number of pending requests per server.  

- **Socket Programming** 🌐  
  Uses Java Sockets to send serialized `UserRequest` objects from clients to the load balancer.  

- **Thread-based Server Simulation** 🧵  
  Each server runs on its own thread and processes incoming requests from a shared BlockingQueue.  

- **Swing GUI** 🖥️  
  A **Matrix-style** control panel 💚🖤 that allows:  
    * ➕ Adding/removing servers manually  
    * 🔄 Switching between algorithms  
    * 🚀 Toggling auto-scaling ON/OFF  
    * 📊 Viewing real-time stats: current algorithm, total running servers, total requests handled  

---

## 🛠 Technologies & Concepts Used

- **Java Socket Programming**  
  * 📡 `ServerSocket` for listening to incoming client connections  
  * 📦 `ObjectOutputStream` / `ObjectInputStream` for sending and receiving serialized objects  

- **Object Serialization**  
  * 📄 `UserRequest` objects are serialized and sent over the network  

- **Multithreading**  
  * 🧵 Each server is a separate thread  
  * 🚚 Dispatcher thread in the load balancer distributes requests to the appropriate server  

- **BlockingQueue (LinkedBlockingQueue)**  
  * 🗃️ Used to store incoming requests and allow thread-safe processing  

- **Swing GUI**  
  * 🎛️ `JFrame`, `JButton`, `JToggleButton`, `JLabel` for the control panel  
  * 🎨 Styled with Matrix 💚 / black 🖤 theme  

---

## 📂 Project Structure

```plaintext
src/
├── 📄 Main.java                  – Sends UserRequest objects (Client simulation)
├── 📄 LoadBalancer.java          – Receives requests, distributes to servers
├── 📄 LoadBalancerConsoleUI.java – Swing control panel for managing servers & settings
├── 📄 Server.java                – Simulated server processing requests
├── 📄 UserRequest.java           – Serializable request object


📚 Key Learning Points
🧠 Understanding how load balancers work

🔄 Implementing different distribution algorithms

🌐 Using Java sockets to send serialized objects

🧵 Managing concurrency with threads and BlockingQueue

🎨 Creating a functional Swing GUI for system control

