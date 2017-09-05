package presidio.webapp.controllers.users;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Patch;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;

import java.util.List;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-04T14:26:45.695Z")

@Api(value = "users", description = "the users API")
public interface UsersApi {

    @ApiOperation(value = "Use this endpoint to get and filters list of users from Presidio", notes = "Users endpoint", response = UsersWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of users and more general data", response = UsersWrapper.class)})
    @RequestMapping(value = "/users",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<UsersWrapper> usersGet(@ApiParam(value = "The maximum number of records to return", required = true, defaultValue = "10") @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize,
                                                  @ApiParam(value = "The number of page, start from 0", required = true) @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                                  @ApiParam(value = "Exact match to user name, use wildcard to search for prefix") @RequestParam(value = "name", required = false) String name,
                                                  @ApiParam(value = "Filtering users which have a score less than the minimum specified in minScore") @RequestParam(value = "minScore", required = false) Integer minScore,
                                                  @ApiParam(value = "Filtering users which have a score higher than the maximum specified in minScore") @RequestParam(value = "maxScore", required = false) Integer maxScore,
                                                  @ApiParam(value = "Comma Seperated List of tags. User should have at least one of the tags. Using '*' says \"all tags\"") @RequestParam(value = "tags", required = false) List<String> tags,
                                                  @ApiParam(value = "The fields to sort by. Sort directions can optionally be appended to the sort key, separated by the ‘:’ character. Sort fieds should be seperated with comma. I.E. sort=field1:ASC,field2:DESC.") @RequestParam(value = "sort", required = false) List<String> sort,
                                                  @ApiParam(value = "Classification of the alert (exact match)") @RequestParam(value = "classification", required = false) List<String> classification,
                                                  @ApiParam(value = "Alert includes indicators with this name (exact match)") @RequestParam(value = "indicatorsType", required = false) List<String> indicatorsType,
                                                  @ApiParam(value = "User Severity", allowableValues = "CRITICAL, HIGH, MEDIUM, LOW") @RequestParam(value = "severity", required = false) String severity,
                                                  @ApiParam(value = "Indicate to search users that param name is prefix to there names.") @RequestParam(value = "isPrefix", required = false) Boolean isPrefix) {
        // do some magic!
        return new ResponseEntity<UsersWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get the alerts of a single user", notes = "Users endpoint", response = AlertsWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AlertsWrapper.class)})
    @RequestMapping(value = "/users/{userId}/alerts",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<AlertsWrapper> usersUserIdAlertsGet(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId) {
        // do some magic!
        return new ResponseEntity<AlertsWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get details about single user", notes = "Users endpoint", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<User> usersUserIdGet(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId) {
        // do some magic!
        return new ResponseEntity<User>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this method to update the user tags", notes = "", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<User> usersUserIdPatch(@ApiParam(value = "Exact match to user name", required = true) @RequestBody List<Patch> patch) {
        // do some magic!
        return new ResponseEntity<User>(HttpStatus.OK);
    }

}
