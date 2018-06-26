import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class TCPServer {
    public static void main(String argv[]) throws Exception {


        boolean serverUp = true;
        ServerSocket welcomeSocket = new ServerSocket(8000);

        //initialize hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Socket connectionSocket = welcomeSocket.accept();


        while (serverUp) {

            //connect to client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            //client message
            String clientSentence = inFromClient.readLine();
            if(clientSentence == null) {
                continue;
            }

            //client String -> Message Object
            ObjectMapper mapper = new ObjectMapper();
            Message msg = mapper.readValue(clientSentence, Message.class);

            //construct reply
            Message reply = new Message();
            reply.setMessageType(msg.getMessageType());

            switch(msg.getMessageType()){

                case NEW_WORD:
                    System.out.println("new word");
                    reply.setMessageCode(HibernateUtil.addNewWord(session, msg));
                    break;

                case FETCH_DESCRIPTION_BY_WORD:
                    Description  description = HibernateUtil.fetchDescriptionByWord(session, msg);
                    System.out.println("fetched description - " + description.getDescription());
                    reply.setMessageCode(MessageCode.OK);
                    reply.setStringList(new ArrayList<>(Collections.singletonList(description.getDescription())));
                    break;

                case DELETE_WORD:
                    System.out.println("deleted");
                    reply.setMessageCode(HibernateUtil.deleteWord(session, msg));
                    break;

                case UPDATE_WORD:
                    System.out.println("updated");
                    reply.setMessageCode(HibernateUtil.updateWord(session, msg));
                    break;

                case SEARCH_FOR_WORD_BY_MASK:
                    List<String> words = HibernateUtil.fetchWordsByMask(session, msg);
                    System.out.println("fetch by mask: ");
                    for (String el: words){
                        System.out.println(el);
                    }
                    reply.setStringList(words);
                    reply.setMessageCode(MessageCode.OK);
                    break;

                case END_CONNECTION:
                    HibernateUtil.shutdown();
                    serverUp = false;
                    reply.setMessageCode(MessageCode.OK);
                    break;

                default:
                    throw new IllegalArgumentException("message type wasn't recognised");
            }

            //send reply
            String jsonInString = mapper.writeValueAsString(reply) + '\n';
            outToClient.writeBytes(jsonInString);

        }
    }
}