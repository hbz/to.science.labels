/*
 * Copyright 2015 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package controllers;

import controllers.MyController;
import play.Play;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class HardCodedUser implements User {
    String role = null;

    /**
     * Creates a new HardCoded-User and checks if passwords are set
     * 
     */
    public HardCodedUser() {
	String adminPwd;
	adminPwd = Play.application().configuration()
		.getString("etikett.admin-password");

	if (adminPwd == null)
	    throw new RuntimeException(
		    "Please set passwords for all roles in application.conf");
    }

    @Override
    public User authenticate(String username, String password) {
	role = null;
	if (MyController.ADMIN_ROLE.equals(username)
		&& password.equals(Play.application().configuration()
			.getString("etikett.admin-password"))) {
	    role = MyController.ADMIN_ROLE;
	}
	if (role == null)
	    throw new RuntimeException("No valid credentials!");
	play.Logger.debug("You are authorized with role " + role);
	return this;
    }

    @Override
    public String getRole() {
	return role;
    }

    @Override
    public void setRole(String role) {
	this.role = role;
    }
}
