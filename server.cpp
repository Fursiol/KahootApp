#include <iostream>
#include <cstring>
#include <unistd.h>
#include <string>
#include <vector>
#include <arpa/inet.h>
#include <bits/stdc++.h>

using namespace std;

class question{
    public:
        int id;
        string question;
        string good_answer;
        vector<string> all_answers;
};

class player{
    public:
        int socket;
        string name;
        int score;
        string ip_address;
        int portNumber;
};

class room{
    public:
        int id;
        player owner;
        vector<question> questions;
        vector<player> players;
        string time_per_question;
        string enter_code;
};

string gen_random(const int len) {
    static const char alphanum[] =
        "0123456789"
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz";
    string tmp_s;
    tmp_s.reserve(len);

    for (int i = 0; i < len; ++i) {
        tmp_s += alphanum[rand() % (sizeof(alphanum) - 1)];
    }
    
    return tmp_s;
}

int main() {
    vector<room> rooms;

    room newroom;
    newroom.enter_code = "debil";
    rooms.push_back(newroom);
    
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

    if (listen(serverSocket, SOMAXCONN) == -1) {
        std::cerr << "Error listening on server socket\n";
        close(serverSocket);
        return -1;
    }

    std::cout << "Server is running on port " << PORT << std::endl;

    while (true) {
        sockaddr_in clientAddress{};
        socklen_t clientAddressLength = sizeof(clientAddress);
        int clientSocket = accept(serverSocket, reinterpret_cast<struct sockaddr*>(&clientAddress), &clientAddressLength);
        if (clientSocket == -1) {
            std::cerr << "Error accepting connection\n";
            continue;
        }

        char clientIP[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(clientAddress.sin_addr), clientIP, INET_ADDRSTRLEN);
        int clientPort = ntohs(clientAddress.sin_port);
        std::cout << "\nConnection from client: " << clientIP << ":" << clientPort << std::endl;

        char buffer[BUFFER_SIZE];
        ssize_t bytesRead = recv(clientSocket, buffer, BUFFER_SIZE - 1, 0);
        if (bytesRead == -1) {
            std::cerr << "Error receiving data\n";
        } else {
            buffer[bytesRead] = '\0';
            std::cout << "Received data from client " << clientIP << ":" << clientPort << ": ";

            string str = buffer;
            stringstream ss(str);
            string s;
            vector<string> v;
            while (getline(ss, s, '|')) {
                v.push_back(s);
            }

            for (int i = 0; i < v.size(); i++) {
                cout << v[i] << " ";
            }

            //Zalogowanie sie
            if(v[0] == "login"){
                send(clientSocket, "Y", strlen("Y"), 0);
                std::cout << "Sent login approval to client " << clientIP << ":" << clientPort << std::endl;
            }
            
            //Dolaczenie do quizu
            if(v[0] == "join"){
                player new_player;
                for(int i = 0; i < rooms.size(); i++){
                    if(v[1] == rooms[i].enter_code){
                        new_player.name = v[2];
                        new_player.ip_address = clientIP;
                        new_player.portNumber = clientPort;
                        rooms[i].players.push_back(new_player);
                        send(clientSocket, "Y", strlen("Y"), 0);
                        std::cout << "Sent join approval to client " << clientIP << ":" << clientPort << std::endl;
                        break;
                    }
                }
                if(!(new_player.name.size() > 0)){
                    send(clientSocket, "N", strlen("N"), 0);
                    std::cout << "Sent join DENIAL to client " << clientIP << ":" << clientPort << std::endl;
                }
            }

            //Utworzenie quizu
            if(v[0] == "create"){
                string code = gen_random(6);
                room new_room;
                player owner;
                owner.socket = clientSocket;
                owner.ip_address = clientIP;
                owner.portNumber = clientPort;
                new_room.owner = owner;
                new_room.time_per_question = v[2];
                new_room.enter_code = code;

                char* codeChar = new char[code.size() + 1]; 
                strcpy(codeChar, code.c_str()); 

                send(clientSocket, codeChar, strlen(codeChar), 0);
                std::cout << "Sent create approval to client " << clientIP << ":" << clientPort << std::endl;

                rooms.push_back(new_room);
                delete codeChar;
            }

            //Dodanie pytania
            if(v[0] == "add"){
                for(int i = 0; i < rooms.size(); i++){
                    if(rooms[i].enter_code == v[1]){
                        question new_q;
                        new_q.question = v[2];
                        new_q.good_answer = v[3];
                        new_q.all_answers = {v[3], v[4], v[5], v[6]};
                        rooms[i].questions.push_back(new_q);

                        send(clientSocket, "Y", strlen("Y"), 0);
                        std::cout << "Sent question add approval to client " << clientIP << ":" << clientPort << std::endl;
                    }
                }

            }

            //Otrzymanie informacji na temat poczekalni
            if(v[0] == "w8info"){

            }

            //Uruchomienie quizu
            if(v[0] == "start"){

            }

            //Przeslanie odpowiedzi
            if(v[0] == "answer"){

            }

            //Otrzymanie informacji na temat punktacji
            if(v[0] == "quizinfo"){

            }

            close(clientSocket);
        }
    }

    close(serverSocket);
    return 0;
}