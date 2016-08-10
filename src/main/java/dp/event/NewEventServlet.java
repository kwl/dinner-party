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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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

// Process the http POST of the form: Add RSVP to dinner party event
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find whom user is
        //TODO get event, add RSVP to it

        String event = "placeholder"; //TODO fix this, get event name from form
        event = getBody(req);

        resp.sendRedirect("/event.jsp?"+event); //TODO update this
    }


    public String getBody(HttpServletRequest request) throws IOException {
        // Read from request
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            System.out.println("input: " + line + "\n");
        }
        return buffer.toString();
    }

} // end class

//[END all]
