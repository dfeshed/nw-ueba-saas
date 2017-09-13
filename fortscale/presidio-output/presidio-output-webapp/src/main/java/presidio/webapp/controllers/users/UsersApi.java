package presidio.webapp.controllers.users;


import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.model.*;

import java.util.List;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:25:30.236Z")

@Api(value = "users", description = "the users API")
public interface UsersApi {

    @ApiOperation(value = "Use this endpoint to get the alerts of a single user", notes = "Users endpoint", response = AlertsWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class)})
    @RequestMapping(value = "/users/{userId}/alerts",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<AlertsWrapper> getAlertsByUser(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId,
                                                          @ApiParam(value = "object that hold all the parameters for getting alerts") @RequestBody UserAlertsQuery body) {
        // do some magic!
        return new ResponseEntity<AlertsWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get details about single user", notes = "Users endpoint", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single user", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<User> getUser(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId,
                                         @ApiParam(value = "Expand response to get user alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        // do some magic!
        return new ResponseEntity<User>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get and filters list of users from Presidio", notes = "Users endpoint", response = UsersWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of users and more general data", response = UsersWrapper.class)})
    @RequestMapping(value = "/users",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<UsersWrapper> getUsers(@ApiParam(value = "object that hold all the parameters for getting specific alerts") @RequestBody UserQuery userQuery) {
        // do some magic!
        return new ResponseEntity<UsersWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this method to update the user tags", notes = "", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single user", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<User> updateUser(@ApiParam(value = "Exact match to user name", required = true) @RequestBody List<Patch> patch) {
        // do some magic!
        return new ResponseEntity<User>(HttpStatus.OK);
    }

}
