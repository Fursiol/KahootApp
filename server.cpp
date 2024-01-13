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
        int score = 0;
        vector<int> answers;
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
        int ready = 0;
        vector<int> sockets;
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

int main(int argc, char *argv[]) {
    vector<room> rooms;

    room newroom;
    newroom.enter_code = "debil";
    rooms.push_back(newroom);

    const int PORT = stoi(argv[1]);
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
                close(clientSocket);
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
                close(clientSocket);
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
                close(clientSocket);
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
                close(clientSocket);
            }

            //Otrzymanie informacji na temat poczekalni
            if(v[0] == "w8info"){
                string response = " ";
                for(int i = 0; i < rooms.size(); i++){
                    if(v[1] == rooms[i].enter_code){
                        response += rooms[i].players.size();
                        for(int j = 0; j < rooms[i].players.size(); j++){
                            response += rooms[i].players[j].name;
                            response += ",";
                        }
                    }
                }
                cout << response << endl;
                send(clientSocket, response.c_str(), strlen(response.c_str()), 0);
                std::cout << "Sent question add approval to client " << clientIP << ":" << clientPort << std::endl;
                close(clientSocket);
            }

            //Uruchomienie quizu
            if(v[0] == "start"){
                for(int i =0; i < rooms.size(); i++){
                    if(v[1] == rooms[i].enter_code){
                        rooms[i].ready += 1;
                        rooms[i].sockets.push_back(clientSocket);
                        cout << "Got start request for room " << v[1] << " no. " << rooms[i].ready << " of " << rooms[i].players.size() + 1<< endl;
                        if(rooms[i].ready == rooms[i].players.size() + 1){
                            cout << "Starting quiz " << v[1] << endl;
                            for(int sock = 0; sock < rooms[i].sockets.size(); sock++){
                                close(rooms[i].sockets[sock]);
                            }
                        }
                    }
                }
            }

            //Przeslanie odpowiedzi
            if(v[0] == "answer"){
                string response_question = "";
                for(int i = 0; i < rooms.size(); i++){
                    if(rooms[i].enter_code == v[1]){
                        if (stoi(v[3]) < rooms[i].questions.size()){
                            response_question += rooms[i].time_per_question;
                            response_question += "|";
                            response_question += rooms[i].questions[stoi(v[3])].question;
                            response_question += "|";
                            for(int j = 0; j < rooms[i].questions[stoi(v[3])].all_answers.size(); j++){
                                    response_question += rooms[i].questions[stoi(v[3])].all_answers[j];
                                    response_question += "|";
                            }
                        }
                        if(stoi(v[3]) > 0){
                            cout << v[4] << endl;
                            cout << rooms[i].questions[stoi(v[3]) - 1].good_answer << endl;
                            //Dobra odpowiedz
                            if(v[4] == rooms[i].questions[stoi(v[3]) - 1].good_answer){
                                for(int j = 0; j < rooms[i].players.size(); j++){
                                    if(v[2] == rooms[i].players[j].name){
                                        rooms[i].players[j].score += 1;
                                        rooms[i].players[j].answers.push_back(1);
                                        response_question += rooms[i].players[j].name;
                                        response_question += "|";
                                        response_question += to_string(rooms[i].players[j].score);
                                        response_question += "|";
                                    } else {
                                        response_question += rooms[i].players[j].name;
                                        response_question += "|";
                                        response_question += to_string(rooms[i].players[j].score);
                                        response_question += "|";
                                    }
                                }
                            } else { //Zla odpowiedz
                                for(int j = 0; j < rooms[i].players.size(); j++){
                                    if(v[2] == rooms[i].players[j].name){
                                        rooms[i].players[j].answers.push_back(0);
                                        response_question += rooms[i].players[j].name;
                                        response_question += "|";
                                        response_question += to_string(rooms[i].players[j].score);
                                        response_question += "|";
                                    } else {
                                        response_question += rooms[i].players[j].name;
                                        response_question += "|";
                                        response_question += to_string(rooms[i].players[j].score);
                                        response_question += "|";
                                    }
                                }
                            }
                        } else { //Przed pierwsza odpowiedzia
                            for(int j = 0; j < rooms[i].players.size(); j++){
                                response_question += rooms[i].players[j].name;
                                response_question += "|";
                                response_question += to_string(rooms[i].players[j].score);
                                response_question += "|";
                            }
                        }
                        send(clientSocket, response_question.c_str(), strlen(response_question.c_str()), 0);
                        cout << "Got answer from " << v[2] << endl;
                        close(clientSocket);
                        break;
                    }
                }
            }

            //Otrzymanie informacji na temat punktacji
            if(v[0] == "quizinfo"){
                string response_info = "";
                for(int i = 0; i < rooms.size(); i++){
                    if(rooms[i].enter_code == v[1]){
                        for(int j = 0; j < rooms[i].players.size(); j++){
                            response_info += rooms[i].players[j].name;
                            response_info += "|";
                            response_info += to_string(rooms[i].players[j].score);
                            response_info += "|";
                            if(rooms[i].players[j].answers.size() == 0){
                                response_info += "0";
                            }
                            for(int k = 0; k < rooms[i].players[j].answers.size(); k++){
                                response_info += to_string(rooms[i].players[j].answers[k]);
                                response_info += ",";
                            }
                            response_info += "|";
                        }
                        send(clientSocket, response_info.c_str(), strlen(response_info.c_str()), 0);
                        cout << "Sent quiz info: " << v[2] << endl;
                        close(clientSocket);
                        break;
                    }
                }
            }
        }
    }

    close(serverSocket);
    return 0;
}