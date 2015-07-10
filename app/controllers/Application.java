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

import java.util.ArrayList;

import models.MapEntry;
import play.libs.F.Promise;
import play.mvc.Call;
import play.mvc.Result;
import views.html.index;
import views.html.message;

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
    public static Promise<Result> index(String urlAddress) {
	return Promise.promise(() -> {
	    if (request().accepts("text/html")) {
		return asHtml(urlAddress);
	    } else {
		return asJson(urlAddress);
	    }

	});
    }

    private static Result asJson(String urlAddress) {
	try {
	    response().setHeader("Content-Type", "application/json");
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
	    response().setHeader("Content-Type", "text/html");
	    if (urlAddress != null) {
		MapEntry entry = Globals.profile.pMap.get(urlAddress);
		ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		result.add(entry);
		return ok(index.render(result));
	    } else {
		return ok(index.render(new ArrayList<MapEntry>(
			Globals.profile.nMap.values())));
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

}
