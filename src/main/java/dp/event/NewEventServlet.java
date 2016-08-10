/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//[START all]
package dp.event;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import dp.guests.AddGuestServlet;
import dp.util.ActionUtil;

import java.io.BufferedReader;
import java.io.IOException;

import java.lang.StringBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Form Handling Servlet
 */
public class NewEventServlet extends HttpServlet {

    // Create a new event with given name
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find whom user is

        // Put new event into datastore
        Entity event = new Entity("Event");
        String eventName = req.getParameter("eventName");
        event.setProperty("name", eventName);
        event.setProperty("host", user);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(event);

        //TODO add event to user (or user to event as owner)?
        AddGuestServlet.addGuest(event.getKey(), user.getEmail());

        //String eventName = getBody(req);

        //resp.sendRedirect("/event.jsp?eventName="+eventName); //TODO update this
        ActionUtil.gotoEvent(this, resp, KeyFactory.keyToString(event.getKey()));
    }


    /**
     * Utility method to get text from a form's POST request
     */
    public String getBody(HttpServletRequest request) throws IOException {
        // Read from request
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

} // end class

//[END all]
