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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Etikett;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F.Promise;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.convert;

/**
 * @author Jan Schnasse
 *
 */
public class UnauthorizedCalls extends MyController {

    /**
     * @return http status
     */
    @SuppressWarnings("unchecked")
    public static Promise<Result> postConvert() {
        return Promise.promise(() -> {
            try {
                MultipartFormData body = request().body().asMultipartFormData();
                DynamicForm requestData = Form.form().bindFromRequest();
                String format = requestData.get("format-cb");
                play.Logger.debug(format);
                FilePart data = body.getFile("data");
                String language = requestData.get("lang");
                Collection<Etikett> result = new ArrayList<>();

                if (data != null) {
                    File file = data.getFile();
                    try (FileInputStream uploadData = new FileInputStream(file)) {
                        if ("Rdf-Turtle".equals(format)) {
                            result = Globals.profile.convertRdfData(uploadData, language);
                        } else if ("Json".equals(format)) {
                            result = ((List<Etikett>) new ObjectMapper().readValue(uploadData,
                                    new TypeReference<List<Etikett>>() {
                                    }));
                        } else if ("Json-Context".equals(format)) {
                            result = Globals.profile.convertJsonContextData(
                                    (Map<String, Object>) new ObjectMapper().readValue(uploadData, Map.class));
                        }
                        flash("info", "File uploaded");
                        return ok(json(result));
                    }
                } else {
                    flash("error", "Missing file");
                    return ok(json("Missing file"));
                }
            } catch (Exception e) {
                play.Logger.warn("", e);
                return ok(json(e));
            }
        });
    }

    /**
     * @return a simple upload form for rdf files
     */
    public static Result getConvert() {
        return ok(convert.render());
    }

}
