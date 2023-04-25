package com.astrog.shootergame.common.messaging.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.lang.reflect.Type;

public class ObjectToStringSerializer {

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Point2D.class, new PointTypeAdapter())
        .create();

    public static <T> T deserialize(String s, Class<T> clazz) {
        return gson.fromJson(s, clazz);
    }

    public static <T> T deserialize(String s, Type clazz) {
        return gson.fromJson(s, clazz);
    }

    public static <T> String serialize(T obj) {
        return gson.toJson(obj);
    }
}

class PointTypeAdapter extends TypeAdapter<Point2D> {

    @Override
    public void write(JsonWriter out, Point2D value) throws IOException {
        String xy = value.getX() + "," + value.getY();
        out.value(xy);
    }

    @Override
    public Point2D read(JsonReader in) throws IOException {
        String xy = in.nextString();
        String[] parts = xy.split(",");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        return new Point2D(x, y);
    }
}