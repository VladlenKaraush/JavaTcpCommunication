import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class TCPClient {

    /**
     * example of api usage
     * @throws Exception - SocketException
     */
    private static void testApi() throws Exception {
        Socket clientSocket = new Socket("localhost", 8000);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            ObjectMapper mapper = new ObjectMapper();

        //new word
        Message message = new Message();
        message.setMessageType(MessageType.NEW_WORD);
        message.setStringList(new ArrayList<>(Arrays.asList("recursion", "see recursion")));
        String jsonInString = mapper.writeValueAsString(message) + '\n';
        outToServer.writeBytes(jsonInString);

        //answer
        String answer = inFromServer.readLine();
        System.out.println("FROM SERVER: " + answer);

        //new word
        message.setMessageType(MessageType.NEW_WORD);
        message.setStringList(new ArrayList<>(Arrays.asList("word", "meta")));
        jsonInString = mapper.writeValueAsString(message) + '\n';

        outToServer.writeBytes(jsonInString);
        answer = inFromServer.readLine();
        System.out.println("FROM SERVER: " + answer);

        //update
        message.setMessageType(MessageType.UPDATE_WORD);
        message.setStringList(new ArrayList<>(Arrays.asList("word", "qwerecursionzxc", "new description instead of meta")));
        jsonInString = mapper.writeValueAsString(message) + '\n';

        outToServer.writeBytes(jsonInString);
        answer = inFromServer.readLine();
        System.out.println("FROM SERVER: " + answer);

        //search for word by mask
        message.setMessageType(MessageType.SEARCH_FOR_WORD_BY_MASK);
        message.setStringList(new ArrayList<>(Collections.singletonList("*recursion*")));
        jsonInString = mapper.writeValueAsString(message) + '\n';

        outToServer.writeBytes(jsonInString);
        answer = inFromServer.readLine();
        System.out.println("FROM SERVER: " + answer);

        //fetch description
        message.setMessageType(MessageType.FETCH_DESCRIPTION_BY_WORD);
        message.setStringList(new ArrayList<>(Collections.singletonList("recursion")));
        jsonInString = mapper.writeValueAsString(message) + '\n';

        outToServer.writeBytes(jsonInString);
        answer = inFromServer.readLine();
        System.out.println("FROM SERVER: " + answer);


        clientSocket.close();
    }


    /**
     * parses user input and constructs message
     * @param input - user console input
     * @return - fully constructed message
     */
    private static Message constructMessage(String input) {

        Message message = new Message();
        String[] args = input.split(" ");
        for(MessageType type : MessageType.values()){
            if(type.getLetter().equals(args[0]))
                message.setMessageType(type);
        }

        switch (message.getMessageType()) {
            case NEW_WORD:
                if(args.length > 2)
                    message.setStringList(new ArrayList<>(Arrays.asList(args[1], args[2])));
                else
                    message.setStringList(new ArrayList<>(Collections.singletonList(args[1])));
                break;

            case UPDATE_WORD:
                if(args.length > 3)
                    message.setStringList(new ArrayList<>(Arrays.asList(args[1], args[2], args[3])));
                else
                    message.setStringList(new ArrayList<>(Arrays.asList(args[1], args[2])));
                break;

            case DELETE_WORD: case FETCH_DESCRIPTION_BY_WORD: case SEARCH_FOR_WORD_BY_MASK:
                message.setStringList(new ArrayList<>(Collections.singletonList(args[1])));
                break;

            case END_CONNECTION:
                break;

             default:
                 System.out.println("Command wasn't recognised");
                 throw new IllegalArgumentException("Command wasn't recognised");
        }

        return message;
    }


    public static void main(String argv[]) throws Exception {

        //testApi();
        String help = "manual how to use api:\n" +
                "1. add new word - N 'word' 'description'(optional)\n" +
                "2. update existing word - U 'existing word' 'new word' 'description'(optional)\n" +
                "3. delete word - D 'word'\n" +
                "4. fetch description of a word - F 'word'\n" +
                "5. fetch words fitting mask(? for 1 arbitrary symbol, * for any number of arbitrary symbols) - S - 'mask'\n" +
                "6. shutdown server - E\n" +
                "7. shutdown client - q\n";

        System.out.println(help);

        //socket on 8000 port
        Socket clientSocket = new Socket("localhost", 8000);

        //connect to server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //user input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //jackson json mapper(json -> String and String -> json)
        ObjectMapper mapper = new ObjectMapper();

        String input = inFromUser.readLine();
        boolean run = true;
        while(!input.equals("q") && run){

            Message message = constructMessage(input);

            String jsonInString = mapper.writeValueAsString(message) + '\n';
            outToServer.writeBytes(jsonInString);

            //answer
            String answer = inFromServer.readLine();
            //System.out.println(answer);
            Message messageAnswer = mapper.readValue(answer, Message.class);

            if (messageAnswer.getMessageCode() == MessageCode.WORD_ALREADY_EXISTS)
                System.out.println("word  \'" + messageAnswer.getStringList().get(0) + "\' already exists");
            else
                System.out.println(messageAnswer.getMessageCode());
            //output requested words
            if(messageAnswer.getMessageType() == MessageType.SEARCH_FOR_WORD_BY_MASK)
            {
                System.out.println("words by mask:");
                for (String el: messageAnswer.getStringList()){
                    System.out.println(el);
                }
            }
            //output description
            else if(messageAnswer.getMessageType() == MessageType.FETCH_DESCRIPTION_BY_WORD)
            {
                System.out.println(messageAnswer.getStringList().get(0));
            }
            else if(messageAnswer.getMessageType() == MessageType.END_CONNECTION)
                run = false;

            input = inFromUser.readLine();
        }

        //testApi();
        //ObjectMapper mapper = new ObjectMapper();
        //Message msg = mapper.readValue(clientSentence, Message.class);
    }
}