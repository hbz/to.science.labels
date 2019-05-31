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
import helper.CrossrefLabelResolver;
import helper.EtikettMaker;

/**
 * @author Jan Schnasse
 *
 */
public class TestCrossrefLabelResolver {
    @Test
    public void test() {
        String label = CrossrefLabelResolver.lookup("http://dx.doi.org/10.13039/501100007766", null);
        Assert.assertEquals("Max-Planck-Institut für Kognitions- und Neurowissenschaften", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://dx.doi.org/10.13039/501100007766", null);
        Assert.assertEquals("Max-Planck-Institut für Kognitions- und Neurowissenschaften", label);
    }
}
