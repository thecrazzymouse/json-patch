package net.riotopsys.json_patch.operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.riotopsys.json_patch.JsonPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoveOperation extends AbsOperation{

    public final JsonPath from;

    public MoveOperation(JsonPath from, JsonPath path) {
        this.path = path;
        this.from = from;
    }

    @Override
    public String getOperationName() {
        return "move";
    }

    @Override
    public JsonElement apply(JsonElement original) {
        JsonElement result = duplicate( original );

        JsonElement value = from.navigate(result);

        JsonElement source = from.head().navigate(result);
        JsonElement destination = path.head().navigate(result);

        if ( source.isJsonObject() ){
            source.getAsJsonObject().remove(from.tail());
        } else if ( source.isJsonArray() ){
            JsonArray array = source.getAsJsonArray();

            int index = (from.tail().equals("-")) ? array.size() : Integer.valueOf(from.tail());

            array.remove(index);
        }

        if ( destination.isJsonObject() ){
            destination.getAsJsonObject().add(path.tail(),value);
        } else if ( destination.isJsonArray() ){

            JsonArray array = destination.getAsJsonArray();

            int index = (path.tail().equals("-")) ? array.size() : Integer.valueOf(path.tail());

            List<JsonElement> temp = new ArrayList<JsonElement>();

            Iterator<JsonElement> iter = array.iterator();
            while (iter.hasNext()){
                JsonElement stuff = iter.next();
                iter.remove();
                temp.add( stuff );
            }

            temp.add(index, value);

            for ( JsonElement stuff: temp ){
                array.add(stuff);
            }
        }

        return result;
    }

}
