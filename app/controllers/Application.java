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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.rio.RDFFormat;

import models.MapEntry;
import play.libs.F.Promise;
import play.mvc.Call;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.*;

/**
 * @author Jan Schnasse
 *
 */
public class Application extends MyController {

    /**
     * @param urlAddress
     *            if not null provides just the element with the passed url
     * @return a list of entries of this application profile
     */
    public static Promise<Result> index() {
	return Promise.promise(() -> {
	    if (request().accepts("text/html")) {
		return asHtml(null);
	    } else {
		return asJson(null);
	    }
	});
    }

    public static Result row(String urlAddress) {

	if (request().accepts("text/html")) {
	    return asHtml(urlAddress);
	} else {
	    return asJson(urlAddress);
	}

    }

    /**
     * @param urlAddress
     *            a url address
     * @param colum
     *            the name of the colum
     * @return a string with the content of the colum
     */
    public static Promise<Result> getColum(String urlAddress, String colum) {
	return Promise.promise(() -> {
	    if (colum == null) {
		return row(urlAddress);
	    }
	    response().setHeader("Content-Type", "text/plain; charset=utf-8");
	    if (colum != null && !colum.isEmpty() && urlAddress != null) {
		MapEntry entry = Globals.profile.pMap.get(urlAddress);
		switch (colum) {
		case "Icon":
		    return ok(entry.icon);
		case "Label":
		    return ok(entry.label);
		case "Name":
		    return ok(entry.name);
		case "Uri":
		    return ok(entry.uri);
		case "RefType":
		    return ok(entry.referenceType);
		}
		return ok(entry.label);
	    }
	    return status(500);
	});
    }

    /**
     * @param urlAddress
     * @return all data as json array
     */
    public static Result asJson(String urlAddress) {
	try {
	    response().setHeader("Content-Type",
		    "application/json; charset=utf-8");

	    if (urlAddress != null) {
		MapEntry entry = Globals.profile.pMap.get(urlAddress);
		ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		result.add(entry);
		return ok(json(result));
	    } else {
		return ok(json(new ArrayList<MapEntry>(
			Globals.profile.nMap.values())));
	    }

	} catch (Exception e) {
	    play.Logger.debug("", e);
	    return status(500, json(e.toString()));
	}

    }

    private static Result asHtml(String urlAddress) {
	try {
	    response().setHeader("Content-Type", "text/html; charset=utf-8");
	    if (urlAddress != null) {
		MapEntry entry = Globals.profile.pMap.get(urlAddress);
		ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		result.add(entry);
		return ok(index.render(result));
	    } else {
		return ok(index.render(new ArrayList<MapEntry>(
			Globals.profile.pMap.values())));
	    }
	} catch (Exception e) {
	    play.Logger.debug("", e);
	    return status(
		    500,
		    message.render("Server encounters internal problem: "
			    + e.getMessage()));
	}
    }

    /**
     * @param url
     *            a string to apply the urlencoding to
     * @return a url encoded string for the passed url argument
     * @throws UnsupportedEncodingException
     *             if string is not a url and not null
     */
    @SuppressWarnings("javadoc")
    public static String EncodeURL(final String url)
	    throws java.io.UnsupportedEncodingException {
	if (url == null)
	    return "null";
	return java.net.URLEncoder.encode(url, "UTF-8");
    }

    /**
     * @param call
     *            a play call to encode
     * @return the encoded call
     * @throws UnsupportedEncodingException
     *             if string is not a url
     */
    @SuppressWarnings("javadoc")
    public static String EncodeURL(Call call)
	    throws java.io.UnsupportedEncodingException {
	return EncodeURL(call.toString());
    }

    /**
     * @return http status
     */
    public static Promise<Result> addSkosData() {
	return Promise
		.promise(() -> {
		    try {
			MultipartFormData body = request().body()
				.asMultipartFormData();
			FilePart data = body.getFile("data");
			if (data != null) {
			    String fileName = data.getFilename();
			    String contentType = data.getContentType();
			    File file = data.getFile();
			    try (FileInputStream uploadData = new FileInputStream(
				    file)) {
				Globals.profile.loadToMap(uploadData);

				flash("info", "File uploaded");
				return redirect(routes.Application.index());
			    }
			} else {
			    flash("error", "Missing file");
			    return redirect(routes.Application.index());
			}
		    } catch (Exception e) {
			return redirect(routes.Application.index());
		    }
		});
    }

    public static Result upload() {
	return ok(upload.render());
    }

    public static Promise<Result> asJsonLdContext() {
	return Promise.promise(() -> {
	    try {
		List<MapEntry> ls = new ArrayList<MapEntry>(
			Globals.profile.nMap.values());
		Map<String, Object> pmap;
		Map<String, Object> cmap = new HashMap<String, Object>();
		for (MapEntry l : ls) {
		    pmap = new HashMap<String, Object>();
		    pmap.put("@id", l.uri);
		    pmap.put("label", l.label);
		    if (l.referenceType != null) {
			pmap.put("@type", l.referenceType);
		    }
		    cmap.put(l.name, pmap);
		}
		return ok(json(cmap));
	    } catch (Exception e) {
		play.Logger.warn("", e);
		return redirect(routes.Application.index());
	    }
	});
    }
}
