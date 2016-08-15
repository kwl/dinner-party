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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import dp.util.ActionUtil;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Form Handling Servlet
 */
public class RSVPServlet extends HttpServlet {

// Process the http POST of the form: Add RSVP to dinner party event
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();  // Find whom user is

    // Add "bringing" RSVP to correct guest & event
    // Transaction for retrieving and updating Guest
    String bringing = req.getParameter("bring");
    String comment = req.getParameter("comment");
    String eventKeyStr = req.getParameter("eventKey");
    Key eventKey = KeyFactory.stringToKey(eventKeyStr);
    Entity guest;
    Transaction txn = datastore.beginTransaction();
    try {
      Query q = new Query("Guest", eventKey).setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getEmail()));
        Iterable<Entity> guests = datastore.prepare(q).asIterable();
        guest = guests.iterator().next();
        // Entity guest = datastore.prepare(txn, q).asSingleEntity();
        if (bringing.length() > 0) guest.setProperty("bringing", bringing);
        if (comment.length() > 0) guest.setProperty("comment", comment);
        datastore.put(txn, guest);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }


    //resp.sendRedirect("/parties.jsp"); //TODO update this
    ActionUtil.gotoEvent(this, resp, req.getParameter("eventKey"), req.getParameter("eventName"));
  }

}

//[END all]
