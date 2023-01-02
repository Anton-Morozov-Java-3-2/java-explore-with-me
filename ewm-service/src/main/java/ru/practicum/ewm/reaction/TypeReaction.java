package ru.practicum.ewm.reaction;

public enum TypeReaction {
    LIKE(1), DISLIKE(-1);

    public final int value;

    TypeReaction(int value) {
        this.value = value;
    }
}
