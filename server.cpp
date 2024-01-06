#include <iostream>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>

int main() {
    const int PORT = 12345;
    const int BUFFER_SIZE = 1024;

    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1) {
        std::cerr << "Error creating server socket\n";
        return -1;
    }

    sockaddr_in serverAddress{};
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = INADDR_ANY;
    serverAddress.sin_port = htons(PORT);

    if (bind(serverSocket, reinterpret_cast<struct sockaddr*>(&serverAddress), sizeof(serverAddress)) == -1) {
        std::cerr << "Error binding server socket\n";
        close(serverSocket);
        return -1;
    }

    if (listen(serverSocket, 1) == -1) {
        std::cerr << "Error listening on server socket\n";
        close(serverSocket);
        return -1;
    }

    std::cout << "Server is running on port " << PORT << std::endl;

    sockaddr_in clientAddress{};
    socklen_t clientAddressLength = sizeof(clientAddress);
    int clientSocket = accept(serverSocket, reinterpret_cast<struct sockaddr*>(&clientAddress), &clientAddressLength);
    if (clientSocket == -1) {
        std::cerr << "Error accepting connection\n";
        close(serverSocket);
        return -1;
    }

    char buffer[BUFFER_SIZE];
    ssize_t bytesRead = recv(clientSocket, buffer, BUFFER_SIZE - 1, 0);
    if (bytesRead == -1) {
        std::cerr << "Error receiving data\n";
    } else {
        buffer[bytesRead] = '\0'; // Null-terminate the received data
        std::cout << "Received data from Java client: " << buffer << std::endl;

        // Odpowiedź do klienta (potwierdzenie)
        const char* confirmationMessage = "Message received by the server. Thank you!";
        send(clientSocket, confirmationMessage, strlen(confirmationMessage), 0);
        std::cout << "Sent confirmation to Java client\n";
    }

    close(clientSocket);
    close(serverSocket);

    return 0;
}