package com.astrog.shootergame.common.messaging.serialization;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SerializationTypes {

    public static final Type stringListType = new TypeToken<List<String>>() {
    }.getType();
}
