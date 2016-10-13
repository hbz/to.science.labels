/*Copyright (c) 2015 "hbz"

This file is part of etikett.

etikett is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package controllers;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.message;

/**
 * @author Jan Schnasse
 *
 */
public class MyController extends Controller {
    /**
     * The admin can do everything
     */
    public final static String ADMIN_ROLE = "admin";

    static ObjectMapper mapper = new ObjectMapper();

    private static void setJsonHeader() {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setContentType("application/json; charset=utf-8");
    }

    /**
     * @param obj
     *            an arbitrary object
     * @return json serialization of obj
     */
    public static Result getJsonResult(Object obj) {
        setJsonHeader();
        try {
            return ok(json(obj));
        } catch (Exception e) {
            return internalServerError("Not able to create response!");
        }
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object obj, JsonGenerator jsonGenerator, SerializerProvider sp)
                    throws IOException, JsonProcessingException {
                jsonGenerator.writeFieldName("null");

            }
        });
        return mapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static String json(Object obj) {
        try {
            setJsonHeader();
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer() {
                @Override
                public void serialize(Object obj, JsonGenerator jsonGenerator, SerializerProvider sp)
                        throws IOException, JsonProcessingException {
                    jsonGenerator.writeFieldName("null");

                }
            });
            StringWriter w = new StringWriter();
            mapper.writeValue(w, obj);
            String result = w.toString();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface ApiAction {
        Result exec();
    }

    /**
     * @author Jan Schnasse
     *
     */
    public static class ModifyAction {
        Promise<Result> call(ApiAction ca) {
            return Promise.promise(() -> {
                try {
                    String role = (String) Http.Context.current().args.get("role");
                    if (!modifyingAccessIsAllowed(role)) {
                        return AccessDenied();
                    }
                    return ca.exec();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * @param role
     *            the role of the user
     * @return true if the user is allowed to modify the object
     */
    public static boolean modifyingAccessIsAllowed(String role) {
        if (ADMIN_ROLE.equals(role))
            return true;
        return false;
    }

    /**
     * @return Html or Json Output
     */
    public static Result AccessDenied() {
        return status(401, message.render("Access Denied"));
    }
}
