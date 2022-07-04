package enclave.encare.encare.check;

import enclave.encare.encare.form.MessageForm;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

public class ClientDoctor {
    public static void main(String[] args) throws Exception {
        long accountDoctorId = 6;
        long channelId = 1;

        Scanner scanner = new Scanner(System.in);
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        ClientSessionHandler clientSessionHandler = new ClientSessionHandler();
        ListenableFuture<StompSession> listenableFuture = stompClient.connect(
//                "ws://enclave-encare.herokuapp.com/ws",clientSessionHandler
                "ws://localhost:8080/ws",clientSessionHandler
        );
        StompSession session = listenableFuture.get();
        session.subscribe("/topic/messages/"+accountDoctorId,clientSessionHandler);
        while (true){
            Thread.sleep(2000);
            System.out.print("Said something: ");
            String text = scanner.nextLine();
            session.send("/app/chat", new MessageForm(channelId, accountDoctorId, text));
        }
    }
}