# Ocean View Resort - Application Guide

This application consists of two main components: a **Java EE Backend** and a **Next.js Frontend**. Since this project does not use frameworks like Spring Boot, the backend requires a Jakarta EE compliant application server.

## 1. Running the Backend (Java EE)

The backend is a standard Maven project that packages into a `.war` file.

### Prerequisites:
- **JDK 17** or higher.
- **Apache Maven**.
- **Jakarta EE Application Server** (e.g., Apache TomEE, WildFly, GlassFish, or Payara).

### Steps:
1.  **Navigate to the backend directory:**
    ```powershell
    cd "backend"
    ```
2.  **Build the project:**
    ```powershell
    mvn clean package
    ```
    This will generate a `backend.war` file in the `target/` directory.
3.  **Deploy to Application Server:**
    - Copy `target/backend.war` to the `webapps` (TomEE) or `deployments` (WildFly) folder of your server.
    - Start the server.
4.  **Access the API:**
    - The sample endpoint will be available at: `http://localhost:8080/backend/api/hello` (the port and context path may vary depending on your server configuration).

## 2. Running the Frontend (Next.js)

The frontend is a React-based application using the Next.js framework.

### Prerequisites:
- **Node.js** (v18.x or later recommended).
- **npm** or **yarn**.

### Steps:
1.  **Navigate to the frontend directory:**
    ```powershell
    cd "frontend"
    ```
2.  **Install dependencies:**
    ```powershell
    npm install
    ```
3.  **Run in development mode:**
    ```powershell
    npm run dev
    ```
4.  **Access the application:**
    - Open your browser and go to: `http://localhost:3000`

## Summary of Ports
- **Backend:** Usually `8080` (depends on your Application Server).
- **Frontend:** `3000` (Next.js default).

To connect the frontend to the backend, you would typically use `fetch` or a similar library in your React components to call the URLs exposed by the Java EE server.
