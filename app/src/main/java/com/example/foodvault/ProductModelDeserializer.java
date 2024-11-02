package com.example.foodvault;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductModelDeserializer implements JsonDeserializer<ProductModel> {

    @Override
    public ProductModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();

        // Parsing fields
        Integer productId = jsonObject.has("product_id") && !jsonObject.get("product_id").isJsonNull() 
                            ? jsonObject.get("product_id").getAsInt() : null;

        Integer userId = jsonObject.has("user_id") && !jsonObject.get("user_id").isJsonNull() 
                         ? jsonObject.get("user_id").getAsInt() : null;

        Integer locationId = jsonObject.has("location_id") && !jsonObject.get("location_id").isJsonNull() 
                             ? jsonObject.get("location_id").getAsInt() : null;

        String productName = jsonObject.has("product_name") && !jsonObject.get("product_name").isJsonNull() 
                             ? jsonObject.get("product_name").getAsString() : null;

        // Handling product_barcode as a String or can be null
        String productBarcode = jsonObject.has("product_barcode") && !jsonObject.get("product_barcode").isJsonNull() 
                                ? jsonObject.get("product_barcode").getAsString() : null;

        // Parsing Date field (assuming the date is in yyyy-MM-dd format in JSON)
        Date productExpirationDate = null;
        if (jsonObject.has("product_expiration_date") && !jsonObject.get("product_expiration_date").isJsonNull()) {
            String dateString = jsonObject.get("product_expiration_date").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format based on your needs
            try {
                productExpirationDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                throw new JsonParseException("Failed to parse product expiration date: " + dateString, e);
            }
        }

        String productCategory = jsonObject.has("product_category") && !jsonObject.get("product_category").isJsonNull() 
                                 ? jsonObject.get("product_category").getAsString() : null;

        boolean productExpired = jsonObject.has("product_expired") && !jsonObject.get("product_expired").isJsonNull() 
                                 ? jsonObject.get("product_expired").getAsBoolean() : false;

        int quantity = jsonObject.has("quantity") && !jsonObject.get("quantity").isJsonNull() 
                       ? jsonObject.get("quantity").getAsInt() : 0;

        // Create and return the ProductModel object
        ProductModel product = new ProductModel();


        product.setProductId(productId);
        product.setUserIdForProduct(userId);
        product.setLocationId(locationId);
        product.setProductName(productName);
        product.setProductExpirationDate(productExpirationDate);
        product.setProductCategory(productCategory);
        product.setProductExpired(productExpired);
        product.setProductQuantity(quantity);


        return product;
    }
}
