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
package helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class XmlUtils {

    /**
     * @param file
     *            file to store the string in
     * @param str
     *            the string will be stored in file
     * @return a file containing the string
     */
    public static File newStringToFile(File file, String str) {
	try {
	    file.createNewFile();
	    try (FileOutputStream writer = new FileOutputStream(file)) {
		writer.write(str.getBytes("utf-8"));
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	str = null;
	return file;
    }

}
