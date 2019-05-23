package presidio.webapp.controllers.entities;


import fortscale.utils.rest.jsonpatch.JsonPatch;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.model.*;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:25:30.236Z")

@Api(value = "entities")
public interface EntitiesApi {

    @ApiOperation(value = "Use this endpoint to get the alerts of a single entity", notes = "Entities endpoint", response = AlertsWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class)})
    @RequestMapping(value = "/entities/{entityDocumentId}/alerts",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<AlertsWrapper> getAlertsByEntity(@ApiParam(value = "The UUID of the entity to return", required = true) @PathVariable("entityDocumentId") String entityDocumentId,
                                                          @ApiParam(value = "object that holds all the parameters for getting alerts") EntityAlertsQuery body);

    @ApiOperation(value = "Use this endpoint to get details about a single entity", notes = "Entities endpoint", response = Entity.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single entity", response = Entity.class)})
    @RequestMapping(value = "/entities/{entityDocumentId}",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<Entity> getEntity(@ApiParam(value = "The UUID of the entity to return", required = true) @PathVariable("entityDocumentId") String entityDocumentId,
                                         @ApiParam(value = "Expand response to get the entity alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand);


    @ApiOperation(value = "Use this endpoint to get a filtered list of entities from Presidio", notes = "Entities endpoint", response = EntitiesWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of entities and generic data", response = EntitiesWrapper.class)})
    @RequestMapping(value = "/entities",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<EntitiesWrapper> getEntities(@ApiParam(value = "object that holds all the parameters for getting specific alerts") EntityQuery entityQuery);


    @ApiOperation(value = "Use this method to update the entity tags", notes = "", response = Entity.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single entity", response = Entity.class)})
    @RequestMapping(value = "/entities/{entityDocumentId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    ResponseEntity<Entity> updateEntity(@ApiParam(value = "Exact match to the entity UUID", required = true) @PathVariable("entityDocumentId") String entityDocumentId, @RequestBody JsonPatch jsonPatch);


    @ApiOperation(value = "Use this endpoint to update entities by filter", notes = "Entities endpoint", response = EntitiesWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of updated entities", response = EntitiesWrapper.class)})
    @RequestMapping(value = "/entities",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    ResponseEntity<EntitiesWrapper> updateEntities(@ApiParam(value = "object that holds all the parameters for getting specific entities") @RequestBody EntityPatchBody entityPatchBody);


    @ApiOperation(value = "Use this endpoint to get the alerts of a single user", notes = "Users endpoint", response = AlertsWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class)})
    @RequestMapping(value = "/users/{userId}/alerts",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<AlertsWrapper> getAlertsByUser(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId,
                                                          @ApiParam(value = "object that hold all the parameters for getting alerts") EntityAlertsQuery body);

    @ApiOperation(value = "Use this endpoint to get details about single user", notes = "Users endpoint", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single user", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<User> getUser(@ApiParam(value = "The UUID of the user to return", required = true) @PathVariable("userId") String userId,
                                         @ApiParam(value = "Expand response to get user alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand);


    @ApiOperation(value = "Use this endpoint to get and filters list of users from Presidio", notes = "Users endpoint", response = UsersWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of users and more general data", response = UsersWrapper.class)})
    @RequestMapping(value = "/users",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<UsersWrapper> getUsers(@ApiParam(value = "object that hold all the parameters for getting specific alerts") UserQuery userQuery);


    @ApiOperation(value = "Use this method to update the user tags", notes = "", response = User.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single user", response = User.class)})
    @RequestMapping(value = "/users/{userId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    ResponseEntity<User> updateUser(@ApiParam(value = "Exact match to user name", required = true) @PathVariable("userId") String userId, @RequestBody JsonPatch jsonPatch);


    @ApiOperation(value = "Use this endpoint to update users by filter", notes = "Users endpoint", response = UsersWrapper.class, tags = {"users",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of updated users", response = UsersWrapper.class)})
    @RequestMapping(value = "/users",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    ResponseEntity<UsersWrapper> updateUsers(@ApiParam(value = "object that hold all the parameters for getting specific users") @RequestBody UserPatchBody userPatchBody);
}

