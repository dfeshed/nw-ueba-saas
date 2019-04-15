package presidio.webapp.controllers.entities;


import fortscale.utils.rest.jsonpatch.JsonPatch;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.model.*;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:25:30.236Z")

@Api(value = "entities")
public interface EntitiesApi {

    @ApiOperation(value = "Use this endpoint to get the alerts of a single entity", notes = "Entities endpoint", response = AlertsWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class)})
    @RequestMapping(value = "/entities/{entityId}/alerts",
            produces = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<AlertsWrapper> getAlertsByEntity(@ApiParam(value = "The UUID of the entity to return", required = true) @PathVariable("entityId") String entityId,
                                                          @ApiParam(value = "object that holds all the parameters for getting alerts") EntityAlertsQuery body) {
        // do some magic!
        return new ResponseEntity<AlertsWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get details about a single entity", notes = "Entities endpoint", response = Entity.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single entity", response = Entity.class)})
    @RequestMapping(value = "/entities/{entityId}",
            produces = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<Entity> getEntity(@ApiParam(value = "The UUID of the entity to return", required = true) @PathVariable("entityId") String entityId,
                                         @ApiParam(value = "Expand response to get the entity alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        // do some magic!
        return new ResponseEntity<Entity>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get a filtered list of entities from Presidio", notes = "Entities endpoint", response = EntitiesWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of entities and generic data", response = EntitiesWrapper.class)})
    @RequestMapping(value = "/entities",
            produces = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<EntitiesWrapper> getEntities(@ApiParam(value = "object that holds all the parameters for getting specific alerts") EntityQuery entityQuery) {
        // do some magic!
        return new ResponseEntity<EntitiesWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this method to update the entity tags", notes = "", response = Entity.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single entity", response = Entity.class)})
    @RequestMapping(value = "/entities/{entityId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<Entity> updateEntity(@ApiParam(value = "Exact match to the entity id", required = true) @PathVariable("entityId") String entityId, @RequestBody JsonPatch jsonPatch) {
        // do some magic!
        return new ResponseEntity<Entity>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to update entities by filter", notes = "Entities endpoint", response = EntitiesWrapper.class, tags = {"entities",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of updated entities", response = EntitiesWrapper.class)})
    @RequestMapping(value = "/entities",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<EntitiesWrapper> updateEntities(@ApiParam(value = "object that holds all the parameters for getting specific entities") @RequestBody EntityPatchBody entityPatchBody) {
        // do some magic!
        return new ResponseEntity<EntitiesWrapper>(HttpStatus.OK);
    }
}

