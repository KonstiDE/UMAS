package wue.eorc.umas.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GsonTypeTokens {

    public static final Type hashmapToken = new TypeToken<HashMap<String, String>>(){}.getType();

}
