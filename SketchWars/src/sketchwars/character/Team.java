package sketchwars.character;

import sketchwars.GameObject;
import sketchwars.character.Character;
import sketchwars.character.weapon.AbstractWeapon;
import sketchwars.input.*;

import java.util.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author David Ly <ly_nekros@hotmail.com>
 */
public class Team 
{
    private ArrayList<Character> characters;
    private HashMap<AbstractWeapon.WeaponEnum, AbstractWeapon> weapons;
    private Character active;
    
    public Team(ArrayList<Character> characters, HashMap<AbstractWeapon.WeaponEnum, AbstractWeapon> weapons)
    {
        this.characters = characters;
        this.weapons = weapons;
        if(this.characters.size() > 0)
        {
            active = this.characters.get(0);
        }
    }

    public void changeAmmo(AbstractWeapon.WeaponEnum weaponType, int num)
    {
        weapons.get(weaponType).setAmmo(num);
    }
    
    public int getTotalHealth()
    {
        int total = 0;
        for (int i = 0; i < size(); i++)
            total += characters.get(i).getHealth();
        
        return total;
    }

    public void handleInput(Input input, double elapsedMillis)
    {
        for(Command command : input.getCommands())
        {
            switch(command)
            {
            case FIRE:
                active.fireCurrentWeapon(1.0);
                break;
            case AIM_UP:
                active.aimUp(elapsedMillis);
                break;
            case AIM_DOWN:
                active.aimDown(elapsedMillis);
                break;
            }
        }
    }

    public boolean isDead()
    {
        for(Character character : characters)
        {
            System.out.println(character.isDead());
            if(!character.isDead())
                return false;
        }
        return true;
    }
    
    public int size()
    {
        return characters.size();
    }

    public Character getActiveCharacter()
    {
        return active;
    }

    public void incrementActiveCharacter()
    {
        int idx = characters.indexOf(active);
        if(idx != size() - 1)
        {
            active = characters.get(0);
        }
        else
        {
            active = characters.get(idx + 1);
        }
    }
}
