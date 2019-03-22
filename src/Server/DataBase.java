package Server;

import java.io.*;
import java.util.HashMap;

class ClientsDB {
    private static HashMap<String,Integer> clients = new HashMap<>();
    private final String PATH_TO_DB = "ClientsDB.txt";

    ClientsDB(){
        try{
            File fileDB = new File(PATH_TO_DB);
            FileInputStream fileInputStream = new FileInputStream(fileDB);
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            String[] strData;
            while ((strLine = buffReader.readLine()) != null){
                strData = strLine.split(" ");
                try{
                    clients.put(strData[0],Integer.parseInt(strData[1]));
                }catch(NumberFormatException exc){
                    System.out.println(strData[1] + " is NaN.");
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("FileNotFoundException");
        }catch (IOException e){
            System.out.println("Error while reading DB");
        }
    }

    boolean isClientNotInDB(String login){
        return clients.get(login) == null;
    }

    void addToDB(String login, String pass){
        if(isClientNotInDB(login)){
            clients.put(login,pass.hashCode());
            writeToDB(login, pass);
        }
    }

    private void writeToDB(String login, String pass){
        try{
            File fileDB = new File(PATH_TO_DB);
            FileWriter writer = new FileWriter(fileDB, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write("\n" +login + " " + (pass.hashCode()));
            bufferWriter.close();
        }catch (IOException e){
            System.out.println("Error while writing to DB");
        }
    }

    boolean checkAuth(String login, String pass){
        Integer passFromDB = clients.get(login);
        return passFromDB != null && passFromDB.equals(pass.hashCode());
    }
}