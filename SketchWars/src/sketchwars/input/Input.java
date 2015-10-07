package sketchwars.input;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Input 
{
    public static Input currentInput;

    public static void update() {
        currentInput = new Input();

        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            currentInput.commands.add(Command.JUMP);
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            currentInput.commands.add(Command.CROUCH);
        }
        
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A) && !KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
            currentInput.commands.add(Command.MOVE_LEFT);
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_D) && !KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
            currentInput.commands.add(Command.MOVE_RIGHT);
        }
        
        if(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)){
            currentInput.commands.add(Command.FIRE);
        }
        
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP) && !KeyboardHandler.isKeyDown(GLFW_KEY_DOWN)) {
            currentInput.commands.add(Command.AIM_UP);
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN) && !KeyboardHandler.isKeyDown(GLFW_KEY_UP)) {
            currentInput.commands.add(Command.AIM_DOWN);
        }
    }

    private ArrayList<Command> commands;

    private Input() {
        commands = new ArrayList<>();
    }
    
 
    
    public byte[] serializeByteArray()
    {
        byte[] ret = new byte[1 + commands.size()];
        ret[0] = (byte) commands.size();
        for(int i = 1; i < commands.size() + 1; i++)
        {
            ret[i] = (byte) commands.get(i - 1).ordinal();
        }
        return ret;
    }
    
    public static Input deserializeByteArray(byte[] value)
    {
        //Lenght of command
        byte temp = value[0];
        Input input = new Input();
        //Parase array to get input
        for(int i = 1; i < temp +1; i++)
        {
            input.commands.add(Command.values()[value[i]]);
        }
        
        return input;
    }

    public List<Command> getCommands() {
        return commands;
    }
}