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
package models;

/**
 * This class is used in ApplicationProfile
 * 
 * @author Jan Schnasse
 *
 */
public class MapEntry {
    /**
     * a label
     */
    public String label = null;
    /**
     * a icon
     */
    public String icon = null;

    /**
     * The name is a short-form for the uri used in JSON-LD
     */
    public String name = null;

    /**
     * the full id as uri
     */
    public String uri = null;

    /**
     * The expected type of the resource
     */
    public String referenceType = null;
}