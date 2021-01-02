package com.app.RemoteDataSource.Deserializer;

import android.util.Log;

import com.app.LocalDataSource.Model.User;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDeserializer implements JsonDeserializer<User> {


    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String id = json.getAsJsonObject().get("id").getAsString();
        String email = json.getAsJsonObject().get("email").getAsString();
        String name = json.getAsJsonObject().get("name").getAsString();
        String bornDate = json.getAsJsonObject().get("bornDate").getAsString();
        String phone = json.getAsJsonObject().get("phone").getAsString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        Date date = null;
        try {
            date = simpleDateFormat.parse(bornDate);
        } catch (ParseException e) {
            Log.v("Exception", e.getLocalizedMessage());
        }

        /*
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        date = calendar.getTime();
        */

        User user = new User(id, email, "", "", name, date.toString(), phone);

        return user;
    }
}
