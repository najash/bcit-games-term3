package sketchwars.character;

import sketchwars.character.weapon.AbstractWeapon;
import sketchwars.character.weapon.WeaponTypes;
import sketchwars.input.*;
import sketchwars.HUD.HealthBar;
import java.util.*;

/**
 *
 * @author David Ly <ly_nekros@hotmail.com>
 */
public class Team 
{
    private final ArrayList<SketchCharacter> characters;
    private final HashMap<WeaponTypes, AbstractWeapon> weapons;
    private SketchCharacter active;
    private HealthBar healthBar;
    
    public Team(ArrayList<SketchCharacter> characters, HashMap<WeaponTypes, AbstractWeapon> weapons)
    {
        this.characters = characters;
        this.weapons = weapons;
        healthBar = new HealthBar();
        if(this.characters.size() > 0)
        {
            setActiveCharacter(this.characters.get(0));
        }
    }
    
    public void setHealthBar (HealthBar healthbar)
    {
        healthBar = healthbar;
    }
    
    public HealthBar getHealthBar()
    {
        return healthBar;
    }
    public void changeAmmo(WeaponTypes weaponType, int num)
    {
        weapons.get(weaponType).setAmmo(num);
    }
    
    public void updateTotalHealth()
    {
        int total = 0;
        for (SketchCharacter character : characters) {
            total += character.getHealth();
        }
        
        healthBar.setHealth(total);
    }

    public void handleInput(Input input, double elapsedMillis)
    {
        //don't control dead/turnDone characters
        if(active == null || active.isDead() || active.isTurnDone())
        {
            return;
        }
        MouseCommand comm = null;
        for(Command command : input.getCommands())
        {
            switch(command.getType())
            {
            case FIRE:
                //don't fire more than once in a turn.
                if(!active.hasFiredThisTurn())
                {
                    active.fireCurrentWeapon(1.0f);
                }
                break;
            case AIM_UP:
                active.aimUp(elapsedMillis);
                break;
            case AIM_DOWN:
                active.aimDown(elapsedMillis);
                break;
            case MOUSE_AIM:
                comm = (MouseCommand) command;
                //System.out.println(comm.getX() + ", " + comm.getY());
                active.aimAt(comm.getX(), comm.getY());
                break;
            case MOUSE_FIRE:
                //don't fire more than once in a turn.
                if(!active.hasFiredThisTurn())
                {
                    comm = (MouseCommand) command;
                    active.fireCurrentWeapon(1.0f, comm.getX(), comm.getY());
                }
                break;
            case MOVE_LEFT:
               active.moveLeft(elapsedMillis);
                break;
            case MOVE_RIGHT:
               active.moveRight(elapsedMillis);
                break;
            case STAND:
                active.stand();
                break;
            case JUMP:
                active.jump(elapsedMillis);
                break;
            case SWITCH_1:
                if(weapons.containsKey(WeaponTypes.MELEE_WEAPON))
                {
                    active.setWeapon(weapons.get(WeaponTypes.MELEE_WEAPON));
                }
                break;
            case SWITCH_2:
                if(weapons.containsKey(WeaponTypes.RANGED_WEAPON))
                {
                    active.setWeapon(weapons.get(WeaponTypes.RANGED_WEAPON));
                }
                break;
            case SWITCH_3:
                if(weapons.containsKey(WeaponTypes.BASIC_GRENADE))
                {
                    active.setWeapon(weapons.get(WeaponTypes.BASIC_GRENADE));
                }
                break;
            case SWITCH_4:
                if(weapons.containsKey(WeaponTypes.MINE))
                {
                    active.setWeapon(weapons.get(WeaponTypes.MINE));
                }
                break;
            case SWITCH_5:
                if(weapons.containsKey(WeaponTypes.CLUSTER_BOMB))
                {
                    active.setWeapon(weapons.get(WeaponTypes.CLUSTER_BOMB));
                }
                break;
            case SWITCH_6:
                if(weapons.containsKey(WeaponTypes.BAZOOKA))
                {
                    active.setWeapon(weapons.get(WeaponTypes.BAZOOKA));
                }
                break;
            case SWITCH_7:
                if(weapons.containsKey(WeaponTypes.ERASER))
                {
                    active.setWeapon(weapons.get(WeaponTypes.ERASER));
                }
                break;
            case SWITCH_8:
                if(weapons.containsKey(WeaponTypes.PENCIL))
                {
                    active.setWeapon(weapons.get(WeaponTypes.PENCIL));
                }
                break;
            }
        
        }
    }

    public boolean isDead()
    {
        for(SketchCharacter character : characters)
        {
           // System.out.println(character.isDead());
            if(!character.isDead())
                return false;
        }
        return true;
    }
    
    public int size()
    {
        return characters.size();
    }

    public SketchCharacter getActiveCharacter()
    {
        return active;
    }

    public void cycleActiveCharacter()
    {
        int startIdx = characters.indexOf(active);
        int curIdx = startIdx;
        do
        {
            SketchCharacter previous = characters.get(curIdx);
            previous.setActive(false);
            previous.clearFiredProjectile();
            curIdx = getNextIdx(curIdx);
            SketchCharacter trial = characters.get(curIdx);
            trial.setActive(true);
            if(!trial.isDead())
            {
                setActiveCharacter(trial);
                break;
            }
        } while(curIdx != startIdx);
    }

    private int getNextIdx(int curIdx) 
    {
        if(curIdx == size() - 1)
        {
            return 0;
        }
        else
        {
            return curIdx+1;
        }
    }

    private void setActiveCharacter(SketchCharacter ch)
    {
        AbstractWeapon wep = getActiveWeapon();
        resetCharacters();
        resetWeapons();
        active = ch;
        active.setWeapon(wep);
    }

    private AbstractWeapon getActiveWeapon()
    {
        AbstractWeapon ret = null;
        if(active != null)
        {
            ret = active.getWeapon();
        }
        if(ret == null)
        {
            ret = WeaponTypes.getDefaultWeapon(weapons);
        }
        return ret;
    }

    private void resetCharacters()
    {
        for(SketchCharacter temp : characters)
        {
            temp.resetHasFiredThisTurn();
            temp.setWeapon(null);
        }
    }

    private void resetWeapons()
    {
        for(AbstractWeapon wep : weapons.values())
        {
            wep.resetFire();
        }
    }
}
