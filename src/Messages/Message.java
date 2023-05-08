package Messages;

import java.io.Serializable;

public abstract  class Message implements Serializable {
    private final Enum action;
    public Message(Enum action){
        this.action = action;
    }
    public Message(){
        this.action = null;
    }


    public Enum getMessageAction() {
        return action;
    }

}

