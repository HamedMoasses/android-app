package eu.h2020.sc.protocol.gson.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import eu.h2020.sc.domain.Price;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class PriceAdapter implements JsonDeserializer<Price>, JsonSerializer<Price> {

    @Override
    public Price deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonAmount = jsonObject.get(Price.AMOUNT);
        JsonElement jsonCurrency = jsonObject.get(Price.CURRENCY);

        BigDecimal amount = jsonAmount.getAsBigDecimal();
        Currency currency = Currency.getInstance(jsonCurrency.getAsString());

        return new Price(amount, currency);
    }

    @Override
    public JsonElement serialize(Price price, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonPrice = new JsonObject();

        jsonPrice.addProperty(Price.AMOUNT, price.getAmount());
        jsonPrice.addProperty(Price.CURRENCY, price.getCurrency().getCurrencyCode());

        return jsonPrice;
    }
}
