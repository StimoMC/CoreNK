package net.core.emotes;

import java.util.HashMap;

public class Emotes {

    protected static HashMap<String, String> emotes = new HashMap<>();

    public Emotes(){
    }

    public static void addEmote(String emoteName, String emoteID){
        if (!emotes.containsKey(emoteName)){
            emotes.put(emoteName, emoteID);
        }
    }

    public static HashMap<String, String> getEmotes() {
        return emotes;
    }
}
