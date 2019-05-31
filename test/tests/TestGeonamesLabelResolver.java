/*Copyright (c) 2019 "hbz"

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
package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.GeonamesLabelResolver;

/**
 * @author Jan Schnasse
 *
 */
public class TestGeonamesLabelResolver {
    @Test
    public void test() {
        String label = GeonamesLabelResolver.lookup("http://www.geonames.org/3369157", null);
        Assert.assertEquals("Cape Town", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://www.geonames.org/3369157", "en");
        Assert.assertEquals("Cape Town", label);
    }
}
