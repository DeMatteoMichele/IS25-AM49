package it.polimi.ingsw.is25am49.server.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;

import java.util.ArrayList;

/**
 * Represents an abstract base class for spacecraft components.
 * Defines shared behavior and properties for all component types.
 * This is a sealed class with permitted subclasses: BasicComponent, ContainerComponent, and SpecialComponent.
 */
public sealed abstract class  Component permits BasicComponent, ContainerComponent, SpecialComponent {
    /**
     * Defines the specific type of the spacecraft component.
     * Represents a constant value from the {@link ComponentType} enumeration.
     * Used to determine the behavior and properties associated with the component.
     */
    final protected ComponentType type;
    /**
     * Represents the level of the component defining its capacity, power, or rank.
     * It directly impacts various computations such as firepower or engine power.
     */
    final protected int level;

    /**
     * Constructs a new Component with the specified type and level.
     *
     * @param type the type of the component, representing its functional category
     * @param level the level of the component, representing its effectiveness or capability
     */
    public Component(ComponentType type, int level) {
        this.type = type;
        this.level = level;
    }

    /**
     * Retrieves the level of the component.
     *
     * @return the level of the component as an integer.
     */
    public int getLevel(){
        return level;
    }
    /**
     * Retrieves the type of the component.
     *
     * @return the {@code ComponentType} associated with this component.
     */
    public ComponentType getType(){return type;}
    /**
     * Computes the firepower of the component based on its type and level.
     * The firepower is determined for specific component types such as CANNON and DOUBLE_CANNON;
     * for other types, the firepower is 0.
     *
     * @return the firepower value, which is equal to the level for CANNON and DOUBLE_CANNON types, or 0 for other types.
     */
    public int getFirePower(){
        return switch (type){
            case CANNON,DOUBLE_CANNON -> level;
            default -> 0;
        };
    }
    /**
     * Retrieves the engine power of the component based on its type and level.
     * Returns a non-zero value only if the component type is ENGINE or DOUBLE_ENGINE.
     *
     * @return the engine power of the component; 0 if the type is not ENGINE or DOUBLE_ENGINE.
     */
    public int getEnginePower(){
        return switch (type){
            case ENGINE,DOUBLE_ENGINE -> level;
            default -> 0;
        };
    }

    /**
     * Retrieves the list of tokens currently associated with the component.
     *
     * @return an ArrayList of Token objects representing the tokens in the component.
     */
    public abstract ArrayList<Token> getTokenList();
    /**
     * Returns the total number of tokens present in the component.
     *
     * @return the count of tokens in the component
     */
    public abstract int getNumberOfTokens();
    /**
     * Calculates the number of occurrences of a specific token in the component's token list.
     *
     * @param token the specific token to count within the component.
     * @return the number of occurrences of the specified token.
     */
    public abstract int getNumberOfaToken(Token token);
    /**
     * Validates if the given token is acceptable for the component.
     *
     * @param token the token to be validated
     * @return true if the token is valid for the component, false otherwise
     */
    public abstract boolean checkIfTokenIsValid(Token token);
    /**
     * Adds a specified token to the component's token list.
     *
     * @param token the token to be added; must be a valid instance of the Token enum
     */
    public abstract void addToken(Token token);
    /**
     * Removes a specific token from the component.
     *
     * @param token the token to be removed from the component
     */
    public abstract void removeToken(Token token);
    /**
     * Removes a default token from the component.
     * Specific behavior depends on the subclass implementation.
     */
    public abstract void removeToken();
    /**
     * Fills the component with the specified token.
     *
     * @param token the token to fill the component with; must be a valid type for the component.
     */
    public abstract void fillComponent(Token token);
    /**
     * Clears all state and associated tokens from the component.
     * Resets the component to an empty or uninitialized state.
     */
    public abstract void clearComponent();


    /**
     * Determines whether the component is currently active.
     *
     * @return true if the component is active, false otherwise
     */
    public abstract boolean getIsActive();
    /**
     * Activates the component, transitioning it to an operational state.
     * Intended to be implemented by subclasses to define the activation behavior.
     */
    public abstract void turnOn();
    /**
     * Deactivates the component, setting its state to inactive.
     * Implementation details are defined in subclasses.
     */
    public abstract void turnOff();
}
